import React from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline, CircularProgress, Button, Box, Typography, Paper,  } from '@mui/material';
import SecurityIcon from '@mui/icons-material/Security';
import LoginPage from './components/LoginPage';
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
      baseURL: import.meta.env.VITE_API_BASE_URL,
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
    return <LoginPage onLogin={() => auth.signinRedirect()} />;
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