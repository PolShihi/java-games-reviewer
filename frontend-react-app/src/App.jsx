import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import GamesPage from './pages/GamesPage';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<GamesPage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;