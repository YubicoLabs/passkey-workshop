import { AuthProviderProps } from 'react-oidc-context';
import { WebStorageStateStore } from 'oidc-client-ts';

export const oidcConfig: AuthProviderProps = {
  authority: 'http://localhost:8081/realms/fido-connector-dev',
  client_id: 'fido-connector-ui',
  redirect_uri: window.location.origin,
  automaticSilentRenew: true,
  scope: 'openid profile email',
  userStore: new WebStorageStateStore({ store: window.sessionStorage }),
  onSigninCallback: () => {
    window.history.replaceState({}, document.title, window.location.pathname);
  },
};
