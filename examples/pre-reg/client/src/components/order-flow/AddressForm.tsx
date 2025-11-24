import React, { useState, useCallback } from 'react';
import { 
  Box, 
  TextField, 
  Button, 
  Typography, 
  Alert, 
  CircularProgress, 
  Chip, 
  Stack, 
  Collapse, 
  Autocomplete, 
  Grid,
  styled 
} from '@mui/material';
import { ChevronDown, Check } from 'lucide-react';
import { Address, CountriesResponse, AddressWithValidationSchema } from '@/types/api';
import { useQuery } from '@tanstack/react-query';


const useAddressForm = (
  initialAddress: Address | null,
  onAddressChange: (address: Address) => void,
  onServerValidate: (address: Address) => Promise<{ validated: boolean; errors?: string[]; suggestedAddress?: any }>
) => {
  const [formData, setFormData] = useState<Address>(
    initialAddress || {
      firstName: '', lastName: '', addressLine1: '', addressLine2: '',
      city: '', stateProvince: '', postalCode: '', country: '', phone: '',
    }
  );

  const [validationStatus, setValidationStatus] = useState<'idle' | 'validating' | 'validated' | 'error'>('idle');
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

  const isComplete = () => {
    return !!(
      formData.firstName && formData.lastName && formData.addressLine1 &&
      formData.city && formData.stateProvince && formData.postalCode &&
      formData.country && formData.phone
    );
  };

  return {
    formData,
    fieldErrors,
    validationStatus,
    validationErrors,
    setFieldValue, // Exposing the new setter
    validate,
    isComplete
  };
};

