// frontend/src/services/authService.js

import axiosInstance from '../api/axiosInstance';

// ─────────────────────────────────────────────────────────────
// SIGNUP
// Sends: { name, email, password }
// Gets back: { token, name, email, userId, message }
// ─────────────────────────────────────────────────────────────
export async function signupUser(name, email, password) {
  try {
    const response = await axiosInstance.post('/api/auth/signup', {
      name,
      email,
      password,
    });
    return { success: true, data: response.data };
  } catch (error) {
    // Extract the error message from backend response
    const message = extractErrorMessage(error);
    return { success: false, message };
  }
}

// ─────────────────────────────────────────────────────────────
// LOGIN
// Sends: { email, password }
// Gets back: { token, name, email, userId, message }
// ─────────────────────────────────────────────────────────────
export async function loginUser(email, password) {
  try {
    const response = await axiosInstance.post('/api/auth/login', {
      email,
      password,
    });
    return { success: true, data: response.data };
  } catch (error) {
    const message = extractErrorMessage(error);
    return { success: false, message };
  }
}

// ─────────────────────────────────────────────────────────────
// HEALTH CHECK
// Checks if the backend is reachable
// ─────────────────────────────────────────────────────────────
export async function checkBackendHealth() {
  try {
    const response = await axiosInstance.get('/api/auth/health');
    return { success: true, data: response.data };
  } catch (error) {
    return { success: false, message: 'Backend is not reachable' };
  }
}

// ─────────────────────────────────────────────────────────────
// HELPER: Extract a readable error message from Axios errors
// ─────────────────────────────────────────────────────────────
function extractErrorMessage(error) {
  // Case 1: Backend sent a response with an error message
  if (error.response?.data?.message) {
    return error.response.data.message;
  }

  // Case 2: Backend sent a response but no message field
  if (error.response?.data) {
    // Spring validation errors come as an object
    if (typeof error.response.data === 'object') {
      // Get the first validation error message
      const firstError = Object.values(error.response.data)[0];
      if (firstError) return firstError;
    }
    return String(error.response.data);
  }

  // Case 3: Network error (backend is down)
  if (error.code === 'ECONNABORTED') {
    return 'Request timed out. Please try again.';
  }

  if (!error.response) {
    return 'Cannot connect to server. Is the backend running?';
  }

  // Case 4: Generic fallback
  return 'Something went wrong. Please try again.';
}