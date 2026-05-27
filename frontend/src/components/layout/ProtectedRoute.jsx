// frontend/src/components/ProtectedRoute.jsx

import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { LoadingSpinner } from '../ui';

export default function ProtectedRoute({ children }) {
  const { isLoggedIn, loading } = useAuth();
  const location = useLocation();
  // location.pathname = current URL (e.g., "/dashboard")
  // We save this so after login we can redirect back to where they were going

  // ── While checking localStorage, show a spinner ──
  // Without this, logged-in users see a flash of the login page
  if (loading) {
    return <LoadingSpinner message="Checking authentication..." />;
  }

  // ── If not logged in, redirect to login ──
  if (!isLoggedIn) {
    return (
      <Navigate
        to="/login"
        state={{ from: location.pathname }}
        // Pass the current path so login page can redirect back after login
        replace
      />
    );
  }

  // ── If logged in, show the page ──
  return children;
}