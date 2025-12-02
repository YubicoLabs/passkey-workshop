import React from 'react';
import { Box, CircularProgress, Typography } from '@mui/material';
import { useAuth } from '../hooks/useAuth';
import { ErrorScreen } from '@/shared/components/ErrorScreen';
import LoginPage from './LoginPage';

export interface AuthGuardProps {
  children: React.ReactNode;
}

export const AuthGuard: React.FC<AuthGuardProps> = ({ children }) => {
  const { isLoading, error, isAuthenticated, login } = useAuth();

  if (isLoading) {
    return (
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          height: '100vh',
          gap: 2,
        }}
      >
        <CircularProgress />
        <Typography>Loading authentication...</Typography>
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

  return <>{children}</>;
};
