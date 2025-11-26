import { useState, useCallback } from 'react';
import { AddressWithValidationSchema } from '@/features/orders/types';
import type { Address } from '@/features/orders/types';

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
      
      if (field === 'country' && value !== prev.country) {
        newData.stateProvince = '';
      }
      
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
      const serverResult = await onServerValidate(formData);
      
      if (serverResult.validated) {
        setValidationStatus('validated');
        if (serverResult.suggestedAddress) {
          setFormData(serverResult.suggestedAddress);
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
