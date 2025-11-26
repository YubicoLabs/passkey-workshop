import { useMutation } from '@tanstack/react-query';
import { useApiClient } from './useApiClient';
import type { Address } from '@/types/api';

export const useAddressValidation = () => {
  const client = useApiClient();

  return useMutation({
    mutationFn: (address: Address) => client.validateAddress({ address }),
  });
};
