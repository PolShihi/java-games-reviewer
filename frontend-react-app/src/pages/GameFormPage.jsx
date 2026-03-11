import React from 'react';
import ArrowBackRoundedIcon from '@mui/icons-material/ArrowBackRounded';
import BuildRoundedIcon from '@mui/icons-material/BuildRounded';
import SaveRoundedIcon from '@mui/icons-material/SaveRounded';
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  FormControl,
  FormHelperText,
  InputLabel,
  MenuItem,
  OutlinedInput,
  Paper,
  Select,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import PropTypes from 'prop-types';
import { Controller, useForm } from 'react-hook-form';
import { Link as RouterLink, useNavigate, useParams } from 'react-router-dom';
import ReferenceManagerDialog from '../components/reference/ReferenceManagerDialog';
import GameService from '../services/GameService';
import GenreService from '../services/GenreService';
import log from '../services/Logger';
import ProductionCompanyService from '../services/ProductionCompanyService';
import { fetchAllPageContent } from '../utils/fetchAllPageContent';

const DEFAULT_VALUES = {
  title: '',
  releaseYear: '',
  description: '',
  developerId: '',
  publisherId: '',
  genreIds: [],
};

const toNullableNumber = (value) => {
  if (value === '' || value === null || value === undefined) {
    return null;
  }

  const parsed = Number(value);
  return Number.isNaN(parsed) ? null : parsed;
};

const formatGenreNames = (selected, genres) => {
  if (!Array.isArray(selected) || selected.length === 0) {
    return '';
  }

  return selected
    .map((genreId) => genres.find((genre) => genre.id === genreId)?.name)
    .filter(Boolean)
    .join(', ');
};

