import axios from 'axios';
import config from '../config';

const api = axios.create({
    baseURL: config.api.baseUrl, 
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: config.api.timeout || 5000, 
});

export default api;