# YubiKey LaunchPad 🔐

A secure, embeddable React component library for ordering pre-registered YubiKeys. Accelerate the adoption of phishing-resistant MFA by integrating a seamless ordering experience into your application.

## Features

- 🎨 **Theme Agnostic** - Bring your own Material UI theme
- 🔒 **Secure by Design** - Built-in address validation and secure API communication
- 🌍 **Internationalization** - Full i18n support with react-i18next
- 📱 **Responsive Design** - Works seamlessly on desktop and mobile devices
- 🧪 **Comprehensive Testing** - Unit tests, integration tests, and Storybook stories
- 📦 **Easy Integration** - Simple npm install and minimal configuration

## Quick Start

### Installation

```bash
npm install yubikey-launchpad
```

### Basic Usage

```tsx
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { YubiKeyOrderFlow } from 'yubikey-launchpad';
import { createApiClient } from 'yubikey-launchpad/api';

// Create your theme (or use an existing one)
const theme = createTheme({
  palette: {
    primary: {
      main: '#9ACA3C', // YubiKey green
    },
  },
  typography: {
    fontFamily: '"Helvetica Neue", Helvetica, Arial, sans-serif',
  },
});

function App() {
  const apiClient = createApiClient({
    baseURL: 'https://api.your-domain.com',
    apiKey: 'your-api-key',
    getIdToken: async () => {
      // Return the current user's ID token
      return await getAuthToken();
    }
  });

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <YubiKeyOrderFlow
        apiClient={apiClient}
        userEmail="user@example.com"
        onComplete={(shipment) => {
          console.log('Order completed:', shipment);
        }}
      />
    </ThemeProvider>
  );
}
```

## Development Setup

### Prerequisites

- Node.js 18+
- npm 9+

### Install Dependencies

```bash
npm install
```

### Run Development Server

```bash
npm run dev
```

The app will be available at http://localhost:5173

### Run Storybook

```bash
npm run storybook
```

Storybook will be available at http://localhost:6006

### Run Tests

```bash
# Run tests
npm test

# Run tests with UI
npm run test:ui

# Run tests with coverage
npm run test:coverage
```

## Component API

### YubiKeyOrderFlow

The main component for the complete order flow.

```tsx
interface YubiKeyOrderFlowProps {
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
```

#### Props

- `apiClient` (required) - Configured API client instance
- `userEmail` - Email address of the user placing the order
- `onComplete` - Callback when order is successfully placed
- `onCancel` - Callback when user cancels the order flow
- `locale` - Locale for internationalization
- `translations` - Custom translations object
- `products` - Array of available products (defaults to YubiKey 5C NFC and 5 NFC)
- `containerProps` - Props to pass to the MUI Container component
- `paperProps` - Props to pass to the MUI Paper component

### YubiKeyOrderHistory

Display a list of all orders for a user.

```tsx
interface YubiKeyOrderHistoryProps {
  apiClient: YubiKeyApiClient;
  onOrderSelect?: (orderId: string) => void;
  pageSize?: number;
}
```

### YubiKeyOrderStatus

Display detailed status for a specific order.

```tsx
interface YubiKeyOrderStatusProps {
  shipment: Shipment;
  onBack?: () => void;
}
```

## Theming

The components are designed to work with Material UI's theming system. You must wrap the components with a `ThemeProvider`:

```tsx
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';

const myTheme = createTheme({
  palette: {
    primary: {
      main: '#your-brand-color',
    },
  },
  typography: {
    fontFamily: 'Your Font, sans-serif',
  },
});

function App() {
  return (
    <ThemeProvider theme={myTheme}>
      <CssBaseline />
      <YubiKeyOrderFlow {...props} />
    </ThemeProvider>
  );
}
```

### Customizing Container and Paper

You can customize the wrapper components using the `containerProps` and `paperProps`:

