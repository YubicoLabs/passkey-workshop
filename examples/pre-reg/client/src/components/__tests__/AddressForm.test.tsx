import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { ThemeProvider } from '@mui/material/styles';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AddressForm } from '../order-flow/AddressForm';
import { theme } from '../../theme';
import { Country } from '../../types/api';

const mockCountries: Country[] = [
  {
    code: 'US',
    name: 'United States',
    states: [{ code: 'CA', name: 'California' }],
  },
];

const queryClient = new QueryClient();

const renderComponent = () => {
  const onAddressChange = vi.fn();
  const onValidate = vi.fn().mockResolvedValue({ validated: true });
  const onNext = vi.fn();
  const onBack = vi.fn();
  const getCountries = vi.fn().mockResolvedValue(mockCountries);

  render(
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <AddressForm
          address={null}
          onAddressChange={onAddressChange}
          onValidate={onValidate}
          onNext={onNext}
          onBack={onBack}
          getCountries={getCountries}
        />
      </ThemeProvider>
    </QueryClientProvider>
  );

  return { onAddressChange, onValidate, onNext, onBack, getCountries };
};

describe('AddressForm', () => {
  it('should render all required fields', () => {
    renderComponent();
    expect(screen.getByLabelText(/first name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/last name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/address line 1/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/city/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/country/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/postal code/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/phone/i)).toBeInTheDocument();
  });

  it('should have a disabled Validate button when form is empty', () => {
    renderComponent();
    const validateButton = screen.getByRole('button', { name: /validate address/i });
    // This is the fix: assert the button is disabled initially.
    expect(validateButton).toBeDisabled();
  });

  it('should enable validate button only when form is complete', async () => {
    renderComponent();
    const user = userEvent.setup();
    const validateButton = screen.getByRole('button', { name: /validate address/i });

    expect(validateButton).toBeDisabled();

    // Fill form
    await user.type(screen.getByLabelText(/first name/i), 'John');
    await user.type(screen.getByLabelText(/last name/i), 'Doe');
    await user.type(screen.getByLabelText(/address line 1/i), '123 Main St');
    await user.type(screen.getByLabelText(/city/i), 'Anytown');
    await user.type(screen.getByLabelText(/postal code/i), '12345');
    await user.type(screen.getByLabelText(/phone/i), '555-555-5555');

    // Select country (which will load states)
    await user.click(screen.getByLabelText(/country/i));
    await user.click(await screen.findByRole('option', { name: /united states/i }));
    
    // Select state
    await user.click(screen.getByLabelText(/state\/province/i));
    await user.click(await screen.findByRole('option', { name: /california/i }));

    // Now the button should be enabled
    await waitFor(() => {
      expect(validateButton).not.toBeDisabled();
    });
  });

  it('should call onValidate and then onNext after successful validation', async () => {
    const { onValidate, onNext } = renderComponent();
    const user = userEvent.setup();
    const validateButton = screen.getByRole('button', { name: /validate address/i });

    // Fill form
    await user.type(screen.getByLabelText(/first name/i), 'John');
    await user.type(screen.getByLabelText(/last name/i), 'Doe');
    await user.type(screen.getByLabelText(/address line 1/i), '123 Main St');
    await user.type(screen.getByLabelText(/city/i), 'Anytown');
    await user.type(screen.getByLabelText(/postal code/i), '12345');
    await user.type(screen.getByLabelText(/phone/i), '555-555-5555');
    await user.click(screen.getByLabelText(/country/i));
    await user.click(await screen.findByRole('option', { name: /united states/i }));
    await user.click(screen.getByLabelText(/state\/province/i));
    await user.click(await screen.findByRole('option', { name: /california/i }));

    // Click validate
    await user.click(validateButton);

    // Assert onValidate was called
    await waitFor(() => {
      expect(onValidate).toHaveBeenCalledTimes(1);
    });

    // Success message and Next button should appear
    expect(await screen.findByText(/shipping address validated successfully/i)).toBeInTheDocument();
    const nextButton = screen.getByRole('button', { name: /next/i });
    expect(nextButton).toBeInTheDocument();

    // Click next
    await user.click(nextButton);
    expect(onNext).toHaveBeenCalledTimes(1);
  });
});