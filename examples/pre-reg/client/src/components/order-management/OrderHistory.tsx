import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Chip,
  Stack,
  Button,
  Grid,
  Divider,
  Skeleton,
  Alert,
} from '@mui/material';
import { Package, Truck, CheckCircle, XCircle, Clock, ChevronRight } from 'lucide-react';
import { Shipment } from '@/types/api';
import { useQuery } from '@tanstack/react-query';

interface OrderHistoryProps {
  getShipments: () => Promise<{ shipments: Shipment[]; total: number }>;
  onOrderSelect?: (shipment: Shipment) => void;
  loading?: boolean;
  error?: string;
}

const getStatusIcon = (status: Shipment['status']) => {
  switch (status) {
    case 'PENDING':
      return <Clock size={20} />;
    case 'PROCESSING':
      return <Package size={20} />;
    case 'SHIPPED':
      return <Truck size={20} />;
    case 'DELIVERED':
      return <CheckCircle size={20} />;
    case 'CANCELLED':
      return <XCircle size={20} />;
    default:
      return <Package size={20} />;
  }
};

const getStatusColor = (status: Shipment['status']) => {
  switch (status) {
    case 'PENDING':
      return 'warning';
    case 'PROCESSING':
      return 'info';
    case 'SHIPPED':
      return 'primary';
    case 'DELIVERED':
      return 'success';
    case 'CANCELLED':
      return 'error';
    default:
      return 'default';
  }
};

const getStatusLabel = (status: Shipment['status']) => {
  switch (status) {
    case 'PENDING':
      return 'Awaiting Validation';
    case 'PROCESSING':
      return 'Accepted for Fulfillment';
    case 'SHIPPED':
      return 'Shipped';
    case 'DELIVERED':
      return 'Delivered';
    case 'CANCELLED':
      return 'Cancelled';
    default:
      return status;
  }
};

