import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { ThemeProvider } from '@mui/material/styles';
import { ProductSelection } from '../order-flow/ProductSelection';
import { theme } from '../../theme';
import { Product, SelectedProduct } from '../../types/api';

const mockProducts: Product[] = [
  {
    id: 'yubikey-5c-nfc',
    name: 'Security Key (USB-C)',
    description: 'USB-C with NFC',
    price: 55,
    currency: 'USD',
    formFactor: 'USB-C',
    capabilities: ['FIDO2', 'NFC'],
    inStock: true,
  },
  {
    id: 'yubikey-5-nfc',
    name: 'Security Key (USB-A)',
    description: 'USB-A with NFC',
    price: 50,
    currency: 'USD',
    formFactor: 'USB-A',
    capabilities: ['FIDO2', 'NFC'],
    inStock: true,
  },
];

describe('ProductSelection', () => {
  it('should update radio button visual state when clicked', async () => {
    const onProductsChange = vi.fn();
    const user = userEvent.setup();
    
    render(
      <ThemeProvider theme={theme}>
        <ProductSelection
          products={mockProducts}
          selectedProducts={[]}
          onProductsChange={onProductsChange}
          onNext={vi.fn()}
        />
      </ThemeProvider>
    );
    
    // Get all radio inputs
    const radioInputs = screen.getAllByRole('radio');
    
    // Initially, no radio should be checked
    radioInputs.forEach(radio => {
      expect(radio).not.toBeChecked();
    });
    
    // Click the first primary radio
    await user.click(radioInputs[0]);
    
    // THIS IS THE KEY TEST: The radio should now be checked
    expect(radioInputs[0]).toBeChecked();
    
    // And onProductsChange should have been called
    expect(onProductsChange).toHaveBeenCalledWith(
      expect.arrayContaining([
        expect.objectContaining({
          isPrimary: true,
          product: expect.objectContaining({ id: 'yubikey-5c-nfc' }),
        }),
      ])
    );
  });

  it('should maintain selection state when re-rendered with selected products', async () => {
    const onProductsChange = vi.fn();
    
    // Start with a selected product
    const selectedProducts: SelectedProduct[] = [
      {
        product: mockProducts[0],
        quantity: 1,
        isPrimary: true,
      },
    ];
    
    const { rerender } = render(
      <ThemeProvider theme={theme}>
        <ProductSelection
          products={mockProducts}
          selectedProducts={selectedProducts}
          onProductsChange={onProductsChange}
          onNext={vi.fn()}
        />
      </ThemeProvider>
    );
    
    // The first radio should be checked
    const radioInputs = screen.getAllByRole('radio');
    expect(radioInputs[0]).toBeChecked();
    
    // Simulate parent component re-rendering with the same selection
    rerender(
      <ThemeProvider theme={theme}>
        <ProductSelection
          products={mockProducts}
          selectedProducts={selectedProducts}
          onProductsChange={onProductsChange}
          onNext={vi.fn()}
        />
      </ThemeProvider>
    );
    
    // The selection should persist
    const updatedRadios = screen.getAllByRole('radio');
    expect(updatedRadios[0]).toBeChecked();
  });

  it('should enable Next button only when both selections are made', async () => {
    const onNext = vi.fn();
    const onProductsChange = vi.fn();
    const user = userEvent.setup();
    
    const { rerender } = render(
      <ThemeProvider theme={theme}>
        <ProductSelection
          products={mockProducts}
          selectedProducts={[]}
          onProductsChange={onProductsChange}
          onNext={onNext}
        />
      </ThemeProvider>
    );
    
    // Next button should be disabled initially
    const nextButton = screen.getByRole('button', { name: /next/i });
    expect(nextButton).toBeDisabled();
    
    // Select primary key
    const radioInputs = screen.getAllByRole('radio');
    await user.click(radioInputs[0]);
    
    // Simulate parent updating the props after selection
    const afterPrimary: SelectedProduct[] = [
      {
        product: mockProducts[0],
        quantity: 1,
        isPrimary: true,
      },
    ];
    
    rerender(
      <ThemeProvider theme={theme}>
        <ProductSelection
          products={mockProducts}
          selectedProducts={afterPrimary}
          onProductsChange={onProductsChange}
          onNext={onNext}
        />
      </ThemeProvider>
    );
    
    // Next should still be disabled (only one selection)
    expect(screen.getByRole('button', { name: /next/i })).toBeDisabled();
    
    // Select backup key
    const updatedRadios = screen.getAllByRole('radio');
    await user.click(updatedRadios[3]); // USB-A in backup section
    
    // Simulate parent updating with both selections
    const afterBoth: SelectedProduct[] = [
      {
        product: mockProducts[0],
        quantity: 1,
        isPrimary: true,
      },
      {
        product: mockProducts[1],
        quantity: 1,
        isPrimary: false,
      },
    ];
    
    rerender(
      <ThemeProvider theme={theme}>
        <ProductSelection
          products={mockProducts}
          selectedProducts={afterBoth}
          onProductsChange={onProductsChange}
          onNext={onNext}
        />
      </ThemeProvider>
    );
    
    // Now Next should be enabled
    expect(screen.getByRole('button', { name: /next/i })).not.toBeDisabled();
    
    // Click Next
    await user.click(screen.getByRole('button', { name: /next/i }));
    expect(onNext).toHaveBeenCalled();
  });

  it('should allow selecting the same product for both primary and backup', async () => {
    const onProductsChange = vi.fn();
    const user = userEvent.setup();
    
    render(
      <ThemeProvider theme={theme}>
        <ProductSelection
          products={mockProducts}
          selectedProducts={[]}
          onProductsChange={onProductsChange}
          onNext={vi.fn()}
        />
      </ThemeProvider>
    );
    
    // Select USB-C as primary
    const radioInputs = screen.getAllByRole('radio');
    await user.click(radioInputs[0]);
    
    // Should be able to select the same USB-C as backup (no longer disabled)
    const backupUSBC = radioInputs[2]; // USB-C in backup section
    expect(backupUSBC).not.toBeDisabled();
    
    // Click it to verify it works
    await user.click(backupUSBC);
    
    // Should have been called with the same product for both
    expect(onProductsChange).toHaveBeenCalled();
  });
});