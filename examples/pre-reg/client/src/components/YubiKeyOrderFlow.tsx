import React from 'react';
import { 
  Container, 
  Paper, 
  Box, 
  Typography, 
  CircularProgress, 
  Alert, 
  Button,
  Fade
} from '@mui/material';
import { CheckCircle } from 'lucide-react';
import { useMutation } from '@tanstack/react-query';

import { ProductSelection } from './order-flow/ProductSelection';
import { AddressForm } from './order-flow/AddressForm';
import { OrderReview } from './order-flow/OrderReview';
import ErrorBoundary from './common/ErrorBoundary';

import { 
  Product, 
  Shipment, 
  ShipmentRequest 
} from '@/types/api';
import { YubiKeyApiClient } from '@/services/api-client';
import { useOrderFlow } from '@/features/orders/hooks/useOrderFlow';

export interface YubiKeyOrderFlowProps {
  apiClient: YubiKeyApiClient;
  userEmail?: string;
  keycloakUserId?: string;
  products?: Product[];
  onComplete?: (shipment: Shipment) => void;
  onCancel?: () => void;
  containerProps?: React.ComponentProps<typeof Container>;
  paperProps?: React.ComponentProps<typeof Paper>;
}

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

const useSubmitOrder = (apiClient: YubiKeyApiClient, onSuccess: (data: Shipment) => void) => {
  return useMutation({
    mutationFn: async (payload: ShipmentRequest) => {
      return await apiClient.createShipment(payload);
    },
    onSuccess,
  });
};

const SuccessView = ({ shipmentId, onReset }: { shipmentId: string; onReset: () => void }) => (
  <Box textAlign="center" py={6}>
    <Fade in>
      <Box>
        <CheckCircle size={64} color="#388E3C" style={{ marginBottom: 16 }} />
        <Typography variant="h4" gutterBottom>
          Order Confirmed!
        </Typography>
        <Typography color="text.secondary" paragraph>
          Your shipment has been created successfully.
        </Typography>
        <Typography variant="subtitle1" sx={{ fontFamily: 'monospace', mb: 4, bgcolor: 'grey.100', p: 1, borderRadius: 1, display: 'inline-block' }}>
          ID: {shipmentId}
        </Typography>
        <Box>
          <Button variant="contained" onClick={onReset}>
            Place Another Order
          </Button>
        </Box>
      </Box>
    </Fade>
  </Box>
);

const YubiKeyOrderFlowInternal: React.FC<YubiKeyOrderFlowProps> = ({
  apiClient,
  userEmail = 'user@example.com',
  keycloakUserId,
  products = defaultProducts, // Uses the restored default data
  onComplete,
  onCancel,
  containerProps,
  paperProps = {},
}) => {
  const {
    step,
    setStep,
    selectedProducts,
    setSelectedProducts,
    address,
    setAddress,
    createdShipment,
    setCreatedShipment,
    goToNext,
    goToBack,
  } = useOrderFlow(products);

  const { mutate: submitOrder, isPending, error: submitError } = useSubmitOrder(apiClient, (data) => {
    setCreatedShipment(data);
    setStep('success');
    if (onComplete) onComplete(data);
  });

  const handleReviewConfirm = () => {
    if (!address || !keycloakUserId) {
      console.error("Missing address or User ID");
      return;
    }

    // Construct the payload based on the strict Zod schema in types/api.ts
    const payload: ShipmentRequest = {
      user_id: keycloakUserId,
      pin_request: { type: 'generate', length: 8 },
      yubico_shipment_request: {
        delivery_type: 1,
        recipient: {
          recipient_company: 'Yubico', // Or derive from props
          recipient_email: userEmail,
          recipient_firstname: address.firstName,
          recipient_lastname: address.lastName,
          recipient_telephone: address.phone,
        },
        mailing_address: {
          street_line1: address.addressLine1,
          street_line2: address.addressLine2 || undefined,
          city: address.city,
          region: address.stateProvince,
          postal_code: address.postalCode,
          country_code_2: address.country,
        },
        // Mapping selected products to shipment items
        shipment_items: selectedProducts.map(sp => ({
          product_id: sp.product.productId || 0, // Fallback if ID missing
          inventory_product_id: 133, // Ideally mocked or dynamic
          product_quantity: sp.quantity,
          customization_id: 'standard-config'
        })),
      },
    };

    submitOrder(payload);
  };

  const renderStepContent = () => {
    switch (step) {
      case 'products':
        return (
          <ProductSelection
            products={products}
            selectedProducts={selectedProducts}
            onProductsChange={setSelectedProducts}
            onNext={goToNext}
          />
        );
      
      case 'address':
        return (
          <AddressForm
            address={address}
            onAddressChange={setAddress}
            onNext={goToNext}
            onBack={goToBack}
          />
        );

      case 'review':
        if (!address) return null;
        return (
          <>
            {submitError && (
              <Alert severity="error" sx={{ mb: 3 }}>
                Order submission failed. Please try again.
              </Alert>
            )}
            <OrderReview
              selectedProducts={selectedProducts}
              shippingAddress={address}
              onConfirm={handleReviewConfirm}
              onBack={goToBack}
              isSubmitting={isPending}
            />
          </>
        );

      case 'success':
        return createdShipment ? (
            <SuccessView 
              shipmentId={createdShipment.shipment_id} 
              onReset={onCancel || (() => window.location.reload())} 
            />
        ) : <CircularProgress />;

      default:
        return null;
    }
  };

  return (
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
        {!keycloakUserId && step !== 'success' && (
          <Alert severity="warning" sx={{ mb: 3 }}>
            Development Mode: No Keycloak User ID detected. Submission may fail.
          </Alert>
        )}
        
        {renderStepContent()}
      </Paper>
    </Container>
  );
};

export const YubiKeyOrderFlow: React.FC<YubiKeyOrderFlowProps> = (props) => (
  <ErrorBoundary>
    <YubiKeyOrderFlowInternal {...props} />
  </ErrorBoundary>
);