import type { Meta, StoryObj } from '@storybook/react';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { OrderHistory } from '../components/order-management/OrderHistory';
import { theme } from '@/theme';
import type { Shipment } from '@/types/api';

// Create query client for stories
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
      refetchOnWindowFocus: false,
    },
  },
});

// --------------------
// Mock data builders
// --------------------
const baseAddress = {
  firstName: 'John',
  lastName: 'Doe',
  addressLine1: '123 Main St',
  addressLine2: '',
  city: 'New York',
  stateProvince: 'NY',
  country: 'US',
  postalCode: '10001',
  phone: '555-123-4567',
};

const mockProducts = [
  {
    product: {
      id: 'yubikey-5c-nfc',
      name: 'YubiKey 5C NFC',
      description: 'USB-C security key with NFC',
      price: 55,
      currency: 'USD',
      formFactor: 'USB-C' as const,
      capabilities: ['FIDO2', 'U2F', 'NFC'],
      inStock: true,
    },
    quantity: 1,
    isPrimary: true,
  },
  {
    product: {
      id: 'yubikey-5-nfc',
      name: 'YubiKey 5 NFC',
      description: 'USB-A security key with NFC',
      price: 50,
      currency: 'USD',
      formFactor: 'USB-A' as const,
      capabilities: ['FIDO2', 'U2F', 'NFC'],
      inStock: true,
    },
    quantity: 1,
    isPrimary: false,
  },
];

const createShipment = (
  id: string,
  status: Shipment['status'],
  daysAgo: number,
  overrides?: Partial<Shipment>
): Shipment => {
  const requestDate = new Date();
  requestDate.setDate(requestDate.getDate() - daysAgo);
  
  return {
    id: `SHIP-${id}`,
    orderId: `#OPENAI${Math.floor(Math.random() * 10000)}`,
    status,
    products: mockProducts,
    shippingAddress: baseAddress,
    userEmail: 'john.doe@example.com',
    requestDate: requestDate.toISOString(),
    requestor: 'john.doe@example.com',
    trackingNumber: status === 'SHIPPED' || status === 'DELIVERED' ? '1Z999AA10123456784' : null,
    carrier: status === 'SHIPPED' || status === 'DELIVERED' ? 'UPS' : null,
    estimatedDelivery: status === 'SHIPPED' ? new Date(Date.now() + 2 * 86400000).toISOString() : null,
    actualDelivery: status === 'DELIVERED' ? requestDate.toISOString() : null,
    metadata: null,
    ...overrides,
  };
};

// --------------------
// Mock data sets
// --------------------
const multipleOrders: Shipment[] = [
  createShipment('001', 'DELIVERED', 30),
  createShipment('002', 'SHIPPED', 2, {
    trackingNumber: '1Z999AA10123456785',
  }),
  createShipment('003', 'PROCESSING', 1),
  createShipment('004', 'PENDING', 0),
  createShipment('005', 'DELIVERED', 60),
];

const singleOrder: Shipment[] = [
  createShipment('001', 'SHIPPED', 1),
];

const cancelledOrders: Shipment[] = [
  createShipment('001', 'CANCELLED', 5, {
    metadata: {
      reasonCode: 'ADDRESS_VALIDATION_FAILED',
      message: 'Unable to validate shipping address',
    },
  }),
  createShipment('002', 'CANCELLED', 10, {
    metadata: {
      reasonCode: 'USER_CANCELLED',
      message: 'Cancelled by user request',
    },
  }),
  createShipment('003', 'DELIVERED', 20),
];

// --------------------
// Storybook meta
// --------------------
const meta = {
  title: 'LaunchPad/Order History',
  component: OrderHistory,
  parameters: {
    layout: 'padded',
    docs: {
      description: {
        component: 'Displays a list of all user orders with their current status. Click on an order to view details.',
      },
    },
  },
  decorators: [
    (Story) => (
      <QueryClientProvider client={queryClient}>
        <ThemeProvider theme={theme}>
          <CssBaseline />
          <div style={{ padding: 24, maxWidth: 900 }}>
            <Story />
          </div>
        </ThemeProvider>
      </QueryClientProvider>
    ),
  ],
} satisfies Meta<typeof OrderHistory>;

