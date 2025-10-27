import type { Preview } from '@storybook/react';
import { initialize, mswLoader } from 'msw-storybook-addon';
import { handlers } from '../src/mocks/handlers';

// Initialize MSW
initialize({ 
  onUnhandledRequest: 'bypass',
  serviceWorker: {
    url: '/mockServiceWorker.js'
  }
});

const preview: Preview = {
  parameters: {
    actions: { argTypesRegex: '^on[A-Z].*' },
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/,
      },
    },
    msw: { 
      handlers,
    },
  },
  loaders: [mswLoader],
};

export default preview;