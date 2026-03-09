import AddCircleOutlineRoundedIcon from '@mui/icons-material/AddCircleOutlineRounded';
import SportsEsportsRoundedIcon from '@mui/icons-material/SportsEsportsRounded';
import {
  AppBar,
  Box,
  Button,
  Container,
  Toolbar,
  Typography,
} from '@mui/material';
import { Link as RouterLink, Outlet } from 'react-router-dom';

function AppLayout() {
  return (
    <Box sx={{ minHeight: '100vh', backgroundColor: '#f4f6f8' }}>
      <AppBar position="sticky" elevation={0} sx={{ borderBottom: '1px solid #d9e0e7' }}>
        <Toolbar sx={{ display: 'flex', justifyContent: 'space-between' }}>
          <Button
            component={RouterLink}
            to="/"
            color="inherit"
            sx={{ textTransform: 'none', gap: 1, px: 0 }}
          >
            <SportsEsportsRoundedIcon />
            <Typography variant="h6" component="span" sx={{ fontWeight: 700 }}>
              Games Reviewer
            </Typography>
          </Button>

          <Button
            component={RouterLink}
            to="/games/new"
            variant="contained"
            color="secondary"
            startIcon={<AddCircleOutlineRoundedIcon />}
            sx={{ textTransform: 'none', fontWeight: 600 }}
          >
            Add Game
          </Button>
        </Toolbar>
      </AppBar>

      <Container maxWidth="lg" sx={{ py: 3 }}>
        <Outlet />
      </Container>
    </Box>
  );
}

export default AppLayout;

