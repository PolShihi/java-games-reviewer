import api from '../api/axiosConfig';

class MediaOutletService {
    getAll(params) {
        return api.get('/media-outlets', { params });
    }

    getById(id) {
        return api.get(`/media-outlets/${id}`);
    }

    create(data) {
        return api.post('/media-outlets', data);
    }

    update(id, data) {
        return api.put(`/media-outlets/${id}`, data);
    }

    delete(id) {
        return api.delete(`/media-outlets/${id}`);
    }
}

export default new MediaOutletService();

