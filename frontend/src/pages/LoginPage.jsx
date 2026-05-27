// frontend/src/pages/LoginPage.jsx

import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { loginUser } from '../services/authService';
import { useFormValidation } from '../hooks/useFormValidation';
import { Alert, Button, InputField } from '../components/ui';
import LoadingButton from '../components/ui/LoadingButton';
// Validation rules
const validationRules = {
  email: {
    required: true,
    requiredMessage: 'Email address is required',
    pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    patternMessage: 'Please enter a valid email address',
  },
  password: {
    required: true,
    requiredMessage: 'Password is required',
  },
};

export default function LoginPage() {
  const [loading,    setLoading]    = useState(false);
  const [apiError,   setApiError]   = useState('');
  const [apiSuccess, setApiSuccess] = useState('');
  const [showPass,   setShowPass]   = useState(false); // toggle password visibility

  const { login }  = useAuth();
  const navigate   = useNavigate();
  const location   = useLocation();

  // If user was redirected from a protected page, go back there after login
  // e.g., they tried to go to /dashboard → were sent to /login → after login → /dashboard
  const redirectTo = location.state?.from || '/dashboard';

  const { values, errors, handleChange, handleBlur, validate } = useFormValidation(
    { email: '', password: '' },
    validationRules
  );

  const handleSubmit = async (e) => {
    e.preventDefault();
    setApiError('');
    setApiSuccess('');

    // Validate fields first
    const isValid = validate();
    if (!isValid) return;

    setLoading(true);

    try {
      // Call backend login API
      const result = await loginUser(values.email, values.password);

      if (result.success) {
        setApiSuccess(`Welcome back, ${result.data.name}! Redirecting...`);

        const userData = {
          name:   result.data.name,
          email:  result.data.email,
          userId: result.data.userId,
        };

        // Save to context + localStorage
        login(userData, result.data.token);

        // Redirect after short delay
        setTimeout(() => navigate(redirectTo, { replace: true }), 1000);
      } else {
        setApiError(result.message);
      }
    } catch (err) {
      setApiError('An unexpected error occurred. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center px-4 py-12">
      <div className="w-full max-w-md">
        <div className="bg-slate-900 border border-slate-700/50 rounded-2xl p-8 shadow-2xl">

          {/* Header */}
          <div className="text-center mb-8">
            <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-blue-700 rounded-xl flex items-center justify-center mx-auto mb-4 shadow-lg shadow-blue-500/25">
              <span className="text-white font-bold text-lg">AI</span>
            </div>
            <h1 className="text-2xl font-bold text-white">Welcome back</h1>
            <p className="text-slate-400 mt-1.5 text-sm">Sign in to continue your preparation</p>
          </div>

          {/* Alerts */}
          {apiError   && <div className="mb-5"><Alert type="error"   message={apiError}   onClose={() => setApiError('')}   /></div>}
          {apiSuccess && <div className="mb-5"><Alert type="success" message={apiSuccess} /></div>}

          {/* Form */}
          <form onSubmit={handleSubmit} className="space-y-5" noValidate>

            <InputField
              label="Email Address"
              type="email"
              name="email"
              value={values.email}
              onChange={handleChange}
              onBlur={handleBlur}
              placeholder="you@example.com"
              error={errors.email}
              required
              autoComplete="email"
            />

            {/* Password with show/hide toggle */}
            <div className="flex flex-col gap-1.5">
              <label htmlFor="password" className="text-sm font-medium text-slate-300">
                Password <span className="text-red-400">*</span>
              </label>
              <div className="relative">
                <input
                  id="password"
                  type={showPass ? 'text' : 'password'}
                  name="password"
                  value={values.password}
                  onChange={handleChange}
                  onBlur={handleBlur}
                  placeholder="Enter your password"
                  autoComplete="current-password"
                  className={`
                    w-full bg-slate-800 border rounded-lg px-4 py-3 pr-12
                    text-white placeholder-slate-500 text-sm
                    focus:outline-none focus:ring-2 transition-colors
                    ${errors.password
                      ? 'border-red-500/50 focus:border-red-500 focus:ring-red-500/20'
                      : 'border-slate-600 focus:border-blue-500 focus:ring-blue-500/20'
                    }
                  `}
                />
                {/* Show/hide password button */}
                <button
                  type="button"
                  onClick={() => setShowPass(!showPass)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-200 text-sm transition-colors"
                >
                  {showPass ? '🙈' : '👁️'}
                </button>
              </div>
              {errors.password && (
                <p className="text-red-400 text-xs flex items-center gap-1">
                  <span>⚠</span> {errors.password}
                </p>
              )}
            </div>

            <LoadingButton type="submit" loading={loading} disabled={loading}>
              Sign In
            </LoadingButton>
          </form>

          {/* Divider */}
          <div className="flex items-center gap-3 my-5">
            <div className="flex-1 h-px bg-slate-700" />
            <span className="text-slate-500 text-xs">or</span>
            <div className="flex-1 h-px bg-slate-700" />
          </div>

          {/* Signup link */}
          <p className="text-center text-slate-400 text-sm">
            Don't have an account?{' '}
            <Link to="/signup" className="text-blue-400 hover:text-blue-300 font-medium transition-colors">
              Create one free →
            </Link>
          </p>
        </div>

        {/* Backend status indicator */}
        <BackendStatus />
      </div>
    </div>
  );
}

// ── Shows whether backend is reachable ──
// Helpful during development to know if Spring Boot is running
import { useEffect } from 'react';
import { checkBackendHealth } from '../services/authService';

function BackendStatus() {
  const [status, setStatus] = useState(null); // null | 'online' | 'offline'

  useEffect(() => {
    checkBackendHealth().then(result => {
      setStatus(result.success ? 'online' : 'offline');
    });
  }, []);

  if (status === null) return null; // still checking

  return (
    <div className="mt-4 flex items-center justify-center gap-2">
      <div className={`w-2 h-2 rounded-full ${status === 'online' ? 'bg-green-400 animate-pulse' : 'bg-red-400'}`} />
      <span className="text-xs text-slate-500">
        Backend: {status === 'online' ? 'Connected' : 'Offline — start Spring Boot'}
      </span>
    </div>
  );
}