const CompletedStep = ({ label }: { label: string }) => (
  <Stack direction="row" spacing={2} alignItems="center" mb={3}>
    <Typography variant="h5" color="text.secondary">
      {label}
    </Typography>
    <Chip label="Done" size="small" icon={<Check size={16} />} color="success" variant="outlined" />
  </Stack>
);

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
  const {
    formData,
    fieldErrors,
    validationStatus,
    validationErrors,
    setFieldValue,
    validate,
    isComplete
  } = useAddressForm(address, onAddressChange, onValidate);

  const [showAddressLine2, setShowAddressLine2] = useState(!!formData.addressLine2);

  const { data: countriesData } = useQuery({ queryKey: ['countries'], queryFn: getCountries });
  const countries = countriesData?.countries ?? [];

  const handleNextClick = () => {
    if (validationStatus === 'validated') onNext();
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>Get your YubiKeys</Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        To protect our users against account takeovers, we're rolling out the use of security keys.
      </Typography>

      <Box mt={4}>
        <CompletedStep label="1 • Select your products" />
        <SectionTitle variant="h5">2 • Address</SectionTitle>

        <FormGrid container spacing={2}>
          <Grid item xs={12} sm={6}>
            <TextField 
              label="First Name" 
              value={formData.firstName} 
              onChange={(e) => setFieldValue('firstName', e.target.value)} 
              required fullWidth 
              error={!!fieldErrors.firstName} 
              helperText={fieldErrors.firstName} 
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField 
              label="Last Name" 
              value={formData.lastName} 
              onChange={(e) => setFieldValue('lastName', e.target.value)} 
              required fullWidth 
              error={!!fieldErrors.lastName} 
              helperText={fieldErrors.lastName} 
            />
          </Grid>
          
          <Grid item xs={12}>
            <TextField 
              label="Address Line 1" 
              value={formData.addressLine1} 
              onChange={(e) => setFieldValue('addressLine1', e.target.value)} 
              required fullWidth 
              error={!!fieldErrors.addressLine1} 
              helperText={fieldErrors.addressLine1} 
            />
          </Grid>

          <Grid item xs={12}>
            <Collapse in={showAddressLine2}>
              <TextField 
                label="Address Line 2" 
                value={formData.addressLine2 || ''} 
                onChange={(e) => setFieldValue('addressLine2', e.target.value)} 
                placeholder="Apartment, suite, etc." 
                fullWidth 
                error={!!fieldErrors.addressLine2}
                helperText={fieldErrors.addressLine2}
              />
            </Collapse>
            {!showAddressLine2 && (
              <Button startIcon={<ChevronDown />} onClick={() => setShowAddressLine2(true)} size="small" sx={{ mt: -1 }}>
                Add Address Line 2
              </Button>
            )}
          </Grid>

          <Grid item xs={12} sm={6}>
            <TextField 
              label="City" 
              value={formData.city} 
              onChange={(e) => setFieldValue('city', e.target.value)} 
              required fullWidth 
              error={!!fieldErrors.city} 
              helperText={fieldErrors.city} 
            />
          </Grid>

          <Grid item xs={12} sm={6}>
            <TextField 
              label="State/Province" 
              value={formData.stateProvince} 
              onChange={(e) => setFieldValue('stateProvince', e.target.value)} 
              required fullWidth 
              error={!!fieldErrors.stateProvince} 
              helperText={fieldErrors.stateProvince} 
            />
          </Grid>

          <Grid item xs={12} sm={6}>
            <Autocomplete
              fullWidth
              options={countries}
              autoHighlight
              getOptionLabel={(option) => option.country_name}
              value={countries.find((c) => c.country_code_2 === formData.country) || null}
              // IMPROVEMENT: No more fake events or 'as any'
              onChange={(_, newValue) => setFieldValue('country', newValue ? newValue.country_code_2 : '')}
              renderInput={(params) => (
                <TextField 
                  {...params} 
                  label="Country" 
                  required fullWidth 
                  error={!!fieldErrors.country} 
                  helperText={fieldErrors.country} 
                />
              )}
            />
          </Grid>

          <Grid item xs={12} sm={6}>
            <TextField 
              label="Postal Code" 
              value={formData.postalCode} 
              onChange={(e) => setFieldValue('postalCode', e.target.value)} 
              required fullWidth 
              error={!!fieldErrors.postalCode} 
              helperText={fieldErrors.postalCode || (formData.country === 'US' ? 'Format: 12345 or 12345-6789' : '')} 
            />
          </Grid>

          <Grid item xs={12}>
            <TextField 
              label="Phone" 
              value={formData.phone} 
              onChange={(e) => setFieldValue('phone', e.target.value)} 
              required fullWidth 
              error={!!fieldErrors.phone} 
              helperText={fieldErrors.phone} 
            />
          </Grid>
        </FormGrid>

        {/* Feedback Section */}
        {validationErrors.length > 0 && (
          <Alert severity="error" sx={{ mt: 2 }}>
            {validationErrors.map((e, i) => <div key={i}>{e}</div>)}
          </Alert>
        )}

        {validationStatus === 'validated' && (
          <Alert severity="success" sx={{ mt: 2 }} action={<Chip label="Validated" size="small" color="success" icon={<Check size={14} />} />}>
            Address validated successfully
          </Alert>
        )}

        {/* Action Buttons */}
        <Stack direction="row" spacing={2} mt={4}>
          <ActionButton variant="outlined" size="large" onClick={onBack}>
            Back
          </ActionButton>
          
          {validationStatus !== 'validated' ? (
            <ValidationButton
              variant="contained"
              size="large"
              fullWidth
              disabled={!isComplete() || validationStatus === 'validating'}
              onClick={validate}
              startIcon={validationStatus === 'validating' ? <CircularProgress size={20} color="inherit" /> : null}
            >
              {validationStatus === 'validating' ? 'Validating...' : 'Validate Address'}
            </ValidationButton>
          ) : (
            <PrimaryButton
              variant="contained"
              size="large"
              fullWidth
              onClick={handleNextClick}
            >
              Next
            </PrimaryButton>
          )}
        </Stack>
      </Box>
    </Box>
  );
};

const SectionTitle = styled(Typography)(({ theme }) => ({
  fontWeight: 500,
  marginBottom: theme.spacing(2),
}));

const FormGrid = styled(Grid)(({ theme }) => ({
  marginTop: theme.spacing(1),
}));

const ActionButton = styled(Button)(({ theme }) => ({
  paddingBlock: theme.spacing(1.5),
  minWidth: 120,
}));

const PrimaryButton = styled(ActionButton)(({ theme }) => ({
  backgroundColor: '#000',
  '&:hover': { backgroundColor: '#333' },
}));

const ValidationButton = styled(ActionButton)(({ theme }) => ({
  backgroundColor: '#666',
  '&:hover': { backgroundColor: '#555' },
}));