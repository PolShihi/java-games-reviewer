import api from '../api/axiosConfig';

class MediaOutletService {
    getAll(params) {
        return api.get('/media-outlets', { params });
    }

    getById(id) {
        return api.get(`/media-outlets/${id}`);
    }
}

export default new MediaOutletService();

