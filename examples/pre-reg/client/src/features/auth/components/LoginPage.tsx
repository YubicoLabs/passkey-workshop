import React from 'react';
import { 
  Box, 
  Paper, 
  Typography, 
  Button, 
  Link, 
  styled 
} from '@mui/material';
import SecurityIcon from '@mui/icons-material/Security';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';

const PageContainer = styled(Box)(({ theme }) => ({
  minHeight: '100vh',
  display: 'flex',
  flexDirection: 'column',
  backgroundColor: theme.palette.grey[50], 
}));

const ContentWrapper = styled(Box)(({ theme }) => ({
  flex: 1,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  padding: theme.spacing(2), 
}));

const LoginCard = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(5),
  width: '100%',
  maxWidth: 450,
  textAlign: 'center',
  display: 'flex',
  flexDirection: 'column',
  gap: theme.spacing(3),
  borderRadius: (theme.shape.borderRadius as number) * 2,
}));

const SSOButton = styled(Button)(({ theme }) => ({
  paddingBlock: theme.spacing(1.5),
  fontSize: '1.1rem',
  fontWeight: 600,
  textTransform: 'none',
}));

interface LoginPageProps {
  onLogin: () => void;
}

const LoginPage: React.FC<LoginPageProps> = ({ onLogin }) => (
  <PageContainer>
    <ContentWrapper>
      <LoginCard elevation={3}>
        
        {/* Header */}
        <Box mb={1}>
          <SecurityIcon sx={{ fontSize: 48, color: 'primary.main' }} />
        </Box>

        {/* Text */}
        <Box>
          <Typography variant="h4" component="h1" fontWeight="700" gutterBottom>
            Sign in
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Welcome to the YubiKey LaunchPad. <br />
            Please authenticate to manage your orders.
          </Typography>
        </Box>

        {/* Action */}
        <SSOButton
          variant="contained"
          size="large"
          fullWidth
          onClick={onLogin}
          endIcon={<ArrowForwardIcon />}
        >
          Log in with SSO
        </SSOButton>

        {/* Footer Note */}
        <Typography variant="caption" color="text.disabled" mt={2}>
          Protected by FIDO2 & WebAuthn
        </Typography>

      </LoginCard>
    </ContentWrapper>

    {/* Global Footer */}
    <Box component="footer" py={3} textAlign="center">
      <Typography variant="body2" color="text.secondary">
        &copy; {new Date().getFullYear()} Yubico. All rights reserved. |{' '}
        <Link href="#" color="inherit" underline="hover">
          Privacy Policy
        </Link>
      </Typography>
    </Box>
  </PageContainer>
);

export default LoginPage;
