import ArrowBackRoundedIcon from '@mui/icons-material/ArrowBackRounded';
import ConstructionRoundedIcon from '@mui/icons-material/ConstructionRounded';
import { Alert, Box, Button, Paper, Stack, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

function GameFormPage({ mode }) {
  const isEditMode = mode === 'edit';

  return (
    <Paper sx={{ p: 3 }}>
      <Stack spacing={2}>
        <Box>
          <Typography variant="h4" component="h1" sx={{ fontWeight: 700 }}>
            {isEditMode ? 'Edit Game' : 'Create Game'}
          </Typography>
          <Typography color="text.secondary">
            Stage 2 routing scaffold. Form implementation will be added at stage 5.
          </Typography>
        </Box>

        <Alert icon={<ConstructionRoundedIcon fontSize="inherit" />} severity="info">
          The full game form with validation and API integration is not implemented yet.
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

export default GameFormPage;
