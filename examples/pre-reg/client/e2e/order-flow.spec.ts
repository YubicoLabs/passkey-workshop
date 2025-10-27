import { test, expect } from '@playwright/test';

test.describe('YubiKey Order Flow - Happy Path', () => {
  test('a user can complete the entire order flow successfully', async ({ page }) => {
    // Navigate to the app
    await page.goto('http://localhost:5173');
    
    // 1. Product Selection Step
    await expect(page.getByRole('heading', { name: 'Get your YubiKeys' })).toBeVisible();

    // Select primary key (first USB-C)
    await page.locator('input[name="primary-key"]').first().click();

    // Select backup key (first USB-A)
    await page.locator('input[name="backup-key"]').last().click();
    
    // Click Next
    const productNextButton = page.getByRole('button', { name: 'Next' });
    await expect(productNextButton).toBeEnabled();
    await productNextButton.click();

    // 2. Address Form Step
    await expect(page.getByRole('heading', { name: '2 • Address' })).toBeVisible();

    // Fill out the form
    await page.getByLabel('First Name').fill('John');
    await page.getByLabel('Last Name').fill('Doe');
    await page.getByLabel('Address Line 1').fill('123 Main Street');
    await page.getByLabel('City').fill('San Francisco');
    
    // Select country and state
    await page.getByLabel('Country').click();
    await page.getByRole('option', { name: 'United States' }).click();
    
    await page.getByLabel('State/Province').click();
    await page.getByRole('option', { name: 'California' }).click();
    
    await page.getByLabel('Postal Code').fill('94105');
    await page.getByLabel('Phone').fill('555-123-4567');

    // Validate Address
    const validateButton = page.getByRole('button', { name: 'Validate Address' });
    await expect(validateButton).toBeEnabled();
    await validateButton.click();
    
    // Wait for validation success and click Next
    await expect(page.getByText('Shipping address validated successfully')).toBeVisible();
    const addressNextButton = page.getByRole('button', { name: 'Next' });
    await expect(addressNextButton).toBeEnabled();
    await addressNextButton.click();

    // 3. Review Step
    await expect(page.getByRole('heading', { name: 'Review and confirm your selections' })).toBeVisible();
    
    // Verify details
    await expect(page.getByText('Primary key: Security Key (USB-C)')).toBeVisible();
    await expect(page.getByText('Backup key: Security Key (USB-A)')).toBeVisible();
    await expect(page.getByText('John')).toBeVisible();
    await expect(page.getByText('94105')).toBeVisible();

    // Confirm and submit
    const confirmButton = page.getByRole('button', { name: 'Confirm and submit' });
    await expect(confirmButton).toBeEnabled();
    await confirmButton.click();

    // 4. Confirmation Step
    await expect(page.getByRole('heading', { name: /Confirmed: YubiKey order/ })).toBeVisible();
    await expect(page.getByText('Success! Your YubiKey request has been confirmed')).toBeVisible();
    await expect(page.getByText(/Requestor: demo@example.com/)).toBeVisible();
  });
});