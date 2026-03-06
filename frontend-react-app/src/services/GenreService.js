import api from '../api/axiosConfig';

class GenreService {
    getAll() {
        return api.get('/genres');
    }

    getById(id) {
        return api.get(`/genres/${id}`);
    }
}

export default new GenreService();

