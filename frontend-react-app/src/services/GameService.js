import api from '../api/axiosConfig';

class GameService {
    getAll(params) {
        return api.get('/games', { params });
    }

    getById(id) {
        return api.get(`/games/${id}`);
    }

    create(data) {
        return api.post('/games', data);
    }

    update(id, data) {
        return api.put(`/games/${id}`, data);
    }

    delete(id) {
        return api.delete(`/games/${id}`);
    }
}

export default new GameService();