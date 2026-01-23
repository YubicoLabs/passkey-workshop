import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline, CircularProgress, Button, Box, Typography } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from 'react-oidc-context';

import LoginPage from '@/features/auth/components/LoginPage';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { YubiKeyOrderFlow } from '@/features/orders/components/YubiKeyOrderFlow';
import { useApiClient } from '@/shared/hooks/useApiClient';
import { ErrorScreen } from '@/shared/components/ErrorScreen';
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
      <ErrorScreen
        title="Authentication Error"
        message="We couldn't sign you in. Please check your connection and try again."
        actionLabel="Try Again"
        onAction={login}
        showTechnicalDetails={import.meta.env.DEV}
        technicalDetails={error.message}
      />
    );
  }

  if (!isAuthenticated) {
    return <LoginPage onLogin={login} />;
  }

  return (
    <>
      <Button 
        variant="contained" 
        size="small"
        sx={{ 
          position: 'absolute', 
          top: 16, 
          right: 16, 
          zIndex: 1000,
          backgroundColor: 'common.black',
          '&:hover': { backgroundColor: 'grey.800' }
        }}
        onClick={logout}
      >
        Logout ({user?.email})
      </Button>

      <YubiKeyOrderFlow
        apiClient={apiClient}
        userEmail={user?.email || ''}
        keycloakUserId={user?.preferred_username}
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