import { z } from 'zod';

// Product schemas
export const ProductSchema = z.object({
  id: z.string(),
  productId: z.number().optional(),
  name: z.string(),
  description: z.string(),
  price: z.number(),
  currency: z.string().default('USD'),
  image: z.string().optional(),
  formFactor: z.enum(['USB-A', 'USB-C', 'NFC', 'Nano']),
  capabilities: z.array(z.string()),
});

export const SelectedProductSchema = z.object({
  product: ProductSchema,
  quantity: z.number().min(1).default(1),
  isPrimary: z.boolean().default(false),
});

// Type exports
export type Product = z.infer<typeof ProductSchema>;
export type SelectedProduct = z.infer<typeof SelectedProductSchema>;