```tsx
<YubiKeyOrderFlow
  apiClient={apiClient}
  containerProps={{
    maxWidth: 'lg',
    sx: { backgroundColor: '#f5f5f5' }
  }}
  paperProps={{
    elevation: 3,
    sx: { borderRadius: 4 }
  }}
/>
```

## API Integration

### Creating an API Client

```typescript
import { createApiClient } from 'yubikey-launchpad/api';

const apiClient = createApiClient({
  baseURL: 'https://api.your-domain.com',
  apiKey: 'your-api-key',
  getIdToken: async () => {
    return await yourAuthProvider.getIdToken();
  }
});
```

### Available API Methods

- `getProducts()` - Fetch available YubiKey products
- `validateAddress(address)` - Validate a shipping address
- `createShipment(request)` - Create a new shipment order
- `getShipment(shipmentId)` - Get shipment details
- `getShipments(params)` - List all shipments
- `getCountries()` - Get supported countries

## Internationalization

### Providing Translations

```tsx
const frenchTranslations = {
  'Order Your YubiKey': 'Commandez votre YubiKey',
  'Shipping Information': 'Informations de livraison',
  // ... more translations
};

<YubiKeyOrderFlow
  apiClient={apiClient}
  locale="fr-FR"
  translations={frenchTranslations}
/>
```

## Mock Data with MSW

For development and testing, the library includes Mock Service Worker (MSW) handlers.

```typescript
import { handlers } from 'yubikey-launchpad/mocks';
import { setupWorker } from 'msw/browser';

const worker = setupWorker(...handlers);
worker.start();
```

## Architecture Decisions

### Why No Built-in Theme?

This library is designed as a reference architecture that integrates into existing applications. Most production applications already have their own Material UI theme, so we don't impose our own. This approach:

- Prevents theme conflicts
- Reduces bundle size
- Gives you complete control over styling
- Follows Material UI best practices

### Component Structure

The library is built with atomic design principles:

```
YubiKeyOrderFlow (Main Container)
├── ProductSelection (Step 1)
├── AddressForm (Step 2)
├── OrderReview (Step 3)
└── OrderStatus (Confirmation)
```

Each component can be used independently if needed, though the main flow component handles all orchestration.

## Project Structure

```
yubikey-launchpad/
├── src/
│   ├── components/
│   │   ├── order-flow/       # Order creation components
│   │   ├── order-management/ # Order history/status components
│   │   └── common/           # Shared components
│   ├── services/             # API client
│   ├── types/                # TypeScript types
│   ├── mocks/                # MSW mock handlers
│   ├── theme/                # Example theme (for dev/demo)
│   ├── i18n/                 # Internationalization
│   └── stories/              # Storybook stories
├── public/                   # Static assets
├── .storybook/              # Storybook configuration
└── tests/                   # Test files
```

## Best Practices

1. **Theming**: Always wrap the component with your `ThemeProvider` and include `CssBaseline`
2. **Security**: Always validate user input and use HTTPS in production
3. **Error Handling**: Implement proper error boundaries and fallback UI
4. **Performance**: Use React Query for caching and optimistic updates
5. **Accessibility**: All components follow WCAG 2.1 guidelines
6. **Testing**: Write tests for critical user flows

## Migration Guide

### From v0.x (with built-in theme) to v1.x (bring your own theme)

If you were using an earlier version that included a built-in theme:

```tsx
// Old (v0.x)
<YubiKeyOrderFlow customTheme={myTheme} />

// New (v1.x)
<ThemeProvider theme={myTheme}>
  <CssBaseline />
  <YubiKeyOrderFlow />
</ThemeProvider>
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For questions and support, please open an issue in the GitHub repository or contact support@your-domain.com

## Acknowledgments

- Built with [React](https://reactjs.org/)
- UI components from [Material-UI](https://mui.com/)
- State management with [TanStack Query](https://tanstack.com/query)
- Mocking with [MSW](https://mswjs.io/)
- Documentation with [Storybook](https://storybook.js.org/)