export const OrderHistory: React.FC<OrderHistoryProps> = ({
  getShipments,
  onOrderSelect,
  loading: externalLoading,
  error: externalError,
}) => {
  const { data, isLoading, error } = useQuery({
    queryKey: ['shipments'],
    queryFn: getShipments,
    enabled: !externalLoading && !externalError,
  });

  const isLoadingState = externalLoading || isLoading;
  const errorState = externalError || error;
  const shipments = data?.shipments || [];

  if (isLoadingState) {
    return (
      <Box>
        <Typography variant="h5" gutterBottom>
          Your Orders
        </Typography>
        <Stack spacing={2}>
          {[1, 2, 3].map((i) => (
            <Card key={i}>
              <CardContent>
                <Skeleton variant="text" width="30%" height={24} />
                <Skeleton variant="text" width="50%" height={20} />
                <Skeleton variant="rectangular" height={60} sx={{ mt: 2 }} />
              </CardContent>
            </Card>
          ))}
        </Stack>
      </Box>
    );
  }

  if (errorState) {
    return (
      <Box>
        <Typography variant="h5" gutterBottom>
          Your Orders
        </Typography>
        <Alert severity="error">
          {typeof errorState === 'string' ? errorState : 'Failed to load orders. Please try again later.'}
        </Alert>
      </Box>
    );
  }

  if (shipments.length === 0) {
    return (
      <Box>
        <Typography variant="h5" gutterBottom>
          Your Orders
        </Typography>
        <Card>
          <CardContent sx={{ textAlign: 'center', py: 6 }}>
            <Package size={48} style={{ opacity: 0.3 }} />
            <Typography variant="h6" sx={{ mt: 2, mb: 1 }}>
              No orders yet
            </Typography>
            <Typography variant="body2" color="text.secondary">
              When you place an order, it will appear here
            </Typography>
          </CardContent>
        </Card>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h5" gutterBottom>
        Your Orders
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        {shipments.length} {shipments.length === 1 ? 'order' : 'orders'} total
      </Typography>

      <Stack spacing={2}>
        {shipments.map((shipment) => {
          const requestDate = new Date(shipment.requestDate);
          const primaryProduct = shipment.products.find(p => p.isPrimary);
          const totalProducts = shipment.products.reduce((sum, p) => sum + p.quantity, 0);
          
          return (
            <Card 
              key={shipment.id}
              sx={{ 
                cursor: onOrderSelect ? 'pointer' : 'default',
                transition: 'all 0.2s',
                '&:hover': onOrderSelect ? {
                  boxShadow: 3,
                  transform: 'translateY(-2px)',
                } : {},
              }}
              onClick={() => onOrderSelect?.(shipment)}
            >
              <CardContent>
                <Grid container spacing={2} alignItems="center">
                  <Grid item xs={12} sm={6}>
                    <Stack spacing={1}>
                      <Stack direction="row" spacing={2} alignItems="center">
                        <Typography variant="h6">
                          Order {shipment.orderId}
                        </Typography>
                        <Chip
                          size="small"
                          label={getStatusLabel(shipment.status)}
                          color={getStatusColor(shipment.status) as any}
                          icon={getStatusIcon(shipment.status)}
                        />
                      </Stack>
                      <Typography variant="body2" color="text.secondary">
                        Placed on {requestDate.toLocaleDateString('en-US', { 
                          year: 'numeric',
                          month: 'long',
                          day: 'numeric'
                        })}
                      </Typography>
                    </Stack>
                  </Grid>
                  
                  <Grid item xs={12} sm={4}>
                    <Stack spacing={0.5}>
                      <Typography variant="body2" color="text.secondary">
                        Products
                      </Typography>
                      <Typography variant="body1">
                        {primaryProduct ? primaryProduct.product.name : 'Multiple items'}
                      </Typography>
                      {totalProducts > 1 && (
                        <Typography variant="caption" color="text.secondary">
                          +{totalProducts - 1} more {totalProducts - 1 === 1 ? 'item' : 'items'}
                        </Typography>
                      )}
                    </Stack>
                  </Grid>
                  
                  <Grid item xs={12} sm={2} sx={{ textAlign: 'right' }}>
                    {onOrderSelect && (
                      <Button
                        endIcon={<ChevronRight />}
                        onClick={(e) => {
                          e.stopPropagation();
                          onOrderSelect(shipment);
                        }}
                      >
                        View Details
                      </Button>
                    )}
                  </Grid>
                </Grid>

                {shipment.status === 'SHIPPED' && shipment.trackingNumber && (
                  <>
                    <Divider sx={{ my: 2 }} />
                    <Stack direction="row" spacing={2} alignItems="center">
                      <Truck size={16} />
                      <Typography variant="body2">
                        Tracking: {shipment.trackingNumber}
                      </Typography>
                      {shipment.carrier && (
                        <Chip size="small" label={shipment.carrier} variant="outlined" />
                      )}
                    </Stack>
                  </>
                )}

                {shipment.status === 'DELIVERED' && shipment.actualDelivery && (
                  <>
                    <Divider sx={{ my: 2 }} />
                    <Stack direction="row" spacing={2} alignItems="center">
                      <CheckCircle size={16} color="green" />
                      <Typography variant="body2" color="success.main">
                        Delivered on {new Date(shipment.actualDelivery).toLocaleDateString()}
                      </Typography>
                    </Stack>
                  </>
                )}

                {shipment.status === 'CANCELLED' && shipment.metadata?.reasonCode && (
                  <>
                    <Divider sx={{ my: 2 }} />
                    <Alert severity="error" sx={{ py: 0.5 }}>
                      {shipment.metadata.reasonCode === 'ADDRESS_VALIDATION_FAILED' 
                        ? 'Address validation failed'
                        : shipment.metadata.message || 'Order cancelled'
                      }
                    </Alert>
                  </>
                )}
              </CardContent>
            </Card>
          );
        })}
      </Stack>
    </Box>
  );
};