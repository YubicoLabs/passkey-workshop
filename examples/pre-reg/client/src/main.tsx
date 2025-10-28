import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './index.css';

// Start MSW in development
async function enableMocking() {
  if (process.env.NODE_ENV !== 'development') {
    return;
  }

  const { setupWorker } = await import('msw/browser');
  const { handlers } = await import('./mocks/handlers');

  const worker = setupWorker(...handlers);

  // Start the worker with onUnhandledRequest set to bypass
  return worker.start({
    onUnhandledRequest: 'bypass',
  });
}
 /*
enableMocking().then(() => {
  ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
      <App />
    </React.StrictMode>
  );
});
*/

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
      <App />
    </React.StrictMode>
);