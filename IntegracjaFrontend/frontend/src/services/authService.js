import axios from 'axios';

const API_URL = 'http://localhost:3001/api';

// Configure axios to include credentials
axios.defaults.withCredentials = true;

export const login = async (username, password) => {
    try {
        const response = await axios.post(`${API_URL}/login`, { username, password });
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};

export const logout = async () => {
    try {
        await axios.post(`${API_URL}/logout`);
    } catch (error) {
        console.error('Logout error:', error);
    }
};

export const getToken = async () => {
    try {
        // the token is in cookies, test request will check by middleware
        await axios.get(`${API_URL}/verify`);
        return true; 
    } catch (error) {
        return false; 
    }
};

export const isAuthenticated = async () => {
    return await getToken();
};

// Update axios configuration to include credentials
export const setupAxios = () => {
    axios.interceptors.response.use(
        response => response,
        error => {
            if (error.response?.status === 401) {
                // Handle unauthorized access
                window.location.href = '/login';
            }
            return Promise.reject(error);
        }
    );
}; 