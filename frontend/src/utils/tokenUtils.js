// frontend/src/utils/tokenUtils.js

// ── Decode a JWT token without any library ──
// A JWT looks like: header.payload.signature
// The payload (middle part) contains user data encoded in Base64
export function decodeToken(token) {
  try {
    // Split the token by "." and take the middle part (payload)
    const base64Payload = token.split('.')[1];

    // Decode the Base64 string to get JSON
    const jsonPayload = atob(base64Payload);

    // Parse JSON and return as JavaScript object
    return JSON.parse(jsonPayload);
  } catch (error) {
    // If token is malformed, return null
    return null;
  }
}

// ── Check if a token is expired ──
export function isTokenExpired(token) {
  const decoded = decodeToken(token);
  if (!decoded) return true; // Can't decode = treat as expired

  // JWT stores expiry as Unix timestamp (seconds since 1970)
  // JavaScript Date.now() is in milliseconds, so we multiply by 1000
  const currentTime = Date.now() / 1000;
  return decoded.exp < currentTime;
  // If expiry time is in the past → token is expired
}

// ── Get stored token from localStorage ──
export function getStoredToken() {
  return localStorage.getItem('token');
}

// ── Get stored user from localStorage ──
export function getStoredUser() {
  try {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  } catch {
    return null;
  }
}

// ── Clear all auth data from localStorage ──
export function clearAuthStorage() {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
}