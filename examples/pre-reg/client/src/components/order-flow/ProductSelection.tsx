import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Radio,
  FormControlLabel,
  RadioGroup,
  Alert,
  Button,
  Chip,
  Stack,
} from '@mui/material';
import { Wifi as WifiIcon, Key as KeyIcon } from 'lucide-react';
import { Product, SelectedProduct } from '@/types/api';
import DOMPurify from 'dompurify';

interface ProductSelectionProps {
  products: Product[];
  selectedProducts: SelectedProduct[];
  onProductsChange: (products: SelectedProduct[]) => void;
  onNext: () => void;
  maxProducts?: number;
}

export const ProductSelection: React.FC<ProductSelectionProps> = ({
  products,
  selectedProducts,
  onProductsChange,
  onNext,
}) => {
  // Initialize state from props
  const [primaryKeyId, setPrimaryKeyId] = useState<string>(() => {
    const primary = selectedProducts.find(p => p.isPrimary);
    return primary?.product.id || '';
  });
  
  const [backupKeyId, setBackupKeyId] = useState<string>(() => {
    const backup = selectedProducts.find(p => !p.isPrimary);
    return backup?.product.id || '';
  });

  // Sync with prop changes
  useEffect(() => {
    const primary = selectedProducts.find(p => p.isPrimary);
    const backup = selectedProducts.find(p => !p.isPrimary);
    
    if (primary?.product.id !== primaryKeyId) {
      setPrimaryKeyId(primary?.product.id || '');
    }
    if (backup?.product.id !== backupKeyId) {
      setBackupKeyId(backup?.product.id || '');
    }
  }, [selectedProducts]);

  const handlePrimaryChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const productId = event.target.value;
    setPrimaryKeyId(productId);
    updateSelectedProducts(productId, backupKeyId);
  };

  const handleBackupChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const productId = event.target.value;
    setBackupKeyId(productId);
    updateSelectedProducts(primaryKeyId, productId);
  };

  const updateSelectedProducts = (primaryId: string, backupId: string) => {
    const newSelected: SelectedProduct[] = [];
    
    if (primaryId) {
      const primaryProduct = products.find(p => p.id === primaryId);
      if (primaryProduct) {
        newSelected.push({
          product: primaryProduct,
          quantity: 1,
          isPrimary: true,
        });
      }
    }
    
    if (backupId) {
      const backupProduct = products.find(p => p.id === backupId);
      if (backupProduct) {
        newSelected.push({
          product: backupProduct,
          quantity: 1,
          isPrimary: false,
        });
      }
    }
    
    onProductsChange(newSelected);
  };

  const isComplete = primaryKeyId && backupKeyId;

  const renderProductCard = (product: Product, isPrimary: boolean) => {
    const isSelected = isPrimary 
      ? primaryKeyId === product.id 
      : backupKeyId === product.id;

    // Sanitize description
    const cleanDescription = DOMPurify.sanitize(product.description);

    return (
      <Card
        key={`${isPrimary ? 'primary' : 'backup'}-${product.id}`}
        sx={{
          mb: 2,
          border: isSelected ? 2 : 1,
          borderColor: isSelected ? 'primary.main' : 'divider',
          transition: 'all 0.2s',
          cursor: 'pointer',
          '&:hover': {
            borderColor: 'primary.light',
            boxShadow: 2,
          },
        }}
      >
        <CardContent>
          <FormControlLabel
            control={
              <Radio
                checked={isSelected}
                value={product.id}
                name={isPrimary ? 'primary-key' : 'backup-key'}
              />
            }
            label={
              <Box sx={{ ml: 1 }}>
                <Stack direction="row" spacing={2} alignItems="center">
                  <KeyIcon size={24} />
                  <Box flex={1}>
                    <Typography variant="h6">
                      {product.name}
                    </Typography>
                    <Typography 
                      variant="body2" 
                      color="text.secondary"
                      dangerouslySetInnerHTML={{ __html: cleanDescription }}
                    />
                    <Stack direction="row" spacing={1} mt={1}>
                      <Chip 
                        size="small" 
                        label={product.formFactor}
                        color="primary"
                        variant="outlined"
                      />
                      {product.capabilities.includes('NFC') && (
                        <Chip 
                          size="small" 
                          label="NFC"
                          icon={<WifiIcon size={14} />}
                          variant="outlined"
                        />
                      )}
                    </Stack>
                  </Box>
                  <Typography variant="h6" color="primary">
                    ${product.price}
                  </Typography>
                </Stack>
              </Box>
            }
          />
        </CardContent>
      </Card>
    );
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Get your YubiKeys
      </Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        To protect our users against account takeovers, we're rolling out the
        use of security keys. Order YubiKeys directly to your home.
      </Typography>

      <Box mt={4}>
        <Typography variant="h5" gutterBottom>
          1 • Select your products
        </Typography>
        
        <Typography variant="body2" color="text.secondary" gutterBottom>
          Don't know what a YubiKey is?{' '}
          <a href="https://www.yubico.com/why-yubico/" target="_blank" rel="noopener noreferrer">
            Learn more
          </a>
        </Typography>

        {!isComplete && (
          <Alert severity="info" sx={{ my: 2 }}>
            Select a total of 2 products to continue.
          </Alert>
        )}

        <Box mt={3}>
          <Typography variant="subtitle1" gutterBottom fontWeight={500}>
            Please select your primary key
          </Typography>
          <RadioGroup 
            value={primaryKeyId} 
            onChange={handlePrimaryChange}
            name="primary-key-group"
          >
            {products.map(product => 
              renderProductCard(product, true)
            )}
          </RadioGroup>
        </Box>

        <Box mt={3}>
          <Typography variant="subtitle1" gutterBottom fontWeight={500}>
            Please select your backup key
          </Typography>
          <RadioGroup 
            value={backupKeyId} 
            onChange={handleBackupChange}
            name="backup-key-group"
          >
            {products.map(product => 
              renderProductCard(product, false)
            )}
          </RadioGroup>
        </Box>

        <Box mt={4}>
          <Button
            variant="contained"
            size="large"
            fullWidth
            disabled={!isComplete}
            onClick={onNext}
            sx={{ 
              py: 1.5, 
              backgroundColor: '#000',
              '&:hover': {
                backgroundColor: '#333',
              },
              '&.Mui-disabled': {
                backgroundColor: 'rgba(0, 0, 0, 0.12)',
              },
            }}
          >
            Next
          </Button>
        </Box>
      </Box>

      <Box mt={4}>
        <Typography variant="h5" gutterBottom sx={{ opacity: 0.3 }}>
          2 • Address
        </Typography>
      </Box>
    </Box>
  );
};