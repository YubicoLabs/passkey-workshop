import { useAuth as useOidcAuth } from 'react-oidc-context';

export interface AuthUser {
  sub: string;
  email?: string;
  name?: string;
  preferred_username?: string;
  access_token: string;
}

export interface UseAuthReturn {
  user: AuthUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: Error | null;
  login: () => void;
  logout: () => void;
}

export const useAuth = (): UseAuthReturn => {
  const auth = useOidcAuth();

  const user: AuthUser | null = auth.isAuthenticated && auth.user
    ? {
        sub: auth.user.profile.sub as string,
        email: auth.user.profile.email as string | undefined,
        name: auth.user.profile.name as string | undefined,
        preferred_username: auth.user.profile.preferred_username as string | undefined,
        access_token: auth.user.access_token,
      }
    : null;

  return {
    user,
    isAuthenticated: auth.isAuthenticated,
    isLoading: auth.isLoading,
    error: auth.error || null,
    login: () => auth.signinRedirect(),
    logout: () => auth.signoutRedirect(),
  };
};
