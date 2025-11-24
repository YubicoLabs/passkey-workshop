import React, { useState } from 'react';
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
import { Address, Country, CountriesResponse, AddressWithValidationSchema } from '@/types/api';
import { useQuery } from '@tanstack/react-query';


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


const CompletedStep = ({ label }: { label: string }) => (
  <Stack direction="row" spacing={2} alignItems="center" mb={3}>
    <Typography variant="h5" color="text.secondary">
      {label}
    </Typography>
    <Chip 
      label="Done" 
      size="small" 
      icon={<Check size={16} />} 
      color="success" 
      variant="outlined" 
    />
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
  // --- STATE LOGIC ---
  const [formData, setFormData] = useState<Address>(
    address || {
      firstName: '', lastName: '', addressLine1: '', addressLine2: '',
      city: '', stateProvince: '', postalCode: '', country: '', phone: '',
    }
  );

  const [showAddressLine2, setShowAddressLine2] = useState(!!formData.addressLine2);
  const [validationStatus, setValidationStatus] = useState<'idle' | 'validating' | 'validated' | 'error'>('idle');
  const [validationErrors, setValidationErrors] = useState<string[]>([]);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const { data: countriesData } = useQuery({ queryKey: ['countries'], queryFn: getCountries });
  const countries = countriesData?.countries ?? [];

  const handleFieldChange = (field: keyof Address) => (e: React.ChangeEvent<HTMLInputElement>) => {
    const newData = { ...formData, [field]: e.target.value };
    
    // Clear state/province if country changes to force re-selection/entry
    if (field === 'country' && e.target.value !== formData.country) {
      newData.stateProvince = '';
    }

    setFormData(newData);
    onAddressChange(newData);
    
    // Clear specific field error on change
    if (fieldErrors[field]) {
      setFieldErrors(prev => { 
        const n = { ...prev }; 
        delete n[field]; 
        return n; 
      });
    }
    
    // Reset global validation status on any change
    if (validationStatus === 'validated' || validationStatus === 'error') {
      setValidationStatus('idle');
      setValidationErrors([]);
    }
  };

  const handleValidate = async () => {
    setValidationStatus('validating');
    setValidationErrors([]);
    setFieldErrors({});

    // 1. Zod Schema Validation (Client-Side)
    const result = AddressWithValidationSchema.safeParse(formData);

    if (!result.success) {
      const newFieldErrors: Record<string, string> = {};
      
      result.error.issues.forEach((issue) => {
        // Map Zod path to field name (e.g., ['postalCode'] -> 'postalCode')
        if (issue.path.length > 0) {
          const fieldName = issue.path[0] as string;
          newFieldErrors[fieldName] = issue.message;
        }
      });

      setFieldErrors(newFieldErrors);
      setValidationStatus('error');
      setValidationErrors(['Please correct the errors highlighted below.']);
      return;
    }

    // 2. Server-Side Validation (Existing Logic)
    try {
      const serverResult = await onValidate(formData);
      
      if (serverResult.validated) {
        setValidationStatus('validated');
        if (serverResult.suggestedAddress) {
          setFormData(serverResult.suggestedAddress);
        }
      } else {
        setValidationStatus('error');
        if (serverResult.errors && serverResult.errors.length > 0) {
          setValidationErrors(serverResult.errors);
        } else {
          setValidationErrors(['Address validation failed. Please check your information.']);
        }
      }
    } catch (error) {
      setValidationStatus('error');
      setValidationErrors(['Unable to validate address. Please check your connection.']);
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

  // --- RENDER ---
  return (
    <Box>
      <Typography variant="h4" gutterBottom>Get your YubiKeys</Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        To protect our users against account takeovers, we're rolling out the use of security keys.
      </Typography>

      <Box mt={4}>
        {/* Step 1: Done */}
        <CompletedStep label="1 • Select your products" />

        {/* Step 2: Active */}
        <SectionTitle variant="h5">2 • Address</SectionTitle>

        <FormGrid container spacing={2}>
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
                value={formData.addressLine2 || ''} 
                onChange={handleFieldChange('addressLine2')} 
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
              onChange={handleFieldChange('city')} 
              required 
              fullWidth 
              error={!!fieldErrors.city} 
              helperText={fieldErrors.city} 
            />
          </Grid>

          <Grid item xs={12} sm={6}>
            <TextField 
              label="State/Province" 
              value={formData.stateProvince} 
              onChange={handleFieldChange('stateProvince')} 
              required 
              fullWidth 
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
              onChange={(_, v) => handleFieldChange('country')({ target: { value: v?.country_code_2 || '' } } as any)}
              renderInput={(params) => (
                <TextField 
                  {...params} 
                  label="Country" 
                  required 
                  fullWidth 
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
              onChange={handleFieldChange('postalCode')} 
              required 
              fullWidth 
              error={!!fieldErrors.postalCode} 
              helperText={fieldErrors.postalCode || (formData.country === 'US' ? 'Format: 12345 or 12345-6789' : '')} 
            />
          </Grid>

          <Grid item xs={12}>
            <TextField 
              label="Phone" 
              value={formData.phone} 
              onChange={handleFieldChange('phone')} 
              required 
              fullWidth 
              error={!!fieldErrors.phone} 
              helperText={fieldErrors.phone} 
            />
          </Grid>
        </FormGrid>

        {/* Feedback Messages */}
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

        {/* Actions */}
        <Stack direction="row" spacing={2} mt={4}>
          <ActionButton variant="outlined" size="large" onClick={onBack}>
            Back
          </ActionButton>
          
          {validationStatus !== 'validated' ? (
            <ValidationButton
              variant="contained"
              size="large"
              fullWidth
              disabled={!isFormComplete() || validationStatus === 'validating'}
              onClick={handleValidate}
              startIcon={validationStatus === 'validating' ? <CircularProgress size={20} color="inherit" /> : null}
            >
              {validationStatus === 'validating' ? 'Validating...' : 'Validate Address'}
            </ValidationButton>
          ) : (
            <PrimaryButton
              variant="contained"
              size="large"
              fullWidth
              onClick={onNext}
            >
              Next
            </PrimaryButton>
          )}
        </Stack>
      </Box>
    </Box>
  );
};