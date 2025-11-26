import React from 'react';
import {
  Box,
  Typography,
  Button,
  Card,
  CardContent,
  Stack,
  Divider,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Chip,
} from '@mui/material';
import { Key as KeyIcon, MapPin, User, Phone } from 'lucide-react';
import { SelectedProduct, Address } from '@/types/api';
import DOMPurify from 'dompurify';

interface OrderReviewProps {
  selectedProducts: SelectedProduct[];
  shippingAddress: Address;
  onConfirm: () => void;
  onBack: () => void;
  isSubmitting?: boolean;
}

export const OrderReview: React.FC<OrderReviewProps> = ({
  selectedProducts,
  shippingAddress,
  onConfirm,
  onBack,
  isSubmitting = false,
}) => {
  const totalPrice = selectedProducts.reduce(
    (sum, item) => sum + item.product.price * item.quantity,
    0
  );

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Review and confirm your selections
      </Typography>

      <Box mt={4}>
        <Typography variant="h5" gutterBottom>
          Products
        </Typography>
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <List disablePadding>
              {selectedProducts.map((item, index) => {
                // Sanitize description
                const cleanDescription = DOMPurify.sanitize(item.product.description);
                return (
                  <React.Fragment key={`${item.product.id}-${index}-${item.isPrimary ? 'primary' : 'backup'}`}>
                    {index > 0 && <Divider sx={{ my: 2 }} />}
                    <ListItem disablePadding>
                      <ListItemIcon>
                        <KeyIcon />
                      </ListItemIcon>
                      <ListItemText
                        primary={
                          <Stack direction="row" spacing={1} alignItems="center">
                            <Typography variant="body1">
                              {item.isPrimary ? 'Primary key:' : 'Backup key:'}{' '}
                              {item.product.name}
                            </Typography>
                            {item.product.formFactor && (
                              <Chip 
                                size="small" 
                                label={item.product.formFactor}
                                variant="outlined"
                              />
                            )}
                          </Stack>
                        }
                        secondary={
                          <span dangerouslySetInnerHTML={{ __html: cleanDescription }} />
                        }
                      />
                      <Typography variant="body1" sx={{ minWidth: 80, textAlign: 'right' }}>
                        Qty: {item.quantity}
                      </Typography>
                    </ListItem>
                  </React.Fragment>
                );
              })}
            </List>
            <Divider sx={{ my: 2 }} />
            <Box display="flex" justifyContent="space-between" alignItems="center">
              <Typography variant="h6">Total</Typography>
              <Typography variant="h6" color="primary">
                ${totalPrice} USD
              </Typography>
            </Box>
          </CardContent>
        </Card>

        <Typography variant="h5" gutterBottom>
          Address
        </Typography>
        <Card>
          <CardContent>
            <Stack spacing={1.5}>
              <Stack direction="row" spacing={2}>
                <User size={20} />
                <Typography>
                  <strong>First Name:</strong> {shippingAddress.firstName}
                </Typography>
              </Stack>
              
              <Stack direction="row" spacing={2}>
                <User size={20} />
                <Typography>
                  <strong>Last Name:</strong> {shippingAddress.lastName}
                </Typography>
              </Stack>

              <Stack direction="row" spacing={2}>
                <MapPin size={20} />
                <Box>
                  <Typography>
                    <strong>Address:</strong> {shippingAddress.addressLine1}
                  </Typography>
                  {shippingAddress.addressLine2 && (
                    <Typography>{shippingAddress.addressLine2}</Typography>
                  )}
                </Box>
              </Stack>

              <Stack direction="row" spacing={2}>
                <Box width={20} />
                <Typography>
                  <strong>City:</strong> {shippingAddress.city}
                </Typography>
              </Stack>

              <Stack direction="row" spacing={2}>
                <Box width={20} />
                <Typography>
                  <strong>State/Province:</strong> {shippingAddress.stateProvince}
                </Typography>
              </Stack>

              <Stack direction="row" spacing={2}>
                <Box width={20} />
                <Typography>
                  <strong>Country:</strong> {shippingAddress.country}
                </Typography>
              </Stack>

              <Stack direction="row" spacing={2}>
                <Box width={20} />
                <Typography>
                  <strong>Postal Code:</strong> {shippingAddress.postalCode}
                </Typography>
              </Stack>

              <Stack direction="row" spacing={2}>
                <Phone size={20} />
                <Typography>
                  <strong>Phone:</strong> {shippingAddress.phone}
                </Typography>
              </Stack>
            </Stack>
          </CardContent>
        </Card>

        <Stack direction="row" spacing={2} mt={4}>
          <Button
            variant="text"
            size="large"
            onClick={onBack}
            disabled={isSubmitting}
            sx={{ minWidth: 120 }}
          >
            Go back
          </Button>
          
          <Button
            variant="contained"
            size="large"
            fullWidth
            onClick={onConfirm}
            disabled={isSubmitting}
            sx={{ 
              py: 1.5,
              backgroundColor: '#000',
              '&:hover': {
                backgroundColor: '#333',
              },
            }}
          >
            {isSubmitting ? 'Processing...' : 'Confirm and submit'}
          </Button>
        </Stack>
      </Box>
    </Box>
  );
};