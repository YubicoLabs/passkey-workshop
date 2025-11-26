import { useMemo } from 'react';
import { useAuth } from 'react-oidc-context';
import { createApiClient } from '../services/api-client';

export const useApiClient = () => {
  const auth = useAuth();

  const apiClient = useMemo(() => {
    return createApiClient({
      baseURL: import.meta.env.VITE_API_BASE_URL,
      getIdToken: async () => {
        return auth.user?.access_token || '';
      },
    });
  }, [auth.user?.access_token]);

  return apiClient;
};
