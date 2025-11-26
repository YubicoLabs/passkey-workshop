import { useQuery } from '@tanstack/react-query';
import { useApiClient } from './useApiClient';

export const useCountries = () => {
  const client = useApiClient();

  return useQuery({
    queryKey: ['countries'],
    queryFn: () => client.getCountries(),
  });
};
