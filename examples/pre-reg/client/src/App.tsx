import React from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline, CircularProgress, Button, Box, Typography } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'; // - Add this import
import { AuthProvider, useAuth } from 'react-oidc-context';

import LoginPage from './components/LoginPage';
import { YubiKeyOrderFlow } from './components/YubiKeyOrderFlow';
import { useApiClient } from './hooks/useApiClient';
import { theme } from './theme';
import { oidcConfig } from './auth/config';

// 1. Create a client instance outside the component to keep it stable
const queryClient = new QueryClient();

function AuthenticatedApp() {
  const auth = useAuth();

  // ... (Keep your existing AuthenticatedApp logic unchanged)
  React.useEffect(() => {
    if (auth.user) {
      console.log('🔐 Token Info:', {
        sub: auth.user.profile.sub,
        email: auth.user.profile.email,
        name: auth.user.profile.name,
      });
    }
  }, [auth.user]);

  const apiClient = useApiClient();

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

  const userId = auth.user?.profile.sub;
  const userEmail = auth.user?.profile.email;

  return (
    <>
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
    // 2. Wrap the entire app hierarchy with the Provider
    <QueryClientProvider client={queryClient}>
      <AuthProvider {...oidcConfig}>
        <ThemeProvider theme={theme}>
          <CssBaseline />
          <AuthenticatedApp />
        </ThemeProvider>
      </AuthProvider>
    </QueryClientProvider>
  );
}

export default App;