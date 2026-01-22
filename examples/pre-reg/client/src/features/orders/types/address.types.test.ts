import { describe, it, expect } from 'vitest';
import { 
  AddressSchema, 
  AddressWithValidationSchema,
} from './address.types';

const validAddress = {
  firstName: 'John',
  lastName: 'Doe',
  addressLine1: '123 Main St',
  addressLine2: '',
  city: 'Austin',
  stateProvince: 'TX',
  postalCode: '78701',
  country: 'US',
  phone: '512-555-1234',
};

describe('AddressSchema', () => {
  it('accepts valid address', () => {
    const result = AddressSchema.safeParse(validAddress);
    expect(result.success).toBe(true);
  });

  it('rejects missing required fields', () => {
    const result = AddressSchema.safeParse({ ...validAddress, firstName: '' });
    expect(result.success).toBe(false);
  });

  it('transforms country to uppercase', () => {
    const result = AddressSchema.safeParse({ ...validAddress, country: 'us' });
    expect(result.success).toBe(true);
    if (result.success) {
      expect(result.data.country).toBe('US');
    }
  });
});

describe('AddressWithValidationSchema', () => {
  it('accepts valid US postal code', () => {
    const result = AddressWithValidationSchema.safeParse({
      ...validAddress,
      postalCode: '78701-1234',
    });
    expect(result.success).toBe(true);
  });

  it('rejects invalid US postal code', () => {
    const result = AddressWithValidationSchema.safeParse({
      ...validAddress,
      postalCode: '7870',
    });
    expect(result.success).toBe(false);
  });

  it('accepts valid Canadian postal code', () => {
    const result = AddressWithValidationSchema.safeParse({
      ...validAddress,
      country: 'CA',
      postalCode: 'K1A 0B1',
      stateProvince: 'ON',
    });
    expect(result.success).toBe(true);
  });

  it('rejects invalid Canadian postal code', () => {
    const result = AddressWithValidationSchema.safeParse({
      ...validAddress,
      country: 'CA',
      postalCode: '12345',
      stateProvince: 'ON',
    });
    expect(result.success).toBe(false);
  });
});
