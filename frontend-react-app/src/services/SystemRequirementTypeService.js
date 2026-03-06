import api from '../api/axiosConfig';

class SystemRequirementTypeService {
    getAll() {
        return api.get('/system-requirement-types');
    }

    getById(id) {
        return api.get(`/system-requirement-types/${id}`);
    }
}

export default new SystemRequirementTypeService();

