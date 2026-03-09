import React, { useCallback, useEffect, useMemo, useState } from 'react';
import VisibilityRoundedIcon from '@mui/icons-material/VisibilityRounded';
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Chip,
  FormControl,
  IconButton,
  InputLabel,
  MenuItem,
  OutlinedInput,
  Paper,
  Rating,
  Select,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TableSortLabel,
  TextField,
  Typography,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import GameService from '../services/GameService';
import GenreService from '../services/GenreService';
import log from '../services/Logger';
import { normalizePageResponse } from '../utils/pageResponse';

const EMPTY_FILTERS = Object.freeze({
  title: '',
  yearFrom: '',
  yearTo: '',
  genreIds: [],
});

const GamesPage = () => {
  const navigate = useNavigate();

  const [games, setGames] = useState([]);
  const [genres, setGenres] = useState([]);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [sortBy, setSortBy] = useState('id');
  const [sortDirection, setSortDirection] = useState('ASC');

  const [filters, setFilters] = useState(EMPTY_FILTERS);
  const [appliedFilters, setAppliedFilters] = useState(EMPTY_FILTERS);

  const columns = useMemo(
    () => [
      { key: 'id', label: 'ID', sortable: true },
      { key: 'title', label: 'Title', sortable: true },
      { key: 'releaseYear', label: 'Release Year', sortable: true },
      { key: 'genres', label: 'Genres', sortable: false },
      { key: 'developerName', label: 'Developer', sortable: false },
      { key: 'publisherName', label: 'Publisher', sortable: false },
      { key: 'averageRating', label: 'Rating', sortable: true, sortField: 'averageRating' },
      { key: 'actions', label: 'Actions', sortable: false },
    ],
    []
  );

  const hasActiveFilters = useMemo(
    () =>
      Boolean(
        appliedFilters.title ||
          appliedFilters.yearFrom ||
          appliedFilters.yearTo ||
          appliedFilters.genreIds.length
      ),
    [appliedFilters]
  );

  const fetchGenres = useCallback(async () => {
    try {
      const response = await GenreService.getAll();
      setGenres(Array.isArray(response.data) ? response.data : []);
    } catch (err) {
      log.warn('Failed to load genres for filters:', err);
    }
  }, []);

  const fetchGames = useCallback(async () => {
    try {
      setLoading(true);

      const pagingAndSorting = {
        page,
        size: pageSize,
        sortBy,
        sortDirection,
      };

      let response;
      if (hasActiveFilters) {
        const filterParams = {
          ...pagingAndSorting,
          title: appliedFilters.title || undefined,
          yearFrom: appliedFilters.yearFrom ? Number(appliedFilters.yearFrom) : undefined,
          yearTo: appliedFilters.yearTo ? Number(appliedFilters.yearTo) : undefined,
          genreIds: appliedFilters.genreIds.length ? appliedFilters.genreIds.join(',') : undefined,
        };

        response = await GameService.filter(filterParams);
      } else {
        response = await GameService.getAll(pagingAndSorting);
      }

      const pageData = normalizePageResponse(response.data);

      setGames(pageData.content);
      setTotalElements(pageData.totalElements);
      setError(null);
    } catch (err) {
      log.error('Error fetching games:', err);
      setError('Failed to load game list. Check backend connection and try again.');
    } finally {
      setLoading(false);
    }
  }, [appliedFilters, hasActiveFilters, page, pageSize, sortBy, sortDirection]);

  useEffect(() => {
    fetchGenres();
  }, [fetchGenres]);

  useEffect(() => {
    fetchGames();
  }, [fetchGames]);

  const handleSort = (field) => {
    if (!field) {
      return;
    }

    if (sortBy === field) {
      setSortDirection((prev) => (prev === 'ASC' ? 'DESC' : 'ASC'));
    } else {
      setSortBy(field);
      setSortDirection('ASC');
    }

    setPage(0);
  };

  const handleChangePage = (_event, newPage) => {
    setPage(newPage);
  };

  const handleChangePageSize = (event) => {
    const newPageSize = Number(event.target.value);
    setPageSize(newPageSize);
    setPage(0);
  };

  const handleTextFilterChange = (field) => (event) => {
    setFilters((prev) => ({ ...prev, [field]: event.target.value }));
  };

  const handleGenreChange = (event) => {
    const value = event.target.value;
    const genreIds = (Array.isArray(value) ? value : value.split(',')).map(Number);
    setFilters((prev) => ({ ...prev, genreIds }));
  };

  const applyFilters = () => {
    setAppliedFilters({
      title: filters.title.trim(),
      yearFrom: filters.yearFrom,
      yearTo: filters.yearTo,
      genreIds: filters.genreIds,
    });
    setPage(0);
  };

  const resetFilters = () => {
    setFilters(EMPTY_FILTERS);
    setAppliedFilters(EMPTY_FILTERS);
    setPage(0);
  };

  if (loading && games.length === 0) {
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
          Games
        </Typography>
        <Alert severity="error">{error}</Alert>
        <Box>
          <Button variant="contained" onClick={fetchGames}>
            Retry
          </Button>
        </Box>
      </Stack>
    );
  }

  return (
    <Stack spacing={2}>
      <Typography variant="h4" component="h1" sx={{ fontWeight: 700 }}>
        Games
      </Typography>

      <Paper sx={{ p: 2 }}>
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} alignItems={{ md: 'flex-end' }}>
          <TextField
            label="Search title"
            value={filters.title}
            onChange={handleTextFilterChange('title')}
            fullWidth
          />

          <TextField
            label="Year from"
            type="number"
            value={filters.yearFrom}
            onChange={handleTextFilterChange('yearFrom')}
            htmlInput = {{ min: 1950 }}
            // inputProps={{ min: 1950 }}
            sx={{ minWidth: 140 }}
          />

          <TextField
            label="Year to"
            type="number"
            value={filters.yearTo}
            onChange={handleTextFilterChange('yearTo')}
            // inputProps={{ min: 1950 }}
            htmlInput = {{ min: 1950 }}
            sx={{ minWidth: 140 }}
          />

          <FormControl sx={{ minWidth: 240 }}>
            <InputLabel id="genres-filter-label">Genres</InputLabel>
            <Select
              labelId="genres-filter-label"
              id="genres-filter"
              multiple
              value={filters.genreIds}
              onChange={handleGenreChange}
              input={<OutlinedInput label="Genres" />}
              renderValue={(selected) =>
                selected
                  .map((id) => genres.find((genre) => genre.id === id)?.name)
                  .filter(Boolean)
                  .join(', ')
              }
            >
              {genres.map((genre) => (
                <MenuItem key={genre.id} value={genre.id}>
                  {genre.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <Button variant="contained" onClick={applyFilters}>
            Apply
          </Button>

          <Button variant="outlined" onClick={resetFilters}>
            Reset
          </Button>
        </Stack>
      </Paper>

      {games.length === 0 ? (
        <Alert severity="info">No games found.</Alert>
      ) : (
        <TableContainer component={Paper} elevation={3}>
          <Table sx={{ minWidth: 650 }} aria-label="games table">
            <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
              <TableRow>
                {columns.map((column) => (
                  <TableCell key={column.key}>
                    {column.sortable ? (
                      <TableSortLabel
                        active={sortBy === (column.sortField || column.key)}
                        direction={sortBy === (column.sortField || column.key) ? sortDirection.toLowerCase() : 'asc'}
                        onClick={() => handleSort(column.sortField || column.key)}
                      >
                        <strong>{column.label}</strong>
                      </TableSortLabel>
                    ) : (
                      <strong>{column.label}</strong>
                    )}
                  </TableCell>
                ))}
              </TableRow>
            </TableHead>
            <TableBody>
              {games.map((game) => (
                <TableRow
                  key={game.id}
                  hover
                  onClick={() => navigate(`/games/${game.id}`)}
                  sx={{
                    '&:last-child td, &:last-child th': { border: 0 },
                    cursor: 'pointer',
                  }}
                >
                  <TableCell component="th" scope="row">
                    {game.id}
                  </TableCell>

                  <TableCell>{game.title}</TableCell>

                  <TableCell>{game.releaseYear}</TableCell>

                  <TableCell>
                    {game.genreNames?.map((genre) => (
                      <Chip
                        key={`${game.id}-${genre}`}
                        label={genre}
                        size="small"
                        variant="outlined"
                        sx={{ mr: 0.5, mb: 0.5 }}
                      />
                    ))}
                  </TableCell>

                  <TableCell>{game.developerName || '-'}</TableCell>

                  <TableCell>{game.publisherName || '-'}</TableCell>

                  <TableCell>
                    <Box display="flex" alignItems="center">
                      {game.averageRating !== null && game.averageRating !== undefined ? (
                        <>
                          <Rating value={game.averageRating / 20} readOnly precision={0.5} size="small" />
                          <Typography variant="body2" color="text.secondary" ml={1}>
                            {Math.round(game.averageRating)}
                          </Typography>
                        </>
                      ) : (
                        <Typography variant="caption" color="text.secondary">
                          N/A
                        </Typography>
                      )}
                    </Box>
                  </TableCell>

                  <TableCell onClick={(event) => event.stopPropagation()}>
                    <IconButton aria-label={`open game ${game.title}`} onClick={() => navigate(`/games/${game.id}`)}>
                      <VisibilityRoundedIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>

          <TablePagination
            component="div"
            count={totalElements}
            page={page}
            onPageChange={handleChangePage}
            rowsPerPage={pageSize}
            onRowsPerPageChange={handleChangePageSize}
            rowsPerPageOptions={[5, 10, 20, 50]}
          />
        </TableContainer>
      )}
    </Stack>
  );
};

export default GamesPage;
