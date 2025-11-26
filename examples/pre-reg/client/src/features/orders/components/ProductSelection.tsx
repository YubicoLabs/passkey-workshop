import React from 'react';
import { Box, Card, CardContent, Typography, Radio, FormControlLabel, RadioGroup, Alert, Button, Chip, Stack, styled } from '@mui/material';
import { Wifi as WifiIcon, Key as KeyIcon } from 'lucide-react';
import DOMPurify from 'dompurify';
import { Product, SelectedProduct } from '@/features/orders/types';

const SectionTitle = styled(Typography)(({ theme }) => ({
  fontWeight: 500,
  marginBottom: theme.spacing(1),
  marginTop: theme.spacing(3), // Handles the top spacing automatically
}));

const NextButton = styled(Button)(({ theme }) => ({
  marginTop: theme.spacing(4), // Handles its own spacing
  paddingBlock: theme.spacing(1.5),
  backgroundColor: '#000',
  '&:hover': { backgroundColor: '#333' },
  '&.Mui-disabled': { backgroundColor: 'rgba(0, 0, 0, 0.12)' },
}));

const StyledCard = styled(Card, {
  shouldForwardProp: (prop) => prop !== 'isSelected',
})<{ isSelected: boolean }>(({ theme, isSelected }) => ({
  marginBottom: theme.spacing(2),
  borderWidth: isSelected ? 2 : 1,
  borderStyle: 'solid',
  borderColor: isSelected ? theme.palette.primary.main : theme.palette.divider,
  transition: 'all 0.2s',
  cursor: 'pointer',
  '&:hover': {
    borderColor: theme.palette.primary.light,
    boxShadow: theme.shadows[2],
  },
}));

const LinkText = styled('a')(({ theme }) => ({
  color: theme.palette.primary.main,
  textDecoration: 'none',
  '&:hover': { textDecoration: 'underline' },
}));

const ProductCard = ({ product, isSelected, groupName }: { product: Product; isSelected: boolean; groupName: string }) => {
  const cleanDescription = DOMPurify.sanitize(product.description);

  return (
    <StyledCard isSelected={isSelected} variant="outlined">
      <CardContent>
        <FormControlLabel
          value={product.id}
          control={<Radio name={groupName} checked={isSelected} />}
          label={
            <Box ml={1} width="100%">
              <Stack direction="row" spacing={2} alignItems="center">
                <KeyIcon size={24} />
                <Box flex={1}>
                  <Typography variant="h6">{product.name}</Typography>
                  <Typography
                    variant="body2"
                    color="text.secondary"
                    dangerouslySetInnerHTML={{ __html: cleanDescription }}
                  />
                  <Stack direction="row" spacing={1} mt={1}>
                    <Chip size="small" label={product.formFactor} color="primary" variant="outlined" />
                    {product.capabilities.includes('NFC') && (
                      <Chip size="small" label="NFC" icon={<WifiIcon size={14} />} variant="outlined" />
                    )}
                  </Stack>
                </Box>
                <Typography variant="h6" color="primary">${product.price}</Typography>
              </Stack>
            </Box>
          }
          sx={{ width: '100%', margin: 0 }}
        />
      </CardContent>
    </StyledCard>
  );
};

interface ProductSelectionProps {
  products: Product[];
  selectedProducts: SelectedProduct[];
  onProductsChange: (products: SelectedProduct[]) => void;
  onNext: () => void;
}

export const ProductSelection: React.FC<ProductSelectionProps> = ({
  products,
  selectedProducts,
  onProductsChange,
  onNext,
}) => {
  const primaryKeyId = selectedProducts.find(p => p.isPrimary)?.product.id || '';
  const backupKeyId = selectedProducts.find(p => !p.isPrimary)?.product.id || '';
  const isComplete = primaryKeyId && backupKeyId;

  const handleSelection = (id: string, isPrimary: boolean) => {
    const currentPrimaryId = isPrimary ? id : primaryKeyId;
    const currentBackupId = !isPrimary ? id : backupKeyId;

    const newSelected: SelectedProduct[] = [];
    const primaryProd = products.find(p => p.id === currentPrimaryId);
    const backupProd = products.find(p => p.id === currentBackupId);

    if (primaryProd) newSelected.push({ product: primaryProd, quantity: 1, isPrimary: true });
    if (backupProd) newSelected.push({ product: backupProd, quantity: 1, isPrimary: false });

    onProductsChange(newSelected);
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>Get your YubiKeys</Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        To protect our users against account takeovers, we're rolling out the use of security keys.
      </Typography>

      <Box mt={4}>
        <Typography variant="h5" gutterBottom>1 • Select your products</Typography>

        <Typography variant="body2" color="text.secondary" gutterBottom>
          Don't know what a YubiKey is?{' '}
          <LinkText href="https://www.yubico.com" target="_blank" rel="noopener noreferrer">
            Learn more
          </LinkText>
        </Typography>

        {!isComplete && (
          <Alert severity="info" sx={{ my: 2 }}>Select a total of 2 products to continue.</Alert>
        )}

        <SectionTitle variant="subtitle1">Please select your primary key</SectionTitle>
        <RadioGroup value={primaryKeyId} onChange={(e) => handleSelection(e.target.value, true)}>
          {products.map(product => (
            <ProductCard
              key={`primary-${product.id}`}
              product={product}
              isSelected={primaryKeyId === product.id}
              groupName="primary-key-group"
            />
          ))}
        </RadioGroup>

        <SectionTitle variant="subtitle1">Please select your backup key</SectionTitle>
        <RadioGroup value={backupKeyId} onChange={(e) => handleSelection(e.target.value, false)}>
          {products.map(product => (
            <ProductCard
              key={`backup-${product.id}`}
              product={product}
              isSelected={backupKeyId === product.id}
              groupName="backup-key-group"
            />
          ))}
        </RadioGroup>

        <NextButton
          variant="contained"
          size="large"
          fullWidth
          disabled={!isComplete}
          onClick={onNext}
        >
          Next
        </NextButton>
      </Box>
    </Box>
  );
};