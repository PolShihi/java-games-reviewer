import api from '../api/axiosConfig';

class GenreService {
    getAll() {
        return api.get('/genres');
    }

    getById(id) {
        return api.get(`/genres/${id}`);
    }

    create(data) {
        return api.post('/genres', data);
    }

    update(id, data) {
        return api.put(`/genres/${id}`, data);
    }

    delete(id) {
        return api.delete(`/genres/${id}`);
    }
}

export default new GenreService();