export default meta;
type Story = StoryObj<typeof meta>;

// --------------------
// Stories
// --------------------
export const Default: Story = {
  args: {
    getShipments: async () => ({
      shipments: multipleOrders,
      total: multipleOrders.length,
    }),
    onOrderSelect: (shipment) => {
      console.log('Selected order:', shipment);
    },
  },
};

export const EmptyState: Story = {
  args: {
    getShipments: async () => ({
      shipments: [],
      total: 0,
    }),
  },
  parameters: {
    docs: {
      description: {
        story: 'Shows the empty state when no orders exist.',
      },
    },
  },
};

export const LoadingState: Story = {
  args: {
    getShipments: async () => {
      // Simulate loading
      await new Promise(resolve => setTimeout(resolve, 10000));
      return { shipments: [], total: 0 };
    },
    loading: true,
  },
  parameters: {
    docs: {
      description: {
        story: 'Shows skeleton loaders while orders are being fetched.',
      },
    },
  },
};

export const ErrorState: Story = {
  args: {
    getShipments: async () => {
      throw new Error('Failed to load orders');
    },
    error: 'Unable to load your orders. Please check your connection and try again.',
  },
  parameters: {
    docs: {
      description: {
        story: 'Shows error state when orders fail to load.',
      },
    },
  },
};

export const SingleOrder: Story = {
  args: {
    getShipments: async () => ({
      shipments: singleOrder,
      total: 1,
    }),
    onOrderSelect: (shipment) => {
      console.log('Selected order:', shipment);
    },
  },
  parameters: {
    docs: {
      description: {
        story: 'Shows a single order in the history.',
      },
    },
  },
};

export const WithCancelledOrders: Story = {
  args: {
    getShipments: async () => ({
      shipments: cancelledOrders,
      total: cancelledOrders.length,
    }),
    onOrderSelect: (shipment) => {
      console.log('Selected order:', shipment);
    },
  },
  parameters: {
    docs: {
      description: {
        story: 'Shows orders with cancellation reasons.',
      },
    },
  },
};

export const MixedStatuses: Story = {
  args: {
    getShipments: async () => {
      const orders = [
        createShipment('001', 'PENDING', 0),
        createShipment('002', 'PROCESSING', 1),
        createShipment('003', 'SHIPPED', 3),
        createShipment('004', 'DELIVERED', 7),
        createShipment('005', 'CANCELLED', 14, {
          metadata: {
            reasonCode: 'ADDRESS_VALIDATION_FAILED',
            message: 'Invalid address',
          },
        }),
      ];
      return { shipments: orders, total: orders.length };
    },
    onOrderSelect: (shipment) => {
      console.log('Selected order:', shipment);
    },
  },
  parameters: {
    docs: {
      description: {
        story: 'Shows orders in various states to demonstrate all status indicators.',
      },
    },
  },
};

export const WithoutClickHandler: Story = {
  args: {
    getShipments: async () => ({
      shipments: multipleOrders,
      total: multipleOrders.length,
    }),
    // No onOrderSelect - cards won't be clickable
  },
  parameters: {
    docs: {
      description: {
        story: 'Orders displayed without click interaction (no onOrderSelect handler).',
      },
    },
  },
};

export const LongHistory: Story = {
  args: {
    getShipments: async () => {
      const orders = Array.from({ length: 20 }, (_, i) => {
        const statuses: Shipment['status'][] = ['PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];
        const status = statuses[i % statuses.length];
        return createShipment(String(i).padStart(3, '0'), status, i * 5);
      });
      return { shipments: orders, total: orders.length };
    },
    onOrderSelect: (shipment) => {
      console.log('Selected order:', shipment);
    },
  },
  parameters: {
    docs: {
      description: {
        story: 'Shows a long list of orders to test scrolling and performance.',
      },
    },
  },
};