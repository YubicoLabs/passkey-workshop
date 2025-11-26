import { z } from 'zod';

// Validation patterns
const latinCharOnlyRegex = /^[0-9A-Za-z #'.,-/()&\u00C0-\u017F]*$/;
const usPostalCodeRegex = /^\d{5}(-\d{4})?$/;
const caPostalCodeRegex = /^[A-Z]\d[A-Z]\s?\d[A-Z]\d$/i;
const phoneRegex = /^[\d\s\-\(\)\+\.]+$/;

// Country codes that have restricted address requirements
const restrictedCountries = [
  'US', 'SE', 'AT', 'AU', 'BE', 'BG', 'CA', 'CH', 'CL', 'CY', 'CZ', 'DE',
  'DK', 'EE', 'ES', 'FI', 'FR', 'GB', 'GR', 'HK', 'HR', 'HU', 'ID', 'IE',
  'IL', 'IS', 'IT', 'JP', 'LI', 'LT', 'LU', 'LV', 'MT', 'MX', 'NL', 'NO',
  'NZ', 'PH', 'PL', 'PT', 'RO', 'SA', 'SG', 'SK', 'SI', 'TH', 'TW', 'AE'
] as const;

// Base address schema with proper validations
export const AddressSchema = z.object({
  firstName: z.string()
    .min(1, 'First name is required')
    .max(15, 'Cannot exceed 15 characters')
    .regex(latinCharOnlyRegex, 'Only Latin characters are allowed'),
  lastName: z.string()
    .min(1, 'Last name is required')
    .max(20, 'Cannot exceed 20 characters')
    .regex(latinCharOnlyRegex, 'Only Latin characters are allowed'),
  addressLine1: z.string()
    .min(1, 'Address is required')
    .max(60, 'Cannot exceed 60 characters')
    .regex(latinCharOnlyRegex, 'Only Latin characters are allowed'),
  addressLine2: z.string()
    .max(60, 'Cannot exceed 60 characters')
    .regex(latinCharOnlyRegex, 'Only Latin characters are allowed')
    .transform(val => val || undefined)
    .optional()
    .nullable(),
  city: z.string()
    .min(1, 'City is required')
    .max(60, 'Cannot exceed 60 characters')
    .regex(latinCharOnlyRegex, 'Only Latin characters are allowed'),
  stateProvince: z.string()
    .min(1, 'State/Province is required')
    .max(50, 'Cannot exceed 50 characters'),
  postalCode: z.string()
    .min(1, 'Postal code is required')
    .max(50, 'Cannot exceed 50 characters'),
  country: z.string()
    .length(2, 'Must be 2-letter country code')
    .transform(val => val.toUpperCase()),
  phone: z.string()
    .min(1, 'Phone number is required')
    .max(40, 'Cannot exceed 40 characters')
    .regex(phoneRegex, 'Invalid phone number format'),
});

// Relaxed address schema for non-restricted countries
export const RelaxedAddressSchema = z.object({
  firstName: z.string()
    .min(1, 'First name is required')
    .max(255, 'Cannot exceed 255 characters')
    .regex(latinCharOnlyRegex, 'Only Latin characters are allowed'),
  lastName: z.string()
    .min(1, 'Last name is required')
    .max(255, 'Cannot exceed 255 characters')
    .regex(latinCharOnlyRegex, 'Only Latin characters are allowed'),
  addressLine1: z.string()
    .min(1, 'Address is required')
    .max(255, 'Cannot exceed 255 characters')
    .regex(latinCharOnlyRegex, 'Only Latin characters are allowed'),
  addressLine2: z.string()
    .max(255, 'Cannot exceed 255 characters')
    .regex(latinCharOnlyRegex, 'Only Latin characters are allowed')
    .transform(val => val || undefined)
    .optional()
    .nullable(),
  city: z.string()
    .min(1, 'City is required')
    .max(255, 'Cannot exceed 255 characters')
    .regex(latinCharOnlyRegex, 'Only Latin characters are allowed'),
  stateProvince: z.string()
    .min(1, 'State/Province is required')
    .max(255, 'Cannot exceed 255 characters'),
  postalCode: z.string()
    .min(1, 'Postal code is required')
    .max(255, 'Cannot exceed 255 characters'),
  country: z.string()
    .length(2, 'Must be 2-letter country code')
    .transform(val => val.toUpperCase()),
  phone: z.string()
    .min(1, 'Phone number is required')
    .max(40, 'Cannot exceed 40 characters')
    .regex(phoneRegex, 'Invalid phone number format'),
});

// Address with country-specific validation
export const AddressWithValidationSchema = AddressSchema.superRefine((data, ctx) => {
  // US postal code validation
  if (data.country === 'US') {
    if (!usPostalCodeRegex.test(data.postalCode)) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: 'Invalid US postal code format (12345 or 12345-6789)',
        path: ['postalCode'],
      });
    }
  }

  // Canadian postal code validation
  if (data.country === 'CA') {
    if (!caPostalCodeRegex.test(data.postalCode)) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: 'Invalid Canadian postal code format (A1A 1A1)',
        path: ['postalCode'],
      });
    }
  }

  // Use relaxed validation for non-restricted countries
  if (!restrictedCountries.includes(data.country as any)) {
    const relaxedResult = RelaxedAddressSchema.safeParse(data);
    if (!relaxedResult.success) {
      relaxedResult.error.issues.forEach(issue => {
        ctx.addIssue(issue);
      });
    }
  }
});

export const ValidateAddressRequestSchema = z.object({
  address: AddressWithValidationSchema,
});

export const ValidateAddressResponseSchema = z.object({
  validated: z.boolean(),
  suggestedAddress: AddressSchema.optional(),
  errors: z.array(z.string()).optional(),
});

// Validate API Schema
export const ApiValidateAddressRequestSchema = z.object({
  street_line1: z.string().max(60),
  street_line2: z.string().max(60).optional(),
  street_line3: z.string().max(60).optional(), // deprecated but may still be in response
  city: z.string().max(60),
  postal_code: z.string().max(50),
  region: z.string().max(50).optional(),
  country_code_2: z.string().length(2)
});

export const ApiValidateAddressResponseSchema = z.object({
  status: z.string(),
  details: z.array(z.any()).optional(), // Array of error details
  address: z.object({
    street_line1: z.string(),
    street_line2: z.string().optional(),
    street_line3: z.string().optional(),
    city: z.string(),
    postal_code: z.string(),
    region: z.string().optional(),
    country_code_2: z.string()
  }).optional()
});

// Type exports
export type Address = z.infer<typeof AddressSchema>;
export type ValidateAddressRequest = z.infer<typeof ValidateAddressRequestSchema>;
export type ValidateAddressResponse = z.infer<typeof ValidateAddressResponseSchema>;
export type ApiValidateAddressRequest = z.infer<typeof ApiValidateAddressRequestSchema>;
export type ApiValidateAddressResponse = z.infer<typeof ApiValidateAddressResponseSchema>;
