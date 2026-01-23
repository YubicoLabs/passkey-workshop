import { z } from 'zod';

// Country schema
export const DeliveryTypeSchema = z.object({
  value: z.number(),
  name: z.string(),
});

export const CountryApiSchema = z.object({
  country_id: z.number(),
  country_name: z.string(),
  country_code_2: z.string().length(2),
  country_code_3: z.string().length(3),
  country_vat_rate: z.number(),
  delivery_types: z.array(DeliveryTypeSchema),
  states: z.array(z.object({ code: z.string(), name: z.string() })).optional(),
});

export const CountriesResponseSchema = z.object({
  count: z.number(),
  total_count: z.number(),
  countries: z.array(CountryApiSchema),
});

// Type exports
export type DeliveryType = z.infer<typeof DeliveryTypeSchema>;
export type Country = z.infer<typeof CountryApiSchema>;
export type CountriesResponse = z.infer<typeof CountriesResponseSchema>;
