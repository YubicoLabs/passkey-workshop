import { z } from 'zod';

// Shipment API Schema
export const ShipmentItemSchema = z.object({
  product_id: z.number(),
  inventory_product_id: z.number(),
  product_quantity: z.number(),
  customization_id: z.string(),
});

export const MailingAddressSchema = z.object({
  street_line1: z.string(),
  street_line2: z.string().optional(),
  city: z.string(),
  region: z.string(),
  postal_code: z.string(),
  country_code_2: z.string().length(2),
});

export const RecipientSchema = z.object({
  recipient_company: z.string(),
  recipient_email: z.string().email(),
  recipient_firstname: z.string(),
  recipient_lastname: z.string(),
  recipient_telephone: z.string(),
});

export const YubicoShipmentRequestSchema = z.object({
  delivery_type: z.number(),
  recipient: RecipientSchema,
  mailing_address: MailingAddressSchema,
  shipment_items: z.array(ShipmentItemSchema),
});

export const PinRequestSchema = z.object({
  type: z.string(),
  length: z.number(),
});

export const ShipmentRequestSchema = z.object({
  user_id: z.string(),
  pin_request: PinRequestSchema,
  yubico_shipment_request: YubicoShipmentRequestSchema,
});

export const ShipmentSchema = z.object({
  shipment_id: z.string()
});

// Type exports
export type Shipment = z.infer<typeof ShipmentSchema>;
export type ShipmentRequest = z.infer<typeof ShipmentRequestSchema>;
