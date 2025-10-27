import React from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Stepper,
  Step,
  StepLabel,
  StepIcon,
  Stack,
  Chip,
  Alert,
  Divider,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Button,
  Link,
} from '@mui/material';
import { styled } from '@mui/material/styles';
import { 
  Check, 
  Package, 
  Truck, 
  Home,
  KeyIcon,
  MapPin,
  User,
  Copy,
  ExternalLink
} from 'lucide-react';
import { Shipment } from '@/types/api';

interface OrderStatusProps {
  shipment: Shipment;
  onBack?: () => void;
}

const CustomStepIcon = styled(StepIcon)(({ theme, active, completed }) => ({
  '& .MuiStepIcon-root': {
    color: theme.palette.grey[300],
    '&.Mui-completed': {
      color: theme.palette.success.main,
    },
    '&.Mui-active': {
      color: theme.palette.primary.main,
    },
  },
}));

const getStepIndex = (status: string): number => {
  switch (status) {
    case 'PENDING':
    case 'PROCESSING':
      return 0;
    case 'SHIPPED':
      return 1;
    case 'DELIVERED':
      return 2;
    default:
      return 0;
  }
};

export const OrderStatus: React.FC<OrderStatusProps> = ({ shipment, onBack }) => {
  const activeStep = getStepIndex(shipment.status);
  const requestDate = new Date(shipment.requestDate);
  
  const steps = [
    { label: 'Processing', icon: <Package /> },
    { label: 'Shipped', icon: <Truck /> },
    { label: 'Delivered', icon: <Home /> },
  ];

  const handleCopyTracking = () => {
    if (shipment.trackingNumber) {
      navigator.clipboard.writeText(shipment.trackingNumber);
    }
  };

  return (
    <Box>
      <Alert 
        severity="success" 
        icon={<Check />}
        sx={{ mb: 3 }}
      >
        <Typography variant="subtitle1" fontWeight={500}>
          Success! Your YubiKey request has been confirmed
        </Typography>
      </Alert>

      <Typography variant="h4" gutterBottom>
        Confirmed: YubiKey order {shipment.orderId}
      </Typography>
      
      <Typography variant="body2" color="text.secondary" gutterBottom>
        Request date: {requestDate.toLocaleDateString('en-US', { 
          year: 'numeric',
          month: 'long',
          day: 'numeric',
          hour: '2-digit',
          minute: '2-digit',
          timeZoneName: 'short'
        })}
      </Typography>
      
      <Typography variant="body2" color="text.secondary" gutterBottom>
        Requestor: {shipment.userEmail}
      </Typography>

      <Card sx={{ mt: 3, mb: 3 }}>
        <CardContent>
          <Stepper activeStep={activeStep} alternativeLabel>
            {steps.map((step, index) => (
              <Step key={step.label} completed={index < activeStep}>
                <StepLabel
                  StepIconComponent={() => (
                    <Box
                      sx={{
                        width: 40,
                        height: 40,
                        borderRadius: '50%',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        backgroundColor: 
                          index < activeStep ? 'success.main' :
                          index === activeStep ? 'primary.main' : 
                          'grey.300',
                        color: 'white',
                      }}
                    >
                      {index < activeStep ? <Check size={20} /> : step.icon}
                    </Box>
                  )}
                >
                  <Typography 
                    variant="body2" 
                    color={index <= activeStep ? 'text.primary' : 'text.secondary'}
                    sx={{ mt: 1 }}
                  >
                    {step.label}
                  </Typography>
                </StepLabel>
              </Step>
            ))}
          </Stepper>
        </CardContent>
      </Card>

      <Typography variant="h5" gutterBottom>
        Shipping
      </Typography>
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Stack spacing={2}>
            <Stack direction="row" justifyContent="space-between">
              <Typography variant="body2" color="text.secondary">
                Provider:
              </Typography>
              <Typography variant="body2">
                {shipment.carrier || '--'}
              </Typography>
            </Stack>
            
            {shipment.trackingNumber && (
              <Stack direction="row" justifyContent="space-between" alignItems="center">
                <Typography variant="body2" color="text.secondary">
                  Tracking:
                </Typography>
                <Stack direction="row" spacing={1} alignItems="center">
                  <Typography variant="body2" sx={{ fontFamily: 'monospace' }}>
                    {shipment.trackingNumber}
                  </Typography>
                  <Button
                    size="small"
                    startIcon={<Copy size={16} />}
                    onClick={handleCopyTracking}
                    sx={{ minWidth: 'auto', p: 0.5 }}
                  >
                    Copy
                  </Button>
                </Stack>
              </Stack>
            )}
            
            {shipment.estimatedDelivery && (
              <Stack direction="row" justifyContent="space-between">
                <Typography variant="body2" color="text.secondary">
                  Estimated Delivery:
                </Typography>
                <Typography variant="body2">
                  {new Date(shipment.estimatedDelivery).toLocaleDateString()}
                </Typography>
              </Stack>
            )}
          </Stack>
        </CardContent>
      </Card>

      <Typography variant="h5" gutterBottom>
        Products
      </Typography>
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <List disablePadding>
            {shipment.products.map((item, index) => (
              <React.Fragment key={index}>
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
                      </Stack>
                    }
                    secondary={`${item.product.formFactor} form factor`}
                  />
                  <Typography variant="body1">
                    Qty: {item.quantity}
                  </Typography>
                </ListItem>
              </React.Fragment>
            ))}
          </List>
        </CardContent>
      </Card>

      <Typography variant="h5" gutterBottom>
        Address
      </Typography>
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Stack spacing={1.5}>
            <Typography variant="body1">
              <strong>First Name:</strong> {shipment.shippingAddress.firstName}
            </Typography>
            <Typography variant="body1">
              <strong>Last Name:</strong> {shipment.shippingAddress.lastName}
            </Typography>
            <Typography variant="body1">
              <strong>Address:</strong> {shipment.shippingAddress.addressLine1}
              {shipment.shippingAddress.addressLine2 && (
                <>, {shipment.shippingAddress.addressLine2}</>
              )}
            </Typography>
            <Typography variant="body1">
              <strong>City:</strong> {shipment.shippingAddress.city}
            </Typography>
            <Typography variant="body1">
              <strong>State/Province:</strong> {shipment.shippingAddress.stateProvince}
            </Typography>
            <Typography variant="body1">
              <strong>Country:</strong> {shipment.shippingAddress.country}
            </Typography>
            <Typography variant="body1">
              <strong>Postal Code:</strong> {shipment.shippingAddress.postalCode}
            </Typography>
            <Typography variant="body1">
              <strong>Phone:</strong> {shipment.shippingAddress.phone}
            </Typography>
          </Stack>
        </CardContent>
      </Card>

      {onBack && (
        <Button
          variant="outlined"
          onClick={onBack}
          sx={{ mt: 2 }}
        >
          Back to Orders
        </Button>
      )}
    </Box>
  );
};