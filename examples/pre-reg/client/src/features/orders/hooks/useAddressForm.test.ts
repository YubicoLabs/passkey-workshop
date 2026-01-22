import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useAddressForm } from './useAddressForm';
import type { Address } from '@/features/orders/types';

const mockAddress: Address = {
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

describe('useAddressForm', () => {
  let mockOnAddressChange: ReturnType<typeof vi.fn>;
  let mockOnServerValidate: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    mockOnAddressChange = vi.fn();
    mockOnServerValidate = vi.fn().mockResolvedValue({ validated: true });
  });

  it('initializes with provided address', () => {
    const { result } = renderHook(() =>
      useAddressForm({
        initialAddress: mockAddress,
        onAddressChange: mockOnAddressChange,
        onServerValidate: mockOnServerValidate,
      })
    );

    expect(result.current.formData.firstName).toBe('John');
    expect(result.current.validationStatus).toBe('idle');
  });

  it('updates field and notifies parent', () => {
    const { result } = renderHook(() =>
      useAddressForm({
        initialAddress: null,
        onAddressChange: mockOnAddressChange,
        onServerValidate: mockOnServerValidate,
      })
    );

    act(() => {
      result.current.setFieldValue('firstName', 'Jane');
    });

    expect(result.current.formData.firstName).toBe('Jane');
    expect(mockOnAddressChange).toHaveBeenCalled();
  });

  it('shows errors for invalid data', async () => {
    const { result } = renderHook(() =>
      useAddressForm({
        initialAddress: null,
        onAddressChange: mockOnAddressChange,
        onServerValidate: mockOnServerValidate,
      })
    );

    await act(async () => {
      await result.current.validate();
    });

    expect(result.current.validationStatus).toBe('error');
    expect(result.current.fieldErrors.firstName).toBeDefined();
  });

  it('calls server validation for valid data', async () => {
    const { result } = renderHook(() =>
      useAddressForm({
        initialAddress: mockAddress,
        onAddressChange: mockOnAddressChange,
        onServerValidate: mockOnServerValidate,
      })
    );

    await act(async () => {
      await result.current.validate();
    });

    expect(mockOnServerValidate).toHaveBeenCalled();
    expect(result.current.validationStatus).toBe('validated');
  });

  it('normalizes US state name to abbreviation', async () => {
    const { result } = renderHook(() =>
      useAddressForm({
        initialAddress: { ...mockAddress, stateProvince: 'Texas' },
        onAddressChange: mockOnAddressChange,
        onServerValidate: mockOnServerValidate,
      })
    );

    await act(async () => {
      await result.current.validate();
    });

    expect(mockOnServerValidate).toHaveBeenCalledWith(
      expect.objectContaining({ stateProvince: 'TX' })
    );
  });

  it('normalizes Canadian province to abbreviation', async () => {
    const { result } = renderHook(() =>
      useAddressForm({
        initialAddress: {
          ...mockAddress,
          country: 'CA',
          stateProvince: 'Ontario',
          postalCode: 'K1A 0B1',
        },
        onAddressChange: mockOnAddressChange,
        onServerValidate: mockOnServerValidate,
      })
    );

    await act(async () => {
      await result.current.validate();
    });

    expect(mockOnServerValidate).toHaveBeenCalledWith(
      expect.objectContaining({ stateProvince: 'ON' })
    );
  });
});