function GameFormPage({ mode }) {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditMode = mode === 'edit';

  const {
    control,
    handleSubmit,
    register,
    reset,
    formState: { errors, isSubmitting },
  } = useForm({
    defaultValues: DEFAULT_VALUES,
  });

  const [genres, setGenres] = React.useState([]);
  const [companies, setCompanies] = React.useState([]);
  const [loading, setLoading] = React.useState(true);
  const [submitError, setSubmitError] = React.useState(null);
  const [referenceDialogOpen, setReferenceDialogOpen] = React.useState(false);
  const [referenceEntityType, setReferenceEntityType] = React.useState('genres');

  const loadReferenceData = React.useCallback(async () => {
    const [genresResponse, companies] = await Promise.all([
      GenreService.getAll(),
      fetchAllPageContent(
        (params) => ProductionCompanyService.getAll(params),
        {
          sortBy: 'id',
          sortDirection: 'ASC',
        }
      ),
    ]);

    setGenres(Array.isArray(genresResponse.data) ? genresResponse.data : []);
    setCompanies(companies);
  }, []);

  const loadGameForEdit = React.useCallback(async () => {
    if (!isEditMode) {
      return;
    }

    if (!id) {
      throw new Error('Game id is required for edit mode.');
    }

    const response = await GameService.getById(id);
    const game = response.data;

    reset({
      title: game.title || '',
      releaseYear: game.releaseYear ?? '',
      description: game.description || '',
      developerId: game.developer?.id ?? '',
      publisherId: game.publisher?.id ?? '',
      genreIds: Array.isArray(game.genres) ? game.genres.map((genre) => genre.id) : [],
    });
  }, [id, isEditMode, reset]);

  React.useEffect(() => {
    let active = true;

    const loadPageData = async () => {
      try {
        setLoading(true);
        await loadReferenceData();
        await loadGameForEdit();
      } catch (error) {
        log.error('Failed to initialize game form:', error);
        if (active) {
          setSubmitError('Failed to load form data. Please try again.');
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };

    loadPageData();

    return () => {
      active = false;
    };
  }, [loadGameForEdit, loadReferenceData]);

  const onSubmit = async (formValues) => {
    setSubmitError(null);

    const payload = {
      title: formValues.title.trim(),
      releaseYear: Number(formValues.releaseYear),
      description: formValues.description?.trim() || null,
      developerId: toNullableNumber(formValues.developerId),
      publisherId: toNullableNumber(formValues.publisherId),
      genreIds: formValues.genreIds || [],
    };

    try {
      if (isEditMode) {
        if (!id) {
          throw new Error('Game id is required for update.');
        }
        const updateResponse = await GameService.update(id, payload);
        navigate(`/games/${updateResponse.data.id}`);
      } else {
        const createResponse = await GameService.create(payload);
        navigate(`/games/${createResponse.data.id}`);
      }
    } catch (error) {
      log.error('Failed to submit game form:', error);

      const validationErrors = error?.errors;
      if (validationErrors && typeof validationErrors === 'object') {
        const firstValidationMessage = Object.values(validationErrors)[0];
        setSubmitError(firstValidationMessage || 'Validation failed.');
      } else {
        setSubmitError(error.message || 'Failed to save game. Please try again.');
      }
    }
  };

  const openReferenceDialog = (entityType) => {
    setReferenceEntityType(entityType);
    setReferenceDialogOpen(true);
  };

  const closeReferenceDialog = () => {
    setReferenceDialogOpen(false);
  };

  const handleReferenceChanged = async () => {
    await loadReferenceData();
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" mt={10}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Paper sx={{ p: 3 }}>
      <Stack spacing={3}>
        <Box>
          <Typography variant="h4" component="h1" sx={{ fontWeight: 700 }}>
            {isEditMode ? 'Edit Game' : 'Create Game'}
          </Typography>
          <Typography color="text.secondary">
            Fill in the game data and save changes.
          </Typography>
        </Box>

        {submitError && <Alert severity="error">{submitError}</Alert>}

        <Box component="form" onSubmit={handleSubmit(onSubmit)}>
          <Stack spacing={2.5}>
            <TextField
              label="Title"
              fullWidth
              error={Boolean(errors.title)}
              helperText={errors.title?.message}
              {...register('title', {
                required: 'Title is required',
                maxLength: {
                  value: 150,
                  message: 'Title must be at most 150 characters',
                },
              })}
            />

            <TextField
              label="Release year"
              type="number"
              fullWidth
              error={Boolean(errors.releaseYear)}
              helperText={errors.releaseYear?.message}
              slotProps={{ htmlInput: { min: 1950 } }}
              {...register('releaseYear', {
                required: 'Release year is required',
                valueAsNumber: true,
                min: {
                  value: 1950,
                  message: 'Release year must be at least 1950',
                },
              })}
            />

            <TextField
              label="Description"
              multiline
              minRows={4}
              fullWidth
              {...register('description')}
            />

            <Stack direction={{ xs: 'column', md: 'row' }} spacing={1.5} alignItems={{ md: 'flex-end' }}>
              <Controller
                name="developerId"
                control={control}
                render={({ field }) => (
                  <FormControl fullWidth>
                    <InputLabel id="developer-select-label">Developer</InputLabel>
                    <Select
                      {...field}
                      labelId="developer-select-label"
                      label="Developer"
                    >
                      <MenuItem value="">None</MenuItem>
                      {companies.map((company) => (
                        <MenuItem key={`developer-${company.id}`} value={company.id}>
                          {company.name}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                )}
              />
              <Button
                variant="outlined"
                startIcon={<BuildRoundedIcon />}
                onClick={() => openReferenceDialog('production-companies')}
              >
                Manage
              </Button>
            </Stack>

            <Stack direction={{ xs: 'column', md: 'row' }} spacing={1.5} alignItems={{ md: 'flex-end' }}>
              <Controller
                name="publisherId"
                control={control}
                render={({ field }) => (
                  <FormControl fullWidth>
                    <InputLabel id="publisher-select-label">Publisher</InputLabel>
                    <Select
                      {...field}
                      labelId="publisher-select-label"
                      label="Publisher"
                    >
                      <MenuItem value="">None</MenuItem>
                      {companies.map((company) => (
                        <MenuItem key={`publisher-${company.id}`} value={company.id}>
                          {company.name}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                )}
              />
              <Button
                variant="outlined"
                startIcon={<BuildRoundedIcon />}
                onClick={() => openReferenceDialog('production-companies')}
              >
                Manage
              </Button>
            </Stack>

            <Stack direction={{ xs: 'column', md: 'row' }} spacing={1.5} alignItems={{ md: 'flex-end' }}>
              <Controller
                name="genreIds"
                control={control}
                rules={{
                  validate: (value) =>
                    (Array.isArray(value) && value.length > 0) || 'Select at least one genre',
                }}
                render={({ field }) => (
                  <FormControl fullWidth error={Boolean(errors.genreIds)}>
                    <InputLabel id="genre-multi-select-label">Genres</InputLabel>
                    <Select
                      {...field}
                      multiple
                      labelId="genre-multi-select-label"
                      label="Genres"
                      input={<OutlinedInput label="Genres" />}
                      renderValue={(selected) => formatGenreNames(selected, genres)}
                    >
                      {genres.map((genre) => (
                        <MenuItem key={genre.id} value={genre.id}>
                          {genre.name}
                        </MenuItem>
                      ))}
                    </Select>
                    {errors.genreIds && <FormHelperText>{errors.genreIds.message}</FormHelperText>}
                  </FormControl>
                )}
              />
              <Button
                variant="outlined"
                startIcon={<BuildRoundedIcon />}
                onClick={() => openReferenceDialog('genres')}
              >
                Manage
              </Button>
            </Stack>

            <Stack direction="row" spacing={1.5}>
              <Button
                component={RouterLink}
                to={isEditMode && id ? `/games/${id}` : '/'}
                variant="outlined"
                startIcon={<ArrowBackRoundedIcon />}
                disabled={isSubmitting}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="contained"
                startIcon={<SaveRoundedIcon />}
                disabled={isSubmitting}
              >
                {isEditMode ? 'Save changes' : 'Create game'}
              </Button>
            </Stack>
          </Stack>
        </Box>
      </Stack>

      <ReferenceManagerDialog
        open={referenceDialogOpen}
        entityType={referenceEntityType}
        onClose={closeReferenceDialog}
        onChanged={handleReferenceChanged}
      />
    </Paper>
  );
}

export default GameFormPage;

GameFormPage.propTypes = {
  mode: PropTypes.string,
};
