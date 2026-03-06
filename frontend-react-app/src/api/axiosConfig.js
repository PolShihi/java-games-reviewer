import axios from 'axios';
import config from '../config';
import { toApiError } from '../utils/apiError';

const api = axios.create({
    baseURL: config.api.baseUrl, 
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: config.api.timeout || 5000, 
});

api.interceptors.response.use(
    (response) => response,
    (error) => Promise.reject(toApiError(error))
);

export default api;
