import api from '../api/axiosConfig';

class ProductionCompanyService {
    getAll(params) {
        return api.get('/production-companies', { params });
    }

    getById(id) {
        return api.get(`/production-companies/${id}`);
    }

    create(data) {
        return api.post('/production-companies', data);
    }

    update(id, data) {
        return api.put(`/production-companies/${id}`, data);
    }

    delete(id) {
        return api.delete(`/production-companies/${id}`);
    }
}

export default new ProductionCompanyService();

