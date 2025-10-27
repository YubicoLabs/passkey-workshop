import React, { useState } from 'react';
import { 
  Box, 
  Container, 
  Paper,
} from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ProductSelection } from './order-flow/ProductSelection';
import { AddressForm } from './order-flow/AddressForm';
import { OrderReview } from './order-flow/OrderReview';
import { OrderStatus } from './order-management/OrderStatus';
import { 
  Product, 
  SelectedProduct, 
  Address, 
  Shipment,
  CreateShipmentRequest,
} from '@/types/api';
import { YubiKeyApiClient } from '@/services/api-client';
import ErrorBoundary from './common/ErrorBoundary';

export interface YubiKeyOrderFlowProps {
  apiClient: YubiKeyApiClient;
  userEmail?: string;
  onComplete?: (shipment: Shipment) => void;
  onCancel?: () => void;
  locale?: string;
  translations?: Record<string, string>;
  products?: Product[];
  containerProps?: React.ComponentProps<typeof Container>;
  paperProps?: React.ComponentProps<typeof Paper>;
}

type OrderStep = 'products' | 'address' | 'review' | 'confirmation';

// Create query client outside component to avoid recreation
const createQueryClient = () => new QueryClient({
  defaultOptions: {
    queries: {
      // Updated retry logic
      retry: (failureCount, error: any) => {
        // Do not retry on 4xx client errors
        if (error.response?.status >= 400 && error.response?.status < 500) {
          return false;
        }
        // Retry up to 2 times (3 attempts total)
        return failureCount < 2;
      },
      retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000), // Exponential backoff
      refetchOnWindowFocus: false,
    },
  },
});

// Default products for demonstration
const defaultProducts: Product[] = [
  {
    id: 'yubikey-5c-nfc',
    name: 'Security Key (USB-C)',
    description: 'USB-C with NFC for mobile',
    price: 55,
    currency: 'USD',
    formFactor: 'USB-C',
    capabilities: ['FIDO2', 'U2F', 'Smart Card', 'OTP', 'NFC'],
    inStock: true,
  },
  {
    id: 'yubikey-5-nfc',
    name: 'Security Key (USB-A)',
    description: 'USB-A with NFC for mobile',
    price: 50,
    currency: 'USD',
    formFactor: 'USB-A',
    capabilities: ['FIDO2', 'U2F', 'Smart Card', 'OTP', 'NFC'],
    inStock: true,
  },
];

const YubiKeyOrderFlowInternal: React.FC<YubiKeyOrderFlowProps> = ({
  apiClient,
  userEmail = 'user@example.com',
  onComplete,
  onCancel,
  products = defaultProducts,
  containerProps = {},
  paperProps = {},
}) => {
  const [queryClient] = useState(createQueryClient);
  const [currentStep, setCurrentStep] = useState<OrderStep>('products');
  const [selectedProducts, setSelectedProducts] = useState<SelectedProduct[]>([]);
  const [shippingAddress, setShippingAddress] = useState<Address | null>(null);
  const [shipment, setShipment] = useState<Shipment | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleProductsChange = (products: SelectedProduct[]) => {
    setSelectedProducts(products);
  };

  const handleAddressChange = (address: Address) => {
    setShippingAddress(address);
  };

  const handleAddressValidation = async (address: Address) => {
    try {
      const response = await apiClient.validateAddress({ address });
      return {
        validated: response.validated,
        errors: response.errors,
      };
    } catch (error) {
      console.error('Address validation error:', error);
      return {
        validated: false,
        errors: ['Failed to validate address'],
      };
    }
  };

  const handleProductsNext = () => {
    if (selectedProducts.length >= 2) {
      setCurrentStep('address');
    }
  };

  const handleAddressNext = () => {
    if (shippingAddress) {
      setCurrentStep('review');
    }
  };

  const handleReviewConfirm = async () => {
    if (!shippingAddress || selectedProducts.length === 0) {
      return;
    }

    setIsSubmitting(true);

    try {
      const request: CreateShipmentRequest = {
        products: selectedProducts,
        shippingAddress,
        userEmail,
        metadata: {
          source: 'yubikey-launchpad',
          timestamp: new Date().toISOString(),
        },
      };

      const newShipment = await apiClient.createShipment(request);
      setShipment(newShipment);
      setCurrentStep('confirmation');
      
      if (onComplete) {
        onComplete(newShipment);
      }
    } catch (error) {
      console.error('Failed to create shipment:', error);
      // In production, show error UI
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleBack = () => {
    switch (currentStep) {
      case 'address':
        setCurrentStep('products');
        break;
      case 'review':
        setCurrentStep('address');
        break;
      case 'confirmation':
        setCurrentStep('review');
        break;
    }
  };

  const renderStep = () => {
    switch (currentStep) {
      case 'products':
        return (
          <ProductSelection
            products={products}
            selectedProducts={selectedProducts}
            onProductsChange={handleProductsChange}
            onNext={handleProductsNext}
          />
        );
      
      case 'address':
        return (
          <AddressForm
            address={shippingAddress}
            onAddressChange={handleAddressChange}
            onValidate={handleAddressValidation}
            onNext={handleAddressNext}
            onBack={handleBack}
            getCountries={apiClient.getCountries.bind(apiClient)}
          />
        );
      
      case 'review':
        return (
          <OrderReview
            selectedProducts={selectedProducts}
            shippingAddress={shippingAddress!}
            onConfirm={handleReviewConfirm}
            onBack={handleBack}
            isSubmitting={isSubmitting}
          />
        );
      
      case 'confirmation':
        return shipment ? (
          <OrderStatus
            shipment={shipment}
            onBack={onCancel}
          />
        ) : null;
      
      default:
        return null;
    }
  };

  return (
    <QueryClientProvider client={queryClient}>
      <Container maxWidth="md" sx={{ py: 4 }} {...containerProps}>
        <Paper 
          elevation={0} 
          sx={{ 
            p: { xs: 3, md: 5 },
            borderRadius: 2,
            border: '1px solid',
            borderColor: 'divider',
            ...paperProps.sx,
          }}
          {...paperProps}
        >
          {renderStep()}
        </Paper>
      </Container>
    </QueryClientProvider>
  );
};

// Export the component wrapped in the ErrorBoundary
export const YubiKeyOrderFlow: React.FC<YubiKeyOrderFlowProps> = (props) => (
  <ErrorBoundary>
    <YubiKeyOrderFlowInternal {...props} />
  </ErrorBoundary>
);