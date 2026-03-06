import api from '../api/axiosConfig';

class ReviewService {
    getAll(params) {
        return api.get('/reviews', { params });
    }

    getByGameId(gameId, params) {
        return api.get(`/reviews/game/${gameId}`, { params });
    }

    getById(id) {
        return api.get(`/reviews/${id}`);
    }

    create(data) {
        return api.post('/reviews', data);
    }

    update(id, data) {
        return api.put(`/reviews/${id}`, data);
    }

    delete(id) {
        return api.delete(`/reviews/${id}`);
    }
}

export default new ReviewService();

