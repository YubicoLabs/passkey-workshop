import React from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline, CircularProgress, Button, Box, Typography, Paper,  } from '@mui/material';
import SecurityIcon from '@mui/icons-material/Security';
import { YubiKeyOrderFlow } from './components/YubiKeyOrderFlow';
import { createApiClient } from './services/api-client';
import { theme } from './theme';
import { AuthProvider, useAuth } from 'react-oidc-context';
import { oidcConfig } from './auth/config';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';

function AuthenticatedApp() {
  const auth = useAuth();

    React.useEffect(() => {
    if (auth.user) {
      console.log('🔐 Token Info:', {
        sub: auth.user.profile.sub,
        email: auth.user.profile.email,
        name: auth.user.profile.name,
        preffered_username: auth.user?.profile?.preferred_username,
        fullProfile: auth.user.profile
      });
    }
  }, [auth.user]);

  const apiClient = React.useMemo(() => {
    return createApiClient({
      baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8086/api',
      getIdToken: async () => {
        return auth.user?.access_token || '';
      },
    });
  }, [auth.user?.access_token]);

  if (auth.isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
        <Typography sx={{ ml: 2 }}>Loading authentication...</Typography>
      </Box>
    );
  }

  if (auth.error) {
    return (
      <Box sx={{ p: 4 }}>
        <Typography variant="h6" color="error">Authentication Error</Typography>
        <Typography>{auth.error.message}</Typography>
        <Button variant="contained" onClick={() => auth.signinRedirect()}>
          Try Again
        </Button>
      </Box>
    );
  }

if (!auth.isAuthenticated) {
  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        backgroundColor: '#f5f5f5', // Light grey enterprise background
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
            {/* Replace this icon with your actual YubiKey/Company Logo */}
            <SecurityIcon sx={{ fontSize: 48, color: 'primary.main' }} />
          </Box>

          {/* Text Section */}
          <Box>
            <Typography variant="h4" component="h1" fontWeight="600" gutterBottom>
              Sign in
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Welcome to the YubiKey LaunchPad. <br/>
              Please authenticate to manage your orders.
            </Typography>
          </Box>

          {/* Action Section */}
          <Button
            variant="contained"
            size="large"
            fullWidth
            onClick={() => auth.signinRedirect()}
            endIcon={<ArrowForwardIcon />}
            sx={{ 
              py: 1.5,
              fontSize: '1.1rem',
              textTransform: 'none', // Modern look (removes ALL CAPS)
              fontWeight: 600
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
          © 2024 Yubico. All rights reserved. | <a href="#" style={{ color: 'inherit' }}>Privacy Policy</a>
        </Typography>
      </Box>
    </Box>
  );
}

  // User is authenticated - show the actual app
  const userId = auth.user?.profile.sub;
  const userEmail = auth.user?.profile.email;

  return (
    <>
      {/* Optional: Add a logout button in the corner */}
      <Button 
        variant="outlined" 
        size="small"
        sx={{ position: 'absolute', top: 16, right: 16, zIndex: 1000 }}
        onClick={() => auth.signoutRedirect()}
      >
        Logout ({userEmail})
      </Button>

      <YubiKeyOrderFlow
        apiClient={apiClient}
        userEmail={userEmail || 'demo@example.com'}
        keycloakUserId={userId}
        onComplete={(shipment) => {
          console.log('Order completed:', shipment);
        }}
        onCancel={() => {
          console.log('Order cancelled');
        }}
      />
    </>
  );
}

function App() {
  return (
    <AuthProvider {...oidcConfig}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <AuthenticatedApp />
      </ThemeProvider>
    </AuthProvider>
  );
}

export default App;