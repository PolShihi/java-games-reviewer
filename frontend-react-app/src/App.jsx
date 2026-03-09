import { BrowserRouter as Router, Navigate, Route, Routes } from 'react-router-dom';
import AppLayout from './components/layout/AppLayout';
import GameDetailsPage from './pages/GameDetailsPage';
import GameFormPage from './pages/GameFormPage';
import GamesPage from './pages/GamesPage';
import NotFoundPage from './pages/NotFoundPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/" element={<GamesPage />} />
          <Route path="/games/new" element={<GameFormPage mode="create" />} />
          <Route path="/games/:id" element={<GameDetailsPage />} />
          <Route path="/games/:id/edit" element={<GameFormPage mode="edit" />} />
          <Route path="/home" element={<Navigate to="/" replace />} />
        </Route>
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Router>
  );
}

export default App;
