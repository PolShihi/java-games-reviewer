import ArrowBackRoundedIcon from '@mui/icons-material/ArrowBackRounded';
import AddCircleOutlineRoundedIcon from '@mui/icons-material/AddCircleOutlineRounded';
import DeleteOutlineRoundedIcon from '@mui/icons-material/DeleteOutlineRounded';
import EditRoundedIcon from '@mui/icons-material/EditRounded';
import RefreshRoundedIcon from '@mui/icons-material/RefreshRounded';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Paper,
  Rating,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';
import { useCallback, useEffect, useState } from 'react';
import { Link as RouterLink, useNavigate, useParams } from 'react-router-dom';
import AddReviewDialog from '../components/reviews/AddReviewDialog';
import AddRequirementDialog from '../components/system-requirements/AddRequirementDialog';
import GameService from '../services/GameService';
import log from '../services/Logger';
import ReviewService from '../services/ReviewService';
import SystemRequirementService from '../services/SystemRequirementService';

function GameDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [game, setGame] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [requirementDialogOpen, setRequirementDialogOpen] = useState(false);
  const [selectedRequirement, setSelectedRequirement] = useState(null);
  const [reviewDialogOpen, setReviewDialogOpen] = useState(false);
  const [selectedReview, setSelectedReview] = useState(null);

  const loadGame = useCallback(async () => {
    if (!id) {
      setError('Game ID is missing.');
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      const response = await GameService.getById(id);
      setGame(response.data);
      setError(null);
    } catch (loadError) {
      log.error('Failed to load game details:', loadError);
      setError(loadError.message || 'Failed to load game details.');
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    loadGame();
  }, [loadGame]);

  const handleDelete = async () => {
    if (!id) {
      return;
    }

    const confirmed = globalThis.confirm('Delete this game? This action cannot be undone.');
    if (!confirmed) {
      return;
    }

    try {
      setDeleting(true);
      await GameService.delete(id);
      navigate('/');
    } catch (deleteError) {
      log.error('Failed to delete game:', deleteError);
      setError(deleteError.message || 'Failed to delete game.');
    } finally {
      setDeleting(false);
    }
  };

  const handleOpenCreateRequirementDialog = () => {
    setSelectedRequirement(null);
    setRequirementDialogOpen(true);
  };

  const handleOpenEditRequirementDialog = (requirement) => {
    setSelectedRequirement(requirement);
    setRequirementDialogOpen(true);
  };

  const handleCloseRequirementDialog = () => {
    setRequirementDialogOpen(false);
    setSelectedRequirement(null);
  };

  const handleDeleteRequirement = async (requirementId) => {
    const confirmed = globalThis.confirm('Delete this system requirement?');
    if (!confirmed) {
      return;
    }

    try {
      await SystemRequirementService.delete(requirementId);
      await loadGame();
    } catch (deleteError) {
      log.error('Failed to delete system requirement:', deleteError);
      setError(deleteError.message || 'Failed to delete system requirement.');
    }
  };

  const handleOpenCreateReviewDialog = () => {
    setSelectedReview(null);
    setReviewDialogOpen(true);
  };

  const handleOpenEditReviewDialog = (review) => {
    setSelectedReview(review);
    setReviewDialogOpen(true);
  };

  const handleCloseReviewDialog = () => {
    setReviewDialogOpen(false);
    setSelectedReview(null);
  };

  const handleDeleteReview = async (reviewId) => {
    const confirmed = globalThis.confirm('Delete this review?');
    if (!confirmed) {
      return;
    }

    try {
      await ReviewService.delete(reviewId);
      await loadGame();
    } catch (deleteError) {
      log.error('Failed to delete review:', deleteError);
      setError(deleteError.message || 'Failed to delete review.');
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" mt={10}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Stack spacing={2}>
        <Typography variant="h4" component="h1" sx={{ fontWeight: 700 }}>
          Game Details
        </Typography>
        <Alert severity="error">{error}</Alert>
        <Stack direction="row" spacing={1}>
          <Button variant="contained" startIcon={<RefreshRoundedIcon />} onClick={loadGame}>
            Retry
          </Button>
          <Button component={RouterLink} to="/" variant="outlined" startIcon={<ArrowBackRoundedIcon />}>
            Back to Games
          </Button>
        </Stack>
      </Stack>
    );
  }

  if (!game) {
    return (
      <Stack spacing={2}>
        <Alert severity="info">Game was not found.</Alert>
        <Button component={RouterLink} to="/" variant="outlined" startIcon={<ArrowBackRoundedIcon />}>
          Back to Games
        </Button>
      </Stack>
    );
  }

  return (
    <Stack spacing={3}>
      <Stack direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" alignItems={{ md: 'flex-start' }} spacing={2}>
        <Box>
          <Typography variant="h4" component="h1" sx={{ fontWeight: 700 }}>
            {game.title}
          </Typography>
          <Typography color="text.secondary">
            Release year: {game.releaseYear || '-'}
          </Typography>
          <Box mt={1} display="flex" alignItems="center" gap={1}>
            {game.averageRating !== null && game.averageRating !== undefined ? (
              <>
                <Rating value={game.averageRating / 20} readOnly precision={0.5} size="small" />
                <Typography variant="body2" color="text.secondary">
                  {Math.round(game.averageRating)}/100
                </Typography>
              </>
            ) : (
              <Typography variant="body2" color="text.secondary">Rating: N/A</Typography>
            )}
          </Box>
        </Box>

        <Stack direction="row" spacing={1}>
          <Button component={RouterLink} to="/" variant="outlined" startIcon={<ArrowBackRoundedIcon />}>
            Back
          </Button>
          <Button
            component={RouterLink}
            to={`/games/${game.id}/edit`}
            variant="outlined"
            startIcon={<EditRoundedIcon />}
          >
            Edit
          </Button>
          <Button
            variant="contained"
            color="error"
            startIcon={<DeleteOutlineRoundedIcon />}
            onClick={handleDelete}
            disabled={deleting}
          >
            Delete
          </Button>
        </Stack>
      </Stack>

      <Paper sx={{ p: 2.5 }}>
        <Stack spacing={1.5}>
          <Typography variant="h6">About</Typography>
          <Typography color="text.secondary">
            {game.description || 'No description provided.'}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Developer: {game.developer?.name || '-'}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Publisher: {game.publisher?.name || '-'}
          </Typography>
          <Box display="flex" gap={1} flexWrap="wrap">
            {(game.genres || []).map((genre) => (
              <Chip key={genre.id} label={genre.name} variant="outlined" />
            ))}
          </Box>
        </Stack>
      </Paper>

      <Paper sx={{ p: 2.5 }}>
        <Stack spacing={1.5}>
          <Stack direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" alignItems={{ md: 'center' }} spacing={1.5}>
            <Typography variant="h6">System Requirements</Typography>
            <Button
              variant="outlined"
              startIcon={<AddCircleOutlineRoundedIcon />}
              onClick={handleOpenCreateRequirementDialog}
            >
              Add requirement
            </Button>
          </Stack>
          {(game.systemRequirements || []).length === 0 ? (
            <Alert severity="info">No system requirements added yet.</Alert>
          ) : (
            <TableContainer>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Type</TableCell>
                    <TableCell>Storage (GB)</TableCell>
                    <TableCell>RAM (GB)</TableCell>
                    <TableCell>CPU (GHz)</TableCell>
                    <TableCell>GPU (TFLOPS)</TableCell>
                    <TableCell>VRAM (GB)</TableCell>
                    <TableCell align="right">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {game.systemRequirements.map((item) => (
                    <TableRow key={item.id}>
                      <TableCell>{item.type?.name || '-'}</TableCell>
                      <TableCell>{item.storageGb ?? '-'}</TableCell>
                      <TableCell>{item.ramGb ?? '-'}</TableCell>
                      <TableCell>{item.cpuGhz ?? '-'}</TableCell>
                      <TableCell>{item.gpuTflops ?? '-'}</TableCell>
                      <TableCell>{item.vramGb ?? '-'}</TableCell>
                      <TableCell align="right">
                        <Stack direction="row" spacing={1} justifyContent="flex-end">
                          <Button
                            size="small"
                            variant="outlined"
                            startIcon={<EditRoundedIcon />}
                            onClick={() => handleOpenEditRequirementDialog(item)}
                          >
                            Edit
                          </Button>
                          <Button
                            size="small"
                            variant="outlined"
                            color="error"
                            startIcon={<DeleteOutlineRoundedIcon />}
                            onClick={() => handleDeleteRequirement(item.id)}
                          >
                            Delete
                          </Button>
                        </Stack>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </Stack>
      </Paper>

      <Paper sx={{ p: 2.5 }}>
        <Stack spacing={1.5}>
          <Stack direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" alignItems={{ md: 'center' }} spacing={1.5}>
            <Typography variant="h6">Reviews</Typography>
            <Button
              variant="outlined"
              startIcon={<AddCircleOutlineRoundedIcon />}
              onClick={handleOpenCreateReviewDialog}
            >
              Add review
            </Button>
          </Stack>
          {(game.reviews || []).length === 0 ? (
            <Alert severity="info">No reviews added yet.</Alert>
          ) : (
            <Stack spacing={1.5}>
              {game.reviews.map((review) => (
                <Card key={review.id} variant="outlined">
                  <CardContent>
                    <Stack spacing={1}>
                      <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
                        {review.mediaOutlet?.name || 'Unknown outlet'}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Score: {review.score ?? '-'}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {review.summary || 'No summary.'}
                      </Typography>
                      <Stack direction="row" spacing={1}>
                        <Button
                          size="small"
                          variant="outlined"
                          startIcon={<EditRoundedIcon />}
                          onClick={() => handleOpenEditReviewDialog(review)}
                        >
                          Edit
                        </Button>
                        <Button
                          size="small"
                          variant="outlined"
                          color="error"
                          startIcon={<DeleteOutlineRoundedIcon />}
                          onClick={() => handleDeleteReview(review.id)}
                        >
                          Delete
                        </Button>
                      </Stack>
                    </Stack>
                  </CardContent>
                </Card>
              ))}
            </Stack>
          )}
        </Stack>
      </Paper>

      <AddRequirementDialog
        open={requirementDialogOpen}
        gameId={game.id}
        initialRequirement={selectedRequirement}
        onClose={handleCloseRequirementDialog}
        onCreated={loadGame}
      />
      <AddReviewDialog
        open={reviewDialogOpen}
        gameId={game.id}
        initialReview={selectedReview}
        onClose={handleCloseReviewDialog}
        onCreated={loadGame}
      />
    </Stack>
  );
}

export default GameDetailsPage;
