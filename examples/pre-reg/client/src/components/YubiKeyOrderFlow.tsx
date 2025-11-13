import React, { useState } from 'react';
import { 
  Container, 
  Paper,
  Box,
  Typography,
} from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ProductSelection } from './order-flow/ProductSelection';
import { AddressForm } from './order-flow/AddressForm';
import { OrderReview } from './order-flow/OrderReview';
import { OrderStatus } from './order-management/OrderStatus';
import { 
  SelectedProduct, 
  Address,
  Product,
  Shipment,
  ShipmentRequest,
} from '@/types/api';
import { YubiKeyApiClient } from '@/services/api-client';
import ErrorBoundary from './common/ErrorBoundary';
// Removed unused UserInfoStep import

export interface YubiKeyOrderFlowProps {
  apiClient: YubiKeyApiClient;
  userEmail?: string;
  userId?: string;
  onComplete?: (shipment: Shipment) => void;
  onCancel?: () => void;
  locale?: string;
  translations?: Record<string, string>;
  products?: Product[];
  containerProps?: React.ComponentProps<typeof Container>;
  paperProps?: React.ComponentProps<typeof Paper>;
  keycloakUserId?: string;
}

type OrderStep = 'products' | 'address' | 'userInfo' | 'review' | 'confirmation';

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
    id: 'yubikey-5-nfc',
    productId: 1,
    name: 'YubiKey 5 NFC',
    description: 'USB-A with NFC for mobile and desktop',
    price: 50,
    currency: 'USD',
    formFactor: 'USB-A',
    capabilities: ['FIDO2', 'U2F', 'Smart Card', 'OTP', 'NFC'],
  },
  {
    id: 'yubikey-5-nano',
    productId: 2,
    name: 'YubiKey 5 Nano',
    description: 'Ultra-small USB-A for laptops',
    price: 60,
    currency: 'USD',
    formFactor: 'Nano',
    capabilities: ['FIDO2', 'U2F', 'Smart Card', 'OTP'],
  },
  {
    id: 'yubikey-5c',
    productId: 3,
    name: 'YubiKey 5C',
    description: 'USB-C for modern devices',
    price: 55,
    currency: 'USD',
    formFactor: 'USB-C',
    capabilities: ['FIDO2', 'U2F', 'Smart Card', 'OTP'],
  },
  {
    id: 'yubikey-5c-nano',
    productId: 4,
    name: 'YubiKey 5C Nano',
    description: 'Ultra-small USB-C for laptops',
    price: 65,
    currency: 'USD',
    formFactor: 'Nano',
    capabilities: ['FIDO2', 'U2F', 'Smart Card', 'OTP'],
  },
];

const YubiKeyOrderFlowInternal: React.FC<YubiKeyOrderFlowProps> = ({
  apiClient,
  userEmail = 'user@example.com',
  userId = '',
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
  // Use userId from function argument
  const propUserId = userId;
  const [shipment, setShipment] = useState<any>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const resellerOrgId = 'YUBI-RESELLER-ORG-001';

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

  // After address, go directly to review
  const handleAddressNext = () => {
    if (shippingAddress) {
      setCurrentStep('review');
    }
  }

  // Removed unused handleUserInfoNext function

  const handleReviewConfirm = async () => {
    if (!shippingAddress || selectedProducts.length === 0 || !userId.trim()) {
      return;
    }

    setIsSubmitting(true);

    try {
      const shipmentRequest: ShipmentRequest = {
        user_id: userId.trim(),
        pin_request: {
          type: "generate",
          length: 8,
        },
        yubico_shipment_request: {
          delivery_type: 1,
          recipient: {
            recipient_company: "Yubico",
            recipient_email: userEmail,
            recipient_firstname: shippingAddress?.firstName || "",
            recipient_lastname: shippingAddress?.lastName || "",
            recipient_telephone: shippingAddress?.phone || "",
          },
          mailing_address: {
            street_line1: shippingAddress?.addressLine1 || "",
            street_line2: shippingAddress?.addressLine2 || "",
            city: shippingAddress?.city || "",
            region: shippingAddress?.stateProvince || "",
            postal_code: shippingAddress?.postalCode || "",
            country_code_2: shippingAddress?.country || "US",
          },
          shipment_items: [
            {
              product_id: 3,
              inventory_product_id: 133,
              product_quantity: 1,
              customization_id: "test00"
            }
          ],
        },
      };

      const shipment = await apiClient.createShipment(shipmentRequest);
      // Use returned shipment object for confirmation
      if (shipment && shipment.shipment_id) {
        const mappedShipment = {
          id: shipment.shipment_id,
          orderId: '',
          status: 'PROCESSING',
          products: selectedProducts.map((item) => ({
            product: item.product,
            quantity: item.quantity,
            isPrimary: item.isPrimary || false,
          })),
          shippingAddress: shippingAddress,
          userEmail: userEmail || '',
          requestDate: new Date().toISOString(),
          requestor: shippingAddress?.firstName + ' ' + shippingAddress?.lastName,
          trackingNumber: '',
          carrier: '',
          estimatedDelivery: '',
          actualDelivery: '',
          metadata: {},
        };
        setShipment(mappedShipment);
        setCurrentStep('confirmation');
        if (onComplete) {
          onComplete({ shipment_id: shipment.shipment_id });
        }
      } else {
        throw new Error('Shipment creation failed or did not return a valid shipment_id');
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
      case 'userInfo':
        setCurrentStep('address');
        break;
      case 'review':
        setCurrentStep('userInfo');
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
            userId={propUserId}
            resellerOrgId={resellerOrgId}
            onConfirm={handleReviewConfirm}
            onBack={handleBack}
            isSubmitting={isSubmitting}
          />
        );
      case 'confirmation':
        return shipment ? (
          <>
            <OrderStatus
              shipment={shipment}
              onBack={onCancel}
            />
            <Box mt={3}>
              <Typography variant="subtitle1" sx={{ fontWeight: 500 }}>Keycloak User ID</Typography>
              <Typography variant="body2" sx={{ mb: 2 }}>{propUserId}</Typography>
            </Box>
          </>
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