import React, { useState, useEffect } from 'react';
import {
  Box,
  TextField,
  Button,
  Typography,
  MenuItem,
  Alert,
  CircularProgress,
  Chip,
  Stack,
  IconButton,
  Collapse,
} from '@mui/material';
import { ChevronDown, ChevronUp, Check, X } from 'lucide-react';
import Grid from '@mui/material/Grid';
import { Address} from '@/types/api';

interface DeliveryType {
  value: number;
  name: string;
}

interface Country {
  country_id: number;
  country_name: string;
  country_code_2: string;
  country_code_3: string;
  country_vat_rate: number;
  delivery_types: DeliveryType[];
  states?: { code: string; name: string }[];
}

interface CountriesResponse {
  count: number;
  total_count: number;
  countries: Country[];
}

export type { CountriesResponse };
import { useQuery } from '@tanstack/react-query';

interface AddressFormProps {
  address: Address | null;
  onAddressChange: (address: Address) => void;
  onValidate: (address: Address) => Promise<{ validated: boolean; errors?: string[]; suggestedAddress?: any }>;
  onNext: () => void;
  onBack: () => void;
  getCountries: () => Promise<CountriesResponse>;
}

export const AddressForm: React.FC<AddressFormProps> = ({
  address,
  onAddressChange,
  onValidate,
  onNext,
  onBack,
  getCountries,
}) => {
  const [formData, setFormData] = useState<Address>(
    address || {
      firstName: '',
      lastName: '',
      addressLine1: '',
      addressLine2: '',
      city: '',
      stateProvince: '',
      postalCode: '',
      country: '', // Start with empty string until countries load
      phone: '',
    }
  );

  const [showAddressLine2, setShowAddressLine2] = useState(!!formData.addressLine2);
  const [validationStatus, setValidationStatus] = useState<'idle' | 'validating' | 'validated' | 'error'>('idle');
  const [validationErrors, setValidationErrors] = useState<string[]>([]);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const { data: countriesData = undefined, isLoading: countriesLoading } = useQuery<CountriesResponse>({
    queryKey: ['countries'],
    queryFn: getCountries,
  });
  const countries: Country[] = countriesData?.countries ?? [];

  // Set default country once countries are loaded
  useEffect(() => {
    if (countries.length > 0 && !formData.country) {
      const defaultCountry = countries.find(c => c.country_code_2 === 'US') || countries[0];
      setFormData(prev => ({ ...prev, country: defaultCountry.country_code_2 }));
      onAddressChange({ ...formData, country: defaultCountry.country_code_2 });
    }
  }, [countries]);

  const selectedCountry = countries.find(c => c.country_code_2 === formData.country);
  // If you have states in the API, map them here. Otherwise, keep as empty array.
  const states = selectedCountry?.states || [];

  const handleFieldChange = (field: keyof Address) => (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const newData = { ...formData, [field]: event.target.value };
    
    // Clear state/province when country changes
    if (field === 'country' && event.target.value !== formData.country) {
      newData.stateProvince = '';
    }
    
    setFormData(newData);
    onAddressChange(newData);
    
    // Clear field error when user starts typing
    if (fieldErrors[field]) {
      setFieldErrors(prev => {
        const newErrors = { ...prev };
        delete newErrors[field];
        return newErrors;
      });
    }
    
    // Reset validation when address changes
    if (validationStatus === 'validated' || validationStatus === 'error') {
      setValidationStatus('idle');
      setValidationErrors([]);
    }
  };

  const validateField = (field: keyof Address, value: string): string | null => {
    const latinCharRegex = /^[0-9A-Za-z #'.,-/()&\u00C0-\u017F]*$/;
    const phoneRegex = /^[\d\s\-\(\)\+\.]+$/;
    const usPostalRegex = /^\d{5}(-\d{4})?$/;
    const caPostalRegex = /^[A-Z]\d[A-Z]\s?\d[A-Z]\d$/i;

    switch (field) {
      case 'firstName':
      case 'lastName':
        if (!value) return `${field === 'firstName' ? 'First' : 'Last'} name is required`;
        if (!latinCharRegex.test(value)) return 'Only Latin characters are allowed (A-Z, 0-9, spaces, and common punctuation)';
        if (value.length > 15) return `Maximum 15 characters allowed`;
        break;
      
      case 'addressLine1':
        if (!value) return 'Address is required';
        if (!latinCharRegex.test(value)) return 'Only Latin characters are allowed';
        if (value.length > 60) return 'Maximum 60 characters allowed';
        break;
      
      case 'addressLine2':
        if (value && !latinCharRegex.test(value)) return 'Only Latin characters are allowed';
        if (value && value.length > 60) return 'Maximum 60 characters allowed';
        break;
      
      case 'city':
        if (!value) return 'City is required';
        if (!latinCharRegex.test(value)) return 'Only Latin characters are allowed';
        if (value.length > 60) return 'Maximum 60 characters allowed';
        break;
      
      case 'stateProvince':
        if (!value) return 'State/Province is required';
        if (value.length > 50) return 'Maximum 50 characters allowed';
        break;
      
      case 'postalCode':
        if (!value) return 'Postal code is required';
        if (formData.country === 'US' && !usPostalRegex.test(value)) {
          return 'Please enter a valid US postal code (e.g., 12345 or 12345-6789)';
        }
        if (formData.country === 'CA' && !caPostalRegex.test(value)) {
          return 'Please enter a valid Canadian postal code (e.g., A1B 2C3)';
        }
        if (value.length > 50) return 'Maximum 50 characters allowed';
        break;
      
      case 'phone':
        if (!value) return 'Phone number is required';
        if (!phoneRegex.test(value)) {
          return 'Please enter a valid phone number (digits, spaces, dashes, and parentheses allowed)';
        }
        if (value.length > 40) return 'Maximum 40 characters allowed';
        break;
    }
    
    return null;
  };

  const handleValidate = async () => {
    setValidationStatus('validating');
    setValidationErrors([]);
    setFieldErrors({});

    // First, do client-side validation
    const errors: Record<string, string> = {};
    let hasErrors = false;

    Object.keys(formData).forEach((key) => {
      const field = key as keyof Address;
      if (field !== 'country') {
        const error = validateField(field, formData[field] || '');
        if (error) {
          errors[field] = error;
          hasErrors = true;
        }
      }
    });

    if (hasErrors) {
      setFieldErrors(errors);
      setValidationStatus('error');
      setValidationErrors(['Please correct the errors below']);
      return;
    }

    // If client-side validation passes, call server validation
    try {
      const result = await onValidate(formData);
      
      if (result.validated) {
        setValidationStatus('validated');
      } else {
        setValidationStatus('error');
        // Parse server errors to see if they're field-specific
        if (result.errors && result.errors.length > 0) {
          const serverFieldErrors: Record<string, string> = {};
          const generalErrors: string[] = [];
          
          result.errors.forEach(error => {
            // Try to match field-specific errors
            const fieldMatch = error.toLowerCase().match(/(first name|last name|address|city|state|province|postal|zip|phone)/);
            if (fieldMatch) {
              if (fieldMatch[1].includes('first name')) serverFieldErrors.firstName = error;
              else if (fieldMatch[1].includes('last name')) serverFieldErrors.lastName = error;
              else if (fieldMatch[1].includes('address')) serverFieldErrors.addressLine1 = error;
              else if (fieldMatch[1].includes('city')) serverFieldErrors.city = error;
              else if (fieldMatch[1].includes('state') || fieldMatch[1].includes('province')) serverFieldErrors.stateProvince = error;
              else if (fieldMatch[1].includes('postal') || fieldMatch[1].includes('zip')) serverFieldErrors.postalCode = error;
              else if (fieldMatch[1].includes('phone')) serverFieldErrors.phone = error;
            } else {
              generalErrors.push(error);
            }
          });
          
          if (Object.keys(serverFieldErrors).length > 0) {
            setFieldErrors(serverFieldErrors);
          }
          if (generalErrors.length > 0) {
            setValidationErrors(generalErrors);
          }
        } else {
          setValidationErrors(['Address validation failed. Please check your information and try again.']);
        }
      }
    } catch (error) {
      setValidationStatus('error');
      setValidationErrors(['Unable to validate address. Please check your connection and try again.']);
    }
  };

  const isFormComplete = () => {
    return (
      formData.firstName &&
      formData.lastName &&
      formData.addressLine1 &&
      formData.city &&
      formData.stateProvince &&
      formData.postalCode &&
      formData.country &&
      formData.phone
    );
  };

  const handleNext = () => {
    if (validationStatus === 'validated') {
      onNext();
    }
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Get your YubiKeys
      </Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        To protect our users against account takeovers, we're rolling out the
        use of security keys. Order YubiKeys directly to your home.
      </Typography>

      <Box mt={4}>
        <Stack direction="row" spacing={2} alignItems="center" mb={3}>
          <Typography variant="h5">
            1 • Select your products
          </Typography>
          <Chip 
            label="Done" 
            size="small" 
            icon={<Check size={16} />}
            color="success"
          />
        </Stack>

        <Typography variant="h5" gutterBottom>
          2 • Address
        </Typography>

        <Grid container spacing={2} mt={2}>
          <Grid item xs={12} sm={6}>
            <TextField
              label="First Name"
              value={formData.firstName}
              onChange={handleFieldChange('firstName')}
              required
              fullWidth
              error={!!fieldErrors.firstName}
              helperText={fieldErrors.firstName}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Last Name"
              value={formData.lastName}
              onChange={handleFieldChange('lastName')}
              required
              fullWidth
              error={!!fieldErrors.lastName}
              helperText={fieldErrors.lastName}
            />
          </Grid>
          
          <Grid item xs={12}>
            <TextField
              label="Address Line 1"
              value={formData.addressLine1}
              onChange={handleFieldChange('addressLine1')}
              required
              fullWidth
              error={!!fieldErrors.addressLine1}
              helperText={fieldErrors.addressLine1}
            />
          </Grid>

          <Grid item xs={12}>
            <Collapse in={showAddressLine2}>
              <TextField
                label="Address Line 2"
                value={formData.addressLine2}
                onChange={handleFieldChange('addressLine2')}
                placeholder="Apartment, suite, etc. (optional)"
                fullWidth
                error={!!fieldErrors.addressLine2}
                helperText={fieldErrors.addressLine2}
              />
            </Collapse>
            {!showAddressLine2 && (
              <Button
                startIcon={<ChevronDown />}
                onClick={() => setShowAddressLine2(true)}
                size="small"
                sx={{ mt: -1 }}
              >
                Add Address Line 2
              </Button>
            )}
          </Grid>

          <Grid item xs={12} sm={6}>
            <TextField
              label="City"
              value={formData.city}
              onChange={handleFieldChange('city')}
              required
              fullWidth
              error={!!fieldErrors.city}
              helperText={fieldErrors.city}
            />
          </Grid>

          <Grid item xs={12} sm={6}>
            {states.length > 0 ? (
              <TextField
                select
                label="State/Province"
                value={formData.stateProvince}
                onChange={handleFieldChange('stateProvince')}
                required
                fullWidth
                error={!!fieldErrors.stateProvince}
                helperText={fieldErrors.stateProvince}
              >
                {states.map((state) => (
                  <MenuItem key={state.code} value={state.code}>
                    {state.name}
                  </MenuItem>
                ))}
              </TextField>
            ) : (
              <TextField
                label="State/Province"
                value={formData.stateProvince}
                onChange={handleFieldChange('stateProvince')}
                required
                fullWidth
                error={!!fieldErrors.stateProvince}
                helperText={fieldErrors.stateProvince}
              />
            )}
          </Grid>

          <Grid item xs={12} sm={6}>
            <TextField
              select
              label="Country"
              value={formData.country}
              onChange={handleFieldChange('country')}
              required
              fullWidth
              error={!!fieldErrors.country}
              helperText={fieldErrors.country || (formData.country === 'US' ? 'United States format expected' : formData.country === 'CA' ? 'Canadian format expected' : '')}
            >
              {countries.map((country) => (
                <MenuItem key={country.country_code_2} value={country.country_code_2}>
                  {country.country_name}
                </MenuItem>
              ))}
            </TextField>
          </Grid>

          <Grid item xs={12} sm={6}>
            <TextField
              label="Postal Code"
              value={formData.postalCode}
              onChange={handleFieldChange('postalCode')}
              required
              fullWidth
              error={!!fieldErrors.postalCode}
              helperText={fieldErrors.postalCode || (formData.country === 'US' ? 'Format: 12345 or 12345-6789' : formData.country === 'CA' ? 'Format: A1B 2C3' : '')}
              placeholder={formData.country === 'US' ? '12345' : formData.country === 'CA' ? 'A1B 2C3' : ''}
            />
          </Grid>

          <Grid item xs={12}>
            <TextField
              label="Phone"
              value={formData.phone}
              onChange={handleFieldChange('phone')}
              placeholder="555-123-4567"
              required
              fullWidth
              error={!!fieldErrors.phone}
              helperText={fieldErrors.phone || 'Include country code for international numbers'}
            />
          </Grid>
        </Grid>

        {validationErrors.length > 0 && (
          <Alert severity="error" sx={{ mt: 2 }}>
            {validationErrors.map((error, index) => (
              <div key={index}>{error}</div>
            ))}
          </Alert>
        )}

        {validationStatus === 'validated' && (
          <Alert 
            severity="success" 
            sx={{ mt: 2 }}
            action={
              <Chip
                label="Validated"
                icon={<Check size={16} />}
                color="success"
                size="small"
              />
            }
          >
            Shipping address validated successfully
          </Alert>
        )}

        <Stack direction="row" spacing={2} mt={4}>
          <Button
            variant="outlined"
            size="large"
            onClick={onBack}
            sx={{ minWidth: 120 }}
          >
            Back
          </Button>
          
          {validationStatus !== 'validated' && (
            <Button
              variant="contained"
              size="large"
              fullWidth
              disabled={!isFormComplete() || validationStatus === 'validating'}
              onClick={handleValidate}
              startIcon={validationStatus === 'validating' ? <CircularProgress size={20} /> : null}
              sx={{ 
                backgroundColor: '#666',
                '&:hover': {
                  backgroundColor: '#555',
                },
              }}
            >
              {validationStatus === 'validating' ? 'Validating...' : 'Validate Address'}
            </Button>
          )}

          {validationStatus === 'validated' && (
            <Button
              variant="contained"
              size="large"
              fullWidth
              onClick={handleNext}
              sx={{ 
                backgroundColor: '#000',
                '&:hover': {
                  backgroundColor: '#333',
                },
              }}
            >
              Next
            </Button>
          )}
        </Stack>
      </Box>
    </Box>
  );
};