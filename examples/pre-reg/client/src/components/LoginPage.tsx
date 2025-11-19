import React from 'react';
import { Box, CssBaseline, Paper, Typography, Button } from '@mui/material';
import SecurityIcon from '@mui/icons-material/Security';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';

interface LoginPageProps {
  onLogin: () => void;
}

const LoginPage: React.FC<LoginPageProps> = ({ onLogin }) => (
  <Box
    sx={{
      minHeight: '100vh',
      display: 'flex',
      flexDirection: 'column',
      backgroundColor: '#f5f5f5',
      // Optional: Add a subtle background pattern or gradient here
      // backgroundImage: 'radial-gradient(#e0e0e0 1px, transparent 1px)',
      // backgroundSize: '20px 20px'
    }}
  >
    <CssBaseline />
    {/* Main Content Area - Centered */}
    <Box
      sx={{
        flex: 1,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        p: 2,
      }}
    >
      <Paper
        elevation={3}
        sx={{
          p: 5,
          width: '100%',
          maxWidth: 450,
          borderRadius: 2,
          textAlign: 'center',
          display: 'flex',
          flexDirection: 'column',
          gap: 3,
        }}
      >
        {/* Logo Section */}
        <Box sx={{ display: 'flex', justifyContent: 'center', mb: 1 }}>
          <SecurityIcon sx={{ fontSize: 48, color: 'primary.main' }} />
        </Box>
        {/* Text Section */}
        <Box>
          <Typography variant="h4" component="h1" fontWeight="600" gutterBottom>
            Sign in
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Welcome to the YubiKey LaunchPad. <br />
            Please authenticate to manage your orders.
          </Typography>
        </Box>
        {/* Action Section */}
        <Button
          variant="contained"
          size="large"
          fullWidth
          onClick={onLogin}
          endIcon={<ArrowForwardIcon />}
          sx={{
            py: 1.5,
            fontSize: '1.1rem',
            textTransform: 'none',
            fontWeight: 600,
          }}
        >
          Log in with SSO
        </Button>
        {/* Footer / Help Links inside the card */}
        <Typography variant="caption" color="text.disabled" sx={{ mt: 2 }}>
          Protected by FIDO2 & WebAuthn
        </Typography>
      </Paper>
    </Box>
    {/* Global Footer */}
    <Box component="footer" sx={{ py: 3, textAlign: 'center' }}>
      <Typography variant="body2" color="text.secondary">
        © 2024 Yubico. All rights reserved. |{' '}
        <a href="#" style={{ color: 'inherit' }}>Privacy Policy</a>
      </Typography>
    </Box>
  </Box>
);

export default LoginPage;
