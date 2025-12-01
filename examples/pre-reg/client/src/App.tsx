import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline, CircularProgress, Button, Box, Typography } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'; // - Add this import
import { AuthProvider } from 'react-oidc-context';

import LoginPage from '@/features/auth/components/LoginPage';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { YubiKeyOrderFlow } from '@/features/orders/components/YubiKeyOrderFlow';
import { useApiClient } from '@/shared/hooks/useApiClient';
import { theme } from './theme';
import { oidcConfig } from '@/features/auth/config/oidc';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000, // 5 minutes
      retry: 3,
      refetchOnWindowFocus: false,
    },
  },
});

function AuthenticatedApp() {
  const { isAuthenticated, isLoading, error, user, login, logout } = useAuth();

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
        userEmail={user?.email || ''}
        keycloakUserId={user?.sub}
        onComplete={() => {}}
        onCancel={() => {}}
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