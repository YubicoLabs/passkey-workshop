import { useState, useCallback } from 'react';
import type { Product, SelectedProduct, Address, Shipment } from '@/features/orders/types';

export type OrderStep = 'products' | 'address' | 'review' | 'success';

export interface UseOrderFlowReturn {
  step: OrderStep;
  setStep: (step: OrderStep) => void;
  selectedProducts: SelectedProduct[];
  setSelectedProducts: (products: SelectedProduct[]) => void;
  address: Address | null;
  setAddress: (address: Address | null) => void;
  createdShipment: Shipment | null;
  setCreatedShipment: (shipment: Shipment | null) => void;
  goToNext: () => void;
  goToBack: () => void;
  canProceed: boolean;
}

export const useOrderFlow = (initialProducts: Product[]): UseOrderFlowReturn => {
  const [step, setStep] = useState<OrderStep>('products');
  const [selectedProducts, setSelectedProducts] = useState<SelectedProduct[]>([]);
  const [address, setAddress] = useState<Address | null>(null);
  const [createdShipment, setCreatedShipment] = useState<Shipment | null>(null);

  const goToNext = useCallback(() => {
    if (step === 'products' && selectedProducts.length > 0) setStep('address');
    else if (step === 'address' && address) setStep('review');
  }, [step, selectedProducts, address]);

  const goToBack = useCallback(() => {
    if (step === 'address') setStep('products');
    else if (step === 'review') setStep('address');
  }, [step]);

  const canProceed =
    step === 'products' ? selectedProducts.length > 0 :
    step === 'address' ? address !== null :
    step === 'review'  ? true :
    false;

  return {
    step, setStep,
    selectedProducts, setSelectedProducts,
    address, setAddress,
    createdShipment, setCreatedShipment,
    goToNext, goToBack,
    canProceed,
  };
};
