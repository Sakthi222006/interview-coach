// frontend/src/pages/SignupPage.jsx

import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { signupUser } from '../services/authService';
import { useFormValidation } from '../hooks/useFormValidation';
import { Alert, Button, InputField } from '../components/ui';

// ── Validation rules for each field ──
const validationRules = {
  name: {
    required: true,
    requiredMessage: 'Full name is required',
    minLength: 2,
    minLengthMessage: 'Name must be at least 2 characters',
  },
  email: {
    required: true,
    requiredMessage: 'Email address is required',
    pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    patternMessage: 'Please enter a valid email address',
  },
  password: {
    required: true,
    requiredMessage: 'Password is required',
    minLength: 6,
    minLengthMessage: 'Password must be at least 6 characters',
  },
  confirmPassword: {
    required: true,
    requiredMessage: 'Please confirm your password',
    // custom validation: check it matches password
    custom: (value, allValues) => {
      if (value !== allValues.password) return 'Passwords do not match';
      return '';
    },
  },
};

export default function SignupPage() {
  const [loading,    setLoading]    = useState(false);
  const [apiError,   setApiError]   = useState('');    // error from backend
  const [apiSuccess, setApiSuccess] = useState('');    // success message

  const { login }  = useAuth();
  const navigate   = useNavigate();

  // useFormValidation manages field values and per-field errors
  const { values, errors, handleChange, handleBlur, validate } = useFormValidation(
    { name: '', email: '', password: '', confirmPassword: '' },
    validationRules
  );

  const handleSubmit = async (e) => {
    e.preventDefault();
    setApiError('');
    setApiSuccess('');

    // ── Step 1: Validate all fields ──
    const isValid = validate();
    if (!isValid) return; // Stop if any field has an error

    setLoading(true);

    try {
      // ── Step 2: Call the backend API ──
      const result = await signupUser(values.name, values.email, values.password);

      if (result.success) {
        // ── Step 3a: Success! Save auth data and redirect ──
        setApiSuccess('Account created! Redirecting to your dashboard...');

        // Build user object from the response
        const userData = {
          name:   result.data.name,
          email:  result.data.email,
          userId: result.data.userId,
        };

        // Save to AuthContext + localStorage
        login(userData, result.data.token);

        // Short delay so user sees the success message
        setTimeout(() => navigate('/dashboard'), 1500);
      } else {
        // ── Step 3b: Backend returned an error ──
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

        {/* Card */}
        <div className="bg-slate-900 border border-slate-700/50 rounded-2xl p-8 shadow-2xl">

          {/* Header */}
          <div className="text-center mb-8">
            <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-blue-700 rounded-xl flex items-center justify-center mx-auto mb-4 shadow-lg shadow-blue-500/25">
              <span className="text-white font-bold text-lg">AI</span>
            </div>
            <h1 className="text-2xl font-bold text-white">Create your account</h1>
            <p className="text-slate-400 mt-1.5 text-sm">Start your interview preparation journey</p>
          </div>

          {/* API-level alerts (show above the form) */}
          {apiError   && <div className="mb-5"><Alert type="error"   message={apiError}   onClose={() => setApiError('')}   /></div>}
          {apiSuccess && <div className="mb-5"><Alert type="success" message={apiSuccess} /></div>}

          {/* Form */}
          <form onSubmit={handleSubmit} className="space-y-5" noValidate>

            <InputField
              label="Full Name"
              type="text"
              name="name"
              value={values.name}
              onChange={handleChange}
              onBlur={handleBlur}
              placeholder="John Doe"
              error={errors.name}
              required
              autoComplete="name"
            />

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

            <InputField
              label="Password"
              type="password"
              name="password"
              value={values.password}
              onChange={handleChange}
              onBlur={handleBlur}
              placeholder="At least 6 characters"
              error={errors.password}
              required
              autoComplete="new-password"
            />

            <InputField
              label="Confirm Password"
              type="password"
              name="confirmPassword"
              value={values.confirmPassword}
              onChange={handleChange}
              onBlur={handleBlur}
              placeholder="Repeat your password"
              error={errors.confirmPassword}
              required
              autoComplete="new-password"
            />

            {/* Password strength indicator */}
            {values.password && (
              <PasswordStrength password={values.password} />
            )}

            <LoadingButton
              type="submit"
              loading={loading}
              disabled={loading}
            >
              Create Account
            </LoadingButton>
          </form>

          {/* Terms note */}
          <p className="text-center text-slate-500 text-xs mt-4">
            By signing up, you agree to our{' '}
            <span className="text-blue-400 cursor-pointer hover:text-blue-300">Terms of Service</span>
          </p>

          {/* Login link */}
          <p className="text-center text-slate-400 text-sm mt-6">
            Already have an account?{' '}
            <Link to="/login" className="text-blue-400 hover:text-blue-300 font-medium transition-colors">
              Sign in →
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}

// ── Password Strength Meter ──
function PasswordStrength({ password }) {
  const getStrength = (pwd) => {
    let score = 0;
    if (pwd.length >= 6)  score++;
    if (pwd.length >= 10) score++;
    if (/[A-Z]/.test(pwd)) score++;
    if (/[0-9]/.test(pwd)) score++;
    if (/[^A-Za-z0-9]/.test(pwd)) score++;
    return score;
  };

  const strength = getStrength(password);
  const levels   = [
    { label: 'Very Weak', color: 'bg-red-500'    },
    { label: 'Weak',      color: 'bg-orange-500' },
    { label: 'Fair',      color: 'bg-yellow-500' },
    { label: 'Good',      color: 'bg-blue-500'   },
    { label: 'Strong',    color: 'bg-green-500'  },
  ];

  const level = levels[Math.min(strength, levels.length - 1)];

  return (
    <div className="space-y-1.5">
      <div className="flex gap-1">
        {[1, 2, 3, 4, 5].map(i => (
          <div
            key={i}
            className={`h-1 flex-1 rounded-full transition-all duration-300 ${
              i <= strength ? level.color : 'bg-slate-700'
            }`}
          />
        ))}
      </div>
      <p className="text-xs text-slate-400">
        Password strength: <span className="font-medium text-slate-300">{level.label}</span>
      </p>
    </div>
  );
}