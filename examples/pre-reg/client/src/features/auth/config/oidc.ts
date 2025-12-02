import { AuthProviderProps } from 'react-oidc-context';
import { WebStorageStateStore } from 'oidc-client-ts';
import { env } from '@/config/env';

export const oidcConfig = {
  authority: env.VITE_OIDC_AUTHORITY,
  client_id: env.VITE_OIDC_CLIENT_ID,
  redirect_uri: env.VITE_OIDC_REDIRECT_URI,
  automaticSilentRenew: true,
  scope: env.VITE_OIDC_SCOPE,
  userStore: new WebStorageStateStore({ store: window.sessionStorage }),
  onSigninCallback: () => {
    window.history.replaceState({}, document.title, window.location.pathname);
  },
} satisfies AuthProviderProps;
