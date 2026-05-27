// frontend/src/context/AuthContext.jsx

import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { getStoredToken, getStoredUser, clearAuthStorage, isTokenExpired } from '../utils/tokenUtils';

// ── Create the context ──
const AuthContext = createContext(null);

// ── AuthProvider wraps the entire app ──
export function AuthProvider({ children }) {
  const [user,    setUser]    = useState(null);
  const [token,   setToken]   = useState(null);
  const [loading, setLoading] = useState(true);
  // loading = true while we check localStorage on first load
  // This prevents the flash where a logged-in user briefly sees the login page

  // ── On app start: restore session from localStorage ──
  useEffect(() => {
    const restoreSession = () => {
      try {
        const storedToken = getStoredToken();
        const storedUser  = getStoredUser();

        if (storedToken && storedUser) {
          // Check if the token has expired
          if (isTokenExpired(storedToken)) {
            // Token expired — clear everything and force re-login
            clearAuthStorage();
            console.log('Session expired. Please log in again.');
          } else {
            // Token is still valid — restore the session
            setToken(storedToken);
            setUser(storedUser);
          }
        }
      } catch (error) {
        // Something went wrong reading localStorage
        // Clear corrupted data
        clearAuthStorage();
      } finally {
        // Always set loading to false when done
        setLoading(false);
      }
    };

    restoreSession();
  }, []); // Empty array = run only once when app starts

  // ── login(): called after successful API login/signup ──
  const login = useCallback((userData, jwtToken) => {
    // Save to React state (for immediate UI updates)
    setUser(userData);
    setToken(jwtToken);

    // Save to localStorage (persists across browser refresh)
    localStorage.setItem('token', jwtToken);
    localStorage.setItem('user', JSON.stringify(userData));
  }, []);

  // ── logout(): clear everything ──
  const logout = useCallback(() => {
    setUser(null);
    setToken(null);
    clearAuthStorage();
  }, []);

  // ── updateUser(): update user info without re-login ──
  const updateUser = useCallback((updatedUserData) => {
    const newUser = { ...user, ...updatedUserData };
    setUser(newUser);
    localStorage.setItem('user', JSON.stringify(newUser));
  }, [user]);

  // ── The value all components can access ──
  const contextValue = {
    user,           // { name, email, userId }
    token,          // JWT token string
    loading,        // true while checking localStorage
    isLoggedIn: !!token && !isTokenExpired(token), // boolean
    login,          // function to call after API success
    logout,         // function to call to log out
    updateUser,     // function to update user details
  };

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
}

// ── useAuth hook: shortcut for components ──
export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used inside <AuthProvider>');
  }
  return context;
}