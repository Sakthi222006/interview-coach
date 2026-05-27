// frontend/src/api/axiosInstance.js

import axios from 'axios';

// ── Step 1: Create a custom Axios instance ──
// This is like creating a pre-configured version of axios
const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  // ↑ reads from .env file. Falls back to localhost if .env not found

  timeout: 10000,
  // ↑ If the server doesn't respond in 10 seconds, cancel the request

  headers: {
    'Content-Type': 'application/json',
    // ↑ Tell the server: "I'm sending JSON data"
  },
});

// ── Step 2: REQUEST Interceptor ──
// This runs BEFORE every request is sent
// Think of it as a "checkpoint" that adds the JWT token automatically
axiosInstance.interceptors.request.use(
  (config) => {
    // Get the token from localStorage
    const token = localStorage.getItem('token');

    if (token) {
      // Attach token to the Authorization header
      // Spring Boot's JwtAuthFilter reads this header
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config; // Send the request with the modified config
  },
  (error) => {
    // If something went wrong BUILDING the request
    return Promise.reject(error);
  }
);

// ── Step 3: RESPONSE Interceptor ──
// This runs AFTER every response comes back
// Think of it as a "handler" for common errors
axiosInstance.interceptors.response.use(
  (response) => {
    // If response is successful (2xx status), just return it normally
    return response;
  },
  (error) => {
    // If response has an error status code:

    if (error.response?.status === 401) {
      // 401 = Unauthorized = token is expired or invalid
      // Clear stored credentials and redirect to login
      localStorage.removeItem('token');
      localStorage.removeItem('user');

      // Only redirect if not already on the login page
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }

    if (error.response?.status === 403) {
      // 403 = Forbidden = user doesn't have permission
      console.warn('Access forbidden:', error.response.data);
    }

    if (error.code === 'ECONNABORTED') {
      // Request timed out
      console.error('Request timed out. Is the backend running?');
    }

    if (!error.response) {
      // Network error — backend might be down
      console.error('Network error. Backend may be offline.');
    }

    // Pass the error along so the calling code can handle it too
    return Promise.reject(error);
  }
);

export default axiosInstance;