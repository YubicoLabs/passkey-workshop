import { useMemo } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { createApiClient, YubiKeyApiClient } from '@/shared/api/client';

export const useApiClient = (): YubiKeyApiClient => {
  const { user } = useAuth();

  const apiClient = useMemo(() => {
    return createApiClient({
      baseURL: import.meta.env.VITE_API_BASE_URL,
      getIdToken: async () => {
        return user?.access_token || '';
      },
    });
  }, [user?.access_token]);

  return apiClient;
};
