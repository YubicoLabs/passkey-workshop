import { z } from 'zod';

/**
 * Environment variable validation schema
 *
 * This ensures all required environment variables are present and valid
 * at application startup. The app will fail fast if configuration is missing.
 */
const envSchema = z.object({
  VITE_API_BASE_URL: z.string().url('VITE_API_BASE_URL must be a valid URL'),
  VITE_OIDC_AUTHORITY: z.string().url('VITE_OIDC_AUTHORITY must be a valid URL'),
  VITE_OIDC_CLIENT_ID: z.string().min(1, 'VITE_OIDC_CLIENT_ID is required'),
  VITE_OIDC_REDIRECT_URI: z.string().url('VITE_OIDC_REDIRECT_URI must be a valid URL'),
  VITE_OIDC_SCOPE: z.string().default('openid profile email'),
});

/**
 * Validated environment variables
 *
 * Access environment variables through this object to ensure type safety
 * and runtime validation.
 *
 * @throws {ZodError} If required environment variables are missing or invalid
 */
export const env = envSchema.parse(import.meta.env);
