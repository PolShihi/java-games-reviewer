import api from '../api/axiosConfig';

class CompanyTypeService {
    getAll() {
        return api.get('/company-types');
    }

    getById(id) {
        return api.get(`/company-types/${id}`);
    }
}

export default new CompanyTypeService();

