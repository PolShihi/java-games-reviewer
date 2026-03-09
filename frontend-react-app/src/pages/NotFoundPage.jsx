import HomeRoundedIcon from '@mui/icons-material/HomeRounded';
import { Box, Button, Container, Paper, Stack, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

function NotFoundPage() {
  return (
    <Container maxWidth="sm" sx={{ py: 8 }}>
      <Paper sx={{ p: 4, textAlign: 'center' }}>
        <Stack spacing={2}>
          <Typography variant="h3" component="h1" sx={{ fontWeight: 700 }}>
            404
          </Typography>
          <Typography variant="h6">Page not found</Typography>
          <Typography color="text.secondary">
            The route does not exist. Use the button below to return to the games list.
          </Typography>
          <Box>
            <Button
              component={RouterLink}
              to="/"
              variant="contained"
              startIcon={<HomeRoundedIcon />}
            >
              Go to Home
            </Button>
          </Box>
        </Stack>
      </Paper>
    </Container>
  );
}

export default NotFoundPage;

