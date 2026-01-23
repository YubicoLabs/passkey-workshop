import { useMutation } from '@tanstack/react-query';
import { useApiClient } from '@/shared/hooks/useApiClient';
import type { Address } from '@/features/orders/types';

export const useAddressValidation = () => {
  const client = useApiClient();

  return useMutation({
    mutationFn: (address: Address) => client.validateAddress({ address }),
  });
};
