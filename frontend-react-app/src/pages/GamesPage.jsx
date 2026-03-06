import React, { useCallback, useEffect, useState } from 'react';
import GameService from '../services/GameService';
import log from '../services/Logger';
import { normalizePageResponse } from '../utils/pageResponse';

import { 
  Container, 
  Typography, 
  CircularProgress, 
  Alert, 
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  Box,
  Rating
} from '@mui/material';

const GamesPage = () => {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [page, _setPage] = useState(0);
  const [pageSize, _setPageSize] = useState(10);

  const fetchGames = useCallback(async () => {
    try {
      setLoading(true);
      
      const response = await GameService.getAll({ 
        page: page, 
        size: pageSize,
        sortBy: 'id',     
        sortDirection: 'ASC' 
      });

      const pageData = normalizePageResponse(response.data);
      const gamesData = pageData.content;

      setGames(gamesData);
      setError(null);
    } catch (err) {
      log.error('Error fetching games:', err);
      setError('Failed to load game list. Check your connection to the server.');
    } finally {
      setLoading(false);
    }
  }, [page, pageSize]);

  useEffect(() => {
    fetchGames();
  }, [fetchGames]);

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" mt={10}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4 }}>
        <Alert severity="error">{error}</Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'bold' }}>
        Game Library
      </Typography>
      
      {games.length === 0 ? (
        <Alert severity="info">Games not found.</Alert>
      ) : (
        <TableContainer component={Paper} elevation={3}>
          <Table sx={{ minWidth: 650 }} aria-label="games table">
            <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
              <TableRow>
                <TableCell><strong>ID</strong></TableCell>
                <TableCell><strong>Title</strong></TableCell>
                <TableCell><strong>Release Year</strong></TableCell>
                <TableCell><strong>Genres</strong></TableCell>
                <TableCell><strong>Developer</strong></TableCell>
                <TableCell><strong>Publisher</strong></TableCell>
                <TableCell><strong>Rating</strong></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {games.map((game) => (
                <TableRow
                  key={game.id}
                  sx={{ '&:last-child td, &:last-child th': { border: 0 }, '&:hover': { backgroundColor: '#fafafa' } }}
                >
                  <TableCell component="th" scope="row">
                    {game.id}
                  </TableCell>
                  
                  <TableCell>{game.title}</TableCell>
                  
                  <TableCell>{game.releaseYear}</TableCell>
                  
                  <TableCell>
                    {game.genreNames && game.genreNames.map((genre, index) => (
                      <Chip 
                        key={index} 
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
                      {game.averageRating ? (
                        <>
                          <Rating 
                            value={game.averageRating / 20}
                            readOnly 
                            precision={0.5} 
                            size="small" 
                          />
                          <Typography variant="body2" color="text.secondary" ml={1}>
                            {Math.round(game.averageRating)}
                          </Typography>
                        </>
                      ) : (
                        <Typography variant="caption" color="text.secondary">N/A</Typography>
                      )}
                    </Box>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Container>
  );
};

export default GamesPage;
