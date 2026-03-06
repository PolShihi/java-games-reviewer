import api from '../api/axiosConfig';

class ProductionCompanyService {
    getAll(params) {
        return api.get('/production-companies', { params });
    }

    getById(id) {
        return api.get(`/production-companies/${id}`);
    }
}

export default new ProductionCompanyService();

