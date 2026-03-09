import ArrowBackRoundedIcon from '@mui/icons-material/ArrowBackRounded';
import ConstructionRoundedIcon from '@mui/icons-material/ConstructionRounded';
import { Alert, Box, Button, Paper, Stack, Typography } from '@mui/material';
import { Link as RouterLink, useParams } from 'react-router-dom';

function GameDetailsPage() {
  const { id } = useParams();

  return (
    <Paper sx={{ p: 3 }}>
      <Stack spacing={2}>
        <Box>
          <Typography variant="h4" component="h1" sx={{ fontWeight: 700 }}>
            Game Details
          </Typography>
          <Typography color="text.secondary">Game ID: {id}</Typography>
        </Box>

        <Alert icon={<ConstructionRoundedIcon fontSize="inherit" />} severity="info">
          Detailed game view will be implemented at stage 6.
        </Alert>

        <Box>
          <Button
            component={RouterLink}
            to="/"
            variant="outlined"
            startIcon={<ArrowBackRoundedIcon />}
          >
            Back to Games
          </Button>
        </Box>
      </Stack>
    </Paper>
  );
}

export default GameDetailsPage;

