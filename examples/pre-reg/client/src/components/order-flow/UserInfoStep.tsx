import React, { useState } from 'react';
import { Box, TextField, Typography, Button, Paper, Stack, Chip } from '@mui/material';
import { Check } from 'lucide-react';

interface UserInfoStepProps {
  userId: string;
  onUserIdChange: (userId: string) => void;
  resellerOrgId: string;
  onNext: () => void;
  onBack: () => void;
}

export const UserInfoStep: React.FC<UserInfoStepProps> = ({
  userId,
  onUserIdChange,
  resellerOrgId,
  onNext,
  onBack,
}) => {
  const [input, setInput] = useState(userId);
  const [error, setError] = useState('');

  const handleNext = () => {
    if (!input.trim()) {
      setError('User ID is required');
      return;
    }
    setError('');
    onUserIdChange(input.trim());
    onNext();
  };

  return (
    <Paper elevation={0} sx={{ p: { xs: 3, md: 5 }, borderRadius: 2, border: '1px solid', borderColor: 'divider', maxWidth: 600, mx: 'auto' }}>
      <Box>
        {/* Title and description at the top, matching AddressForm style */}
        <Typography variant="h4" gutterBottom>
          Account Details
        </Typography>
        <Typography variant="body1" color="text.secondary" paragraph>
          Please provide your Keycloak User ID for this order. The Yubico Reseller Organization ID is shown for your reference.
        </Typography>
        {/* Stepper, matching AddressForm style */}
        <Box mt={4}>
          <Stack direction="row" spacing={2} alignItems="center" mb={3}>
            <Typography variant="h5">1 • Select your products</Typography>
            <Chip label="Done" size="small" icon={<Check size={16} />} color="success" />
          </Stack>
          <Stack direction="row" spacing={2} alignItems="center" mb={3}>
            <Typography variant="h5">2 • Address</Typography>
            <Chip label="Done" size="small" icon={<Check size={16} />} color="success" />
          </Stack>
          <Typography variant="h5" gutterBottom>3 • Account Information</Typography>
          <Box mt={4}>
            <Stack spacing={2}>
              <TextField
                label="Keycloak User ID"
                value={input}
                onChange={e => setInput(e.target.value)}
                error={!!error}
                helperText={error}
                required
                fullWidth
              />
              <TextField
                label="Yubico Reseller Organization ID"
                value={resellerOrgId}
                fullWidth
                disabled
                InputProps={{ style: { color: '#888' } }}
              />
            </Stack>
            <Stack direction="row" spacing={2} mt={4}>
              <Button
                variant="outlined"
                size="large"
                onClick={onBack}
                sx={{ minWidth: 120 }}
              >
                Back
              </Button>
              <Button
                variant="contained"
                size="large"
                fullWidth
                onClick={handleNext}
                sx={{
                  minWidth: 120,
                  backgroundColor: '#000',
                  '&:hover': { backgroundColor: '#333' },
                }}
              >
                Next
              </Button>
            </Stack>
          </Box>
        </Box>
      </Box>
    </Paper>
  );
};
