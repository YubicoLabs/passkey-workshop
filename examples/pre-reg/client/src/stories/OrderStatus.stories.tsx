import type { Meta, StoryObj } from '@storybook/react';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { OrderStatus } from '../components/order-management/OrderStatus';
import { theme } from '@/theme';
import type { Shipment } from '@/types/api';

// --------------------
// Mock data (matches OrderStatus prop shape)
// --------------------
const baseAddress = {
  firstName: 'John',
  lastName: 'Doe',
  addressLine1: '123 Main St',
  addressLine2: 'Apt 4B',
  city: 'Anytown',
  stateProvince: 'CA',
  country: 'US',
  postalCode: '12345',
  phone: '+1 (555) 123-4567',
};

const baseProducts = [
  {
    product: {
      id: 'yk-5c-nano',
      name: 'YubiKey 5C Nano',
      description: 'Compact security key with USB-C connector',
      price: 55,
      currency: 'USD',
      image: undefined,
      formFactor: 'USB-C', // one of: 'USB-A' | 'USB-C' | 'NFC' | 'Nano'
      capabilities: ['FIDO2', 'U2F'],
      inStock: true,
    },
    quantity: 1,
    isPrimary: true,
  },
  {
    product: {
      id: 'lanyard',
      name: 'YubiKey Lanyard',
      description: 'Accessory for carrying your YubiKey',
      price: 5,
      currency: 'USD',
      image: undefined,
      formFactor: 'NFC',
      capabilities: [],
      inStock: true,
    },
    quantity: 1,
    isPrimary: false,
  },
];

/**
 * Build a Shipment object matching the component's expected shape.
 * Status values used: 'PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'
 */
const buildShipment = (status: Shipment['status'], opts?: Partial<Shipment>): Shipment => {
  const now = new Date();
  const iso = (d: Date) => d.toISOString();

  return {
    id: `shipment-${status.toLowerCase()}`,
    orderId: `YK-${status.slice(0, 3).toUpperCase()}-1001`,
    status,
    products: baseProducts,
    shippingAddress: baseAddress,
    userEmail: 'john.doe@example.com',
    requestDate: iso(now),
    requestor: 'john.doe@example.com',
    trackingNumber:
      status === 'SHIPPED' || status === 'DELIVERED' ? '1Z12AB4567890' : null,
    carrier: status === 'SHIPPED' || status === 'DELIVERED' ? 'UPS' : null,
    estimatedDelivery:
      status === 'SHIPPED' ? iso(new Date(now.getTime() + 3 * 24 * 60 * 60 * 1000)) : null,
    actualDelivery:
      status === 'DELIVERED' ? iso(new Date(now.getTime() - 24 * 60 * 60 * 1000)) : null,
    metadata: opts?.metadata ?? null,
    ...opts,
  } as Shipment;
};

// --------------------
// Storybook meta
// --------------------
const meta = {
  title: 'LaunchPad/Order Status',
  component: OrderStatus,
  parameters: {
    layout: 'padded',
    docs: {
      description: {
        component:
          'Developer-focused stories for the OrderStatus component. By default the story wraps the component with the app MUI theme. Status mapping to console docs is shown per story.',
      },
    },
  },
  decorators: [
    (Story) => (
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <div style={{ padding: 24, maxWidth: 900 }}>
          <Story />
        </div>
      </ThemeProvider>
    ),
  ],
} satisfies Meta<typeof OrderStatus>;

export default meta;
type Story = StoryObj<typeof meta>;

// --------------------
// Stories (each maps to a console-aligned state)
// --------------------
export const AwaitingValidation: Story = {
  args: {
    // console label: "Awaiting Validation" -> component status 'PENDING'
    shipment: buildShipment('PENDING'),
  },
  parameters: {
    docs: {
      story:
        'Maps to console docs: "Awaiting Validation". Component status used: PENDING.',
    },
  },
};

export const AcceptedForFulfillment: Story = {
  args: {
    // console label: "Accepted for Fulfillment" -> component status 'PROCESSING'
    shipment: buildShipment('PROCESSING'),
  },
  parameters: {
    docs: {
      story:
        'Maps to console docs: "Accepted for Fulfillment". Component status used: PROCESSING.',
    },
  },
};

export const Shipped: Story = {
  args: {
    // console label: "Shipped" -> component status 'SHIPPED'
    shipment: buildShipment('SHIPPED'),
  },
  parameters: {
    docs: {
      story:
        'Shipped (includes carrier and tracking number). Component status used: SHIPPED.',
    },
  },
};

export const Delivered: Story = {
  args: {
    // console label: "Delivered" -> component status 'DELIVERED'
    shipment: buildShipment('DELIVERED'),
  },
  parameters: {
    docs: {
      story:
        'Delivered (shows delivery date). Component status used: DELIVERED.',
    },
  },
};

export const AddressValidationFailed: Story = {
  args: {
    // The ShipmentStatus enum does not have a dedicated "ADDRESS_VALIDATION_FAILED" value.
    // Use CANCELLED with metadata to indicate failure reason. Consider updating component
    // to surface metadata.reason explicitly if you want a distinct UI state.
    shipment: buildShipment('CANCELLED', {
      metadata: { reasonCode: 'ADDRESS_VALIDATION_FAILED', message: 'Address validation failed' },
    }),
  },
  parameters: {
    docs: {
      story:
        'Address validation failed. We represent this using status=CANCELLED + metadata.reasonCode. Update component to render special UI for this code if desired.',
    },
  },
};

export const Cancelled: Story = {
  args: {
    shipment: buildShipment('CANCELLED', { metadata: { reasonCode: 'USER_CANCELLED', message: 'Cancelled by user' } }),
  },
  parameters: {
    docs: {
      story: 'Generic cancelled shipment example.',
    },
  },
};

// --------------------
// Variants: theme control
// --------------------
export const WithoutTheme: Story = {
  name: 'Without Theme (unstyled)',
  args: {
    shipment: buildShipment('SHIPPED'),
  },
  decorators: [
    // intentionally no ThemeProvider so consumers can see unstyled baseline
    (Story) => (
      <div style={{ padding: 24, maxWidth: 900 }}>
        <Story />
      </div>
    ),
  ],
  parameters: {
    docs: {
      story:
        'Shows the component without the app MUI theme. Consumers should wrap with ThemeProvider when integrating.',
    },
  },
};

export const WithCustomTheme: Story = {
  name: 'With Custom Theme',
  args: {
    shipment: buildShipment('PROCESSING'),
  },
  decorators: [
    (Story) => {
      const customTheme = {
        ...theme,
        palette: {
          ...theme.palette,
          primary: { ...((theme.palette as any).primary ?? {}), main: '#FF6B35' },
        },
      };
      return (
        <ThemeProvider theme={customTheme}>
          <CssBaseline />
          <div style={{ padding: 24, maxWidth: 900 }}>
            <Story />
          </div>
        </ThemeProvider>
      );
    },
  ],
  parameters: {
    docs: {
      story:
        'Demonstrates theming override. Replace `theme` with your app theme to integrate.',
    },
  },
};