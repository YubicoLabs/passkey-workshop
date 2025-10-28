import type { Meta, StoryObj } from '@storybook/react';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { YubiKeyOrderFlow } from '@/components/YubiKeyOrderFlow';
import { createApiClient } from '@/services/api-client';
import { theme } from '@/theme';

const meta = {
  title: 'YubiKey LaunchPad/Order Flow',
  component: YubiKeyOrderFlow,
  parameters: {
    layout: 'fullscreen',
    docs: {
      description: {
        component: 'Complete YubiKey ordering flow with product selection, address validation, and order confirmation.',
      },
    },
  },
  decorators: [
    (Story) => (
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Story />
      </ThemeProvider>
    ),
  ],
  args: {
    apiClient: createApiClient({ baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api' }),
    userEmail: 'demo@example.com',
  },
} satisfies Meta<typeof YubiKeyOrderFlow>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    onComplete: (shipment) => {
      console.log('Order completed:', shipment);
    },
  },
};

export const ProductSelection: Story = {
  name: 'Step 1: Product Selection',
  args: {
    onComplete: (shipment) => {
      console.log('Order completed:', shipment);
    },
  },
  parameters: {
    docs: {
      description: {
        story: 'The first step where users select their primary and backup YubiKeys.',
      },
    },
  },
};

export const WithoutTheme: Story = {
  name: 'Without Theme (Unstyled)',
  decorators: [
    (Story) => <Story /> // No ThemeProvider
  ],
  parameters: {
    docs: {
      description: {
        story: 'Shows how the component looks without any theme applied. Consumers should wrap the component with their own ThemeProvider.',
      },
    },
  },
};

export const WithCustomContainer: Story = {
  name: 'With Custom Container Props',
  args: {
    containerProps: {
      maxWidth: 'lg',
      sx: { backgroundColor: '#f5f5f5', py: 6 }
    },
    paperProps: {
      elevation: 3,
      sx: { borderRadius: 4 }
    }
  },
  parameters: {
    docs: {
      description: {
        story: 'Demonstrates how to customize the container and paper components.',
      },
    },
  },
};

export const WithCustomTheme: Story = {
  name: 'With Custom Theme',
  decorators: [
    (Story) => {
      const customTheme = {
        ...theme,
        palette: {
          ...theme.palette,
          primary: {
            main: '#FF6B35', // Orange instead of green
          },
        },
      };
      
      return (
        <ThemeProvider theme={customTheme}>
          <CssBaseline />
          <Story />
        </ThemeProvider>
      );
    },
  ],
  parameters: {
    docs: {
      description: {
        story: 'Example with a custom theme using different colors.',
      },
    },
  },
};

export const PreSelectedProducts: Story = {
  name: 'With Pre-selected Products',
  render: (args) => {
    // This demonstrates how a consumer might pre-populate the form
    // In real usage, they would manage this state externally
    return <YubiKeyOrderFlow {...args} />;
  },
  args: {
    onComplete: (shipment) => {
      console.log('Order completed:', shipment);
      alert(`Order ${shipment.orderId} placed successfully!`);
    },
  },
  parameters: {
    docs: {
      description: {
        story: 'Shows how the component behaves with initial state. In production, this would be managed by the parent component.',
      },
    },
  },
};