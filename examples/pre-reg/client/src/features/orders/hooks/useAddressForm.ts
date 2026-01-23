import { useState, useCallback } from 'react';
import { AddressWithValidationSchema } from '@/features/orders/types';
import type { Address } from '@/features/orders/types';

/**
 * Region/state normalization for address validation API.
 * The API requires 2-letter region codes if country_code_2 is "US" or "CA".
 * These mappings convert full names to abbreviations.
 */

// US States mapping
const US_STATES: Record<string, string> = {
  'alabama': 'AL', 'alaska': 'AK', 'arizona': 'AZ', 'arkansas': 'AR',
  'california': 'CA', 'colorado': 'CO', 'connecticut': 'CT', 'delaware': 'DE',
  'florida': 'FL', 'georgia': 'GA', 'hawaii': 'HI', 'idaho': 'ID',
  'illinois': 'IL', 'indiana': 'IN', 'iowa': 'IA', 'kansas': 'KS',
  'kentucky': 'KY', 'louisiana': 'LA', 'maine': 'ME', 'maryland': 'MD',
  'massachusetts': 'MA', 'michigan': 'MI', 'minnesota': 'MN', 'mississippi': 'MS',
  'missouri': 'MO', 'montana': 'MT', 'nebraska': 'NE', 'nevada': 'NV',
  'new hampshire': 'NH', 'new jersey': 'NJ', 'new mexico': 'NM', 'new york': 'NY',
  'north carolina': 'NC', 'north dakota': 'ND', 'ohio': 'OH', 'oklahoma': 'OK',
  'oregon': 'OR', 'pennsylvania': 'PA', 'rhode island': 'RI', 'south carolina': 'SC',
  'south dakota': 'SD', 'tennessee': 'TN', 'texas': 'TX', 'utah': 'UT',
  'vermont': 'VT', 'virginia': 'VA', 'washington': 'WA', 'west virginia': 'WV',
  'wisconsin': 'WI', 'wyoming': 'WY', 'district of columbia': 'DC',
};

// Canadian Provinces mapping
const CA_PROVINCES: Record<string, string> = {
  'alberta': 'AB', 'british columbia': 'BC', 'manitoba': 'MB',
  'new brunswick': 'NB', 'newfoundland and labrador': 'NL', 'newfoundland': 'NL',
  'northwest territories': 'NT', 'nova scotia': 'NS', 'nunavut': 'NU',
  'ontario': 'ON', 'prince edward island': 'PE', 'quebec': 'QC',
  'saskatchewan': 'SK', 'yukon': 'YT',
};

/**
 * Converts state/province name to 2-letter abbreviation.
 * Required for US and CA addresses per API specification.
 */
const normalizeStateProvince = (state: string, country: string): string => {
  const normalized = state.trim().toLowerCase();
  
  // If already a 2-letter code, return uppercase
  if (normalized.length === 2) return state.toUpperCase();
  
  if (country === 'US') {
    return US_STATES[normalized] || state;
  }
  
  if (country === 'CA') {
    return CA_PROVINCES[normalized] || state;
  }
  
  return state;
};

export type ValidationStatus = 'idle' | 'validating' | 'validated' | 'error';

export interface UseAddressFormOptions {
  initialAddress: Address | null;
  onAddressChange: (address: Address) => void;
  onServerValidate: (address: Address) => Promise<{
    validated: boolean;
    errors?: string[];
    suggestedAddress?: Address;
  }>;
}

export interface UseAddressFormReturn {
  formData: Address;
  fieldErrors: Partial<Record<keyof Address, string>>;
  validationStatus: ValidationStatus;
  validationErrors: string[];
  setFieldValue: (field: keyof Address, value: string) => void;
  validate: () => Promise<boolean>;
  isComplete: () => boolean;
  reset: () => void;
}

const EMPTY_ADDRESS: Address = {
  firstName: '', lastName: '', addressLine1: '', addressLine2: '',
  city: '', stateProvince: '', postalCode: '', country: '', phone: '',
};

export const useAddressForm = ({ 
  initialAddress, 
  onAddressChange, 
  onServerValidate 
}: UseAddressFormOptions): UseAddressFormReturn => {
  const [formData, setFormData] = useState<Address>(initialAddress || EMPTY_ADDRESS);

  const [validationStatus, setValidationStatus] = useState<ValidationStatus>('idle');
  const [validationErrors, setValidationErrors] = useState<string[]>([]);
  
  const [fieldErrors, setFieldErrors] = useState<Partial<Record<keyof Address, string>>>({});

  const setFieldValue = useCallback((field: keyof Address, value: string) => {
    setFormData(prev => {
      const newData = { ...prev, [field]: value };
      
      onAddressChange(newData);
      return newData;
    });

    setFieldErrors(prev => {
      if (!prev[field]) return prev;
      const newErrors = { ...prev };
      delete newErrors[field];
      return newErrors;
    });

    setValidationStatus(status => 
      (status === 'validated' || status === 'error') ? 'idle' : status
    );
    setValidationErrors([]);
  }, [onAddressChange]);

  const validate = useCallback(async () => {
    setValidationStatus('validating');
    setValidationErrors([]);
    setFieldErrors({});

    const result = AddressWithValidationSchema.safeParse(formData);

    if (!result.success) {
      const newFieldErrors: Partial<Record<keyof Address, string>> = {};
      result.error.issues.forEach((issue) => {
        if (issue.path.length > 0) {
          const key = issue.path[0] as keyof Address;
          newFieldErrors[key] = issue.message;
        }
      });
      setFieldErrors(newFieldErrors);
      setValidationStatus('error');
      setValidationErrors(['Please correct the errors highlighted below.']);
      return false;
    }

    try {
      // Normalize state/province before sending to server (e.g., "Texas" -> "TX")
      const normalizedData = {
        ...formData,
        stateProvince: normalizeStateProvince(formData.stateProvince, formData.country),
      };
      const serverResult = await onServerValidate(normalizedData);
      
      if (serverResult.validated) {
        setValidationStatus('validated');
        // If API suggests address corrections (e.g., typo fixes), apply those
        // but preserve the user's original state/province format for display
        if (serverResult.suggestedAddress) {
          const displayAddress = {
            ...serverResult.suggestedAddress,
            // Keep user's original input for state/province (they typed "Texas", not "TX")
            stateProvince: formData.stateProvince,
          };
          setFormData(displayAddress);
          onAddressChange(displayAddress);
        }
        return true;
      } else {
        setValidationStatus('error');
        setValidationErrors(serverResult.errors && serverResult.errors.length > 0 
          ? serverResult.errors 
          : ['Address validation failed.']
        );
        return false;
      }
    } catch (error) {
      setValidationStatus('error');
      setValidationErrors(['Unable to validate address. Please check your connection.']);
      return false;
    }
  }, [formData, onServerValidate]);

  const isComplete = useCallback(() => {
    return !!(
      formData.firstName && formData.lastName && formData.addressLine1 &&
      formData.city && formData.stateProvince && formData.postalCode &&
      formData.country && formData.phone
    );
  }, [formData]);

  const reset = useCallback(() => {
    setFormData(EMPTY_ADDRESS);
    setValidationStatus('idle');
    setValidationErrors([]);
    setFieldErrors({});
  }, []);

  return {
    formData,
    fieldErrors,
    validationStatus,
    validationErrors,
    setFieldValue,
    validate,
    isComplete,
    reset,
  };
};
