import React from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline, CircularProgress, Button, Box, Typography } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'; // - Add this import
import { AuthProvider } from 'react-oidc-context';

import LoginPage from '@/features/auth/components/LoginPage';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { YubiKeyOrderFlow } from './components/YubiKeyOrderFlow';
import { useApiClient } from './hooks/useApiClient';
import { theme } from './theme';
import { oidcConfig } from '@/features/auth/config/oidc';

const queryClient = new QueryClient();

function AuthenticatedApp() {
  const { isAuthenticated, isLoading, error, user, login, logout } = useAuth();

  React.useEffect(() => {
    if (user) {
      console.log('🔐 Token Info:', {
        sub: user.sub,
        email: user.email,
        name: user.name,
      });
    }
  }, [user]);

  const apiClient = useApiClient();

  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
        <Typography sx={{ ml: 2 }}>Loading authentication...</Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ p: 4 }}>
        <Typography variant="h6" color="error">Authentication Error</Typography>
        <Typography>{error.message}</Typography>
        <Button variant="contained" onClick={login}>
          Try Again
        </Button>
      </Box>
    );
  }

  if (!isAuthenticated) {
    return <LoginPage onLogin={login} />;
  }

  return (
    <>
      <Button 
        variant="outlined" 
        size="small"
        sx={{ position: 'absolute', top: 16, right: 16, zIndex: 1000 }}
        onClick={logout}
      >
        Logout ({user?.email})
      </Button>

      <YubiKeyOrderFlow
        apiClient={apiClient}
        userEmail={user?.email || 'demo@example.com'}
        keycloakUserId={user?.sub}
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