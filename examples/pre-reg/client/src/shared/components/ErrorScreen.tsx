import React from 'react';
import { Box, Typography, Button, Paper, Stack } from '@mui/material';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';

export interface ErrorScreenProps {
  title?: string;
  message?: string;
  actionLabel?: string;
  onAction?: () => void;
  showTechnicalDetails?: boolean;
  technicalDetails?: string;
}

export const ErrorScreen: React.FC<ErrorScreenProps> = ({
  title = 'Something went wrong',
  message = 'We encountered an unexpected error. Please try again.',
  actionLabel = 'Try Again',
  onAction,
  showTechnicalDetails = false,
  technicalDetails,
}) => {
  return (
    <Box
      sx={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        bgcolor: 'background.default',
        p: 3,
      }}
    >
      <Paper
        elevation={0}
        sx={{
          maxWidth: 480,
          width: '100%',
          p: 6,
          textAlign: 'center',
          border: '1px solid',
          borderColor: 'divider',
        }}
      >
        <Stack spacing={3} alignItems="center">
          <ErrorOutlineIcon
            sx={{
              fontSize: 64,
              color: 'error.main',
              opacity: 0.9,
            }}
          />

          <Stack spacing={1}>
            <Typography
              variant="h5"
              component="h1"
              fontWeight={600}
              color="text.primary"
            >
              {title}
            </Typography>

            <Typography
              variant="body1"
              color="text.secondary"
              sx={{ maxWidth: 400 }}
            >
              {message}
            </Typography>
          </Stack>

          {onAction && (
            <Button
              variant="contained"
              size="large"
              onClick={onAction}
              sx={{
                mt: 2,
                px: 4,
                textTransform: 'none',
                fontSize: '1rem',
              }}
            >
              {actionLabel}
            </Button>
          )}

          {showTechnicalDetails && technicalDetails && (
            <Box
              sx={{
                mt: 3,
                p: 2,
                bgcolor: 'grey.50',
                borderRadius: 1,
                width: '100%',
                textAlign: 'left',
              }}
            >
              <Typography
                variant="caption"
                color="text.secondary"
                sx={{ fontFamily: 'monospace', fontSize: '0.75rem' }}
              >
                {technicalDetails}
              </Typography>
            </Box>
          )}
        </Stack>
      </Paper>
    </Box>
  );
};
