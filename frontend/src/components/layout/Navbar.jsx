// frontend/src/components/Navbar.jsx

import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export default function Navbar() {
  const { isLoggedIn, user, logout } = useAuth();
  const navigate  = useNavigate();
  const location  = useLocation();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
    setMenuOpen(false);
  };

  // Helper to highlight the active nav link
  const isActive = (path) => location.pathname === path;

  const navLinkClass = (path) =>
    `text-sm font-medium transition-colors ${
      isActive(path)
        ? 'text-white'
        : 'text-slate-400 hover:text-white'
    }`;

  return (
    <nav className="bg-slate-900/80 backdrop-blur-sm border-b border-slate-700/50 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-6 h-16 flex items-center justify-between">

        {/* Logo */}
        <Link to="/" className="flex items-center gap-2.5 flex-shrink-0">
          <div className="w-8 h-8 bg-gradient-to-br from-blue-500 to-blue-700 rounded-lg flex items-center justify-center shadow-lg shadow-blue-500/25">
            <span className="text-white font-bold text-xs">AI</span>
          </div>
          <span className="text-white font-bold text-base hidden sm:block">
            InterviewCoach
          </span>
        </Link>

        {/* Desktop Nav Links */}
        <div className="hidden md:flex items-center gap-6">
          {isLoggedIn ? (
            <>
              <Link to="/dashboard" className={navLinkClass('/dashboard')}>Dashboard</Link>
              <Link to="/roadmap" className={navLinkClass('/roadmap')}>Roadmap</Link>
              <Link to="/interview" className={navLinkClass('/interview')}>New Interview</Link>
              <Link to="/voice-interview" className={navLinkClass('/voice-interview')}>Voice Interview</Link>
              <Link to="/resume-analysis" className={navLinkClass('/resume-analysis')}>Resume</Link>
              <Link to="/company/preparation" className={navLinkClass('/company/preparation')}>Company Prep</Link>
              <Link to="/history"   className={navLinkClass('/history')}>History</Link>

              {/* User menu */}
              <div className="flex items-center gap-3 ml-2 pl-4 border-l border-slate-700">
                <div className="flex items-center gap-2">
                  {/* Avatar circle with first letter of name */}
                  <div className="w-8 h-8 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center">
                    <span className="text-white text-xs font-bold">
                      {user?.name?.charAt(0)?.toUpperCase() || 'U'}
                    </span>
                  </div>
                  <span className="text-slate-300 text-sm hidden lg:block">{user?.name}</span>
                </div>
                <button
                  onClick={handleLogout}
                  className="text-slate-400 hover:text-red-400 text-sm transition-colors"
                >
                  Sign Out
                </button>
              </div>
            </>
          ) : (
            <>
              <Link to="/login"  className={navLinkClass('/login')}>Sign In</Link>
              <Link
                to="/signup"
                className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
              >
                Get Started Free
              </Link>
            </>
          )}
        </div>

        {/* Mobile menu button */}
        <button
          className="md:hidden text-slate-400 hover:text-white transition-colors"
          onClick={() => setMenuOpen(!menuOpen)}
        >
          {menuOpen ? '✕' : '☰'}
        </button>
      </div>

      {/* Mobile Menu Dropdown */}
      {menuOpen && (
        <div className="md:hidden border-t border-slate-700/50 bg-slate-900 px-6 py-4 space-y-3">
          {isLoggedIn ? (
            <>
              <div className="flex items-center gap-3 pb-3 border-b border-slate-700">
                <div className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
                  <span className="text-white text-xs font-bold">{user?.name?.charAt(0)}</span>
                </div>
                <div>
                  <p className="text-white text-sm font-medium">{user?.name}</p>
                  <p className="text-slate-400 text-xs">{user?.email}</p>
                </div>
              </div>
              <Link to="/dashboard" onClick={() => setMenuOpen(false)} className="block text-slate-300 hover:text-white text-sm py-1">Dashboard</Link>
              <Link to="/roadmap" onClick={() => setMenuOpen(false)} className="block text-slate-300 hover:text-white text-sm py-1">Roadmap</Link>
              <Link to="/interview" onClick={() => setMenuOpen(false)} className="block text-slate-300 hover:text-white text-sm py-1">New Interview</Link>
              <Link to="/voice-interview" onClick={() => setMenuOpen(false)} className="block text-slate-300 hover:text-white text-sm py-1">Voice Interview</Link>
              <Link to="/resume-analysis" onClick={() => setMenuOpen(false)} className="block text-slate-300 hover:text-white text-sm py-1">Resume</Link>
              <Link to="/company/preparation" onClick={() => setMenuOpen(false)} className="block text-slate-300 hover:text-white text-sm py-1">Company Prep</Link>
              <Link to="/history"   onClick={() => setMenuOpen(false)} className="block text-slate-300 hover:text-white text-sm py-1">History</Link>
              <button onClick={handleLogout} className="block text-red-400 hover:text-red-300 text-sm py-1 w-full text-left">Sign Out</button>
            </>
          ) : (
            <>
              <Link to="/login"  onClick={() => setMenuOpen(false)} className="block text-slate-300 text-sm py-1">Sign In</Link>
              <Link to="/signup" onClick={() => setMenuOpen(false)} className="block text-blue-400 text-sm py-1 font-medium">Get Started Free</Link>
            </>
          )}
        </div>
      )}
    </nav>
  );
}