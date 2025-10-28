import React from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { YubiKeyOrderFlow } from './components/YubiKeyOrderFlow';
import { createApiClient } from './services/api-client';
import { theme } from './theme';

function App() {
  const apiClient = createApiClient({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
    // In production, you'd get this from your auth provider
    getIdToken: async () => {
      return 'mock-id-token';
    },
  });

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <YubiKeyOrderFlow
        apiClient={apiClient}
        userEmail="demo@example.com"
        onComplete={(shipment) => {
          console.log('Order completed:', shipment);
          // In production, you might redirect or show a success message
        }}
        onCancel={() => {
          console.log('Order cancelled');
          // Handle cancellation
        }}
      />
    </ThemeProvider>
  );
}

export default App;