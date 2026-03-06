import api from '../api/axiosConfig';

class SystemRequirementService {
    getAll(params) {
        return api.get('/system-requirements', { params });
    }

    getByGameId(gameId, params) {
        return api.get(`/system-requirements/game/${gameId}`, { params });
    }

    getById(id) {
        return api.get(`/system-requirements/${id}`);
    }

    create(data) {
        return api.post('/system-requirements', data);
    }

    update(id, data) {
        return api.put(`/system-requirements/${id}`, data);
    }

    delete(id) {
        return api.delete(`/system-requirements/${id}`);
    }
}

export default new SystemRequirementService();

