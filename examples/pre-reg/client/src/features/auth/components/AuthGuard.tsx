import React from 'react';
import { Box, CircularProgress, Typography, Button } from '@mui/material';
import { useAuth } from '../hooks/useAuth';
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
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          height: '100vh',
          gap: 2,
          p: 4,
        }}
      >
        <Typography variant="h6" color="error">
          Authentication Error
        </Typography>
        <Typography color="text.secondary">{error.message}</Typography>
        <Button variant="contained" onClick={login}>
          Try Again
        </Button>
      </Box>
    );
  }

  if (!isAuthenticated) {
    return <LoginPage onLogin={login} />;
  }

  return <>{children}</>;
};
