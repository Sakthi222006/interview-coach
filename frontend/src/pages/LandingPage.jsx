// frontend/src/pages/LandingPage.jsx
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function LandingPage() {
  const { isLoggedIn } = useAuth();

  return (
    <div className="min-h-screen bg-slate-950">

      {/* Hero Section */}
      <section className="max-w-7xl mx-auto px-6 pt-20 pb-32 text-center">

        {/* Badge */}
        <div className="inline-flex items-center gap-2 bg-blue-500/10 border border-blue-500/20 rounded-full px-4 py-2 mb-8">
          <div className="w-2 h-2 bg-blue-400 rounded-full animate-pulse"></div>
          <span className="text-blue-400 text-sm font-medium">AI-Powered Career Platform</span>
        </div>

        {/* Main Heading */}
        <h1 className="text-5xl md:text-7xl font-bold text-white mb-6 leading-tight">
          Ace Your Next
          <span className="block text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-cyan-400">
            Job Interview
          </span>
        </h1>

        {/* Subtitle */}
        <p className="text-xl text-slate-400 mb-10 max-w-2xl mx-auto leading-relaxed">
          Practice with AI-generated questions, get real-time emotion analysis,
          and receive personalized feedback to land your dream job.
        </p>

        {/* CTA Buttons */}
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          {isLoggedIn ? (
            <Link
              to="/dashboard"
              className="bg-blue-600 hover:bg-blue-700 text-white px-8 py-4 rounded-xl text-lg font-semibold transition-all transform hover:scale-105"
            >
              Go to Dashboard →
            </Link>
          ) : (
            <>
              <Link
                to="/signup"
                className="bg-blue-600 hover:bg-blue-700 text-white px-8 py-4 rounded-xl text-lg font-semibold transition-all transform hover:scale-105"
              >
                Start Free Today →
              </Link>
              <Link
                to="/login"
                className="border border-slate-600 hover:border-slate-400 text-slate-300 hover:text-white px-8 py-4 rounded-xl text-lg font-semibold transition-all"
              >
                Sign In
              </Link>
            </>
          )}
        </div>
      </section>

      {/* Features Section */}
      <section className="max-w-7xl mx-auto px-6 pb-20">
        <h2 className="text-3xl font-bold text-white text-center mb-12">
          Everything You Need to Succeed
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {features.map((feature, index) => (
            <div
              key={index}
              className="bg-slate-900 border border-slate-700 rounded-2xl p-6 hover:border-blue-500/50 transition-colors"
            >
              <div className="text-4xl mb-4">{feature.icon}</div>
              <h3 className="text-white font-semibold text-lg mb-2">{feature.title}</h3>
              <p className="text-slate-400 text-sm leading-relaxed">{feature.description}</p>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}

// Feature cards data
const features = [
  {
    icon: '🤖',
    title: 'AI Interview Questions',
    description: 'Get personalized questions based on your resume and target role, powered by advanced AI.'
  },
  {
    icon: '📸',
    title: 'Emotion Detection',
    description: 'Real-time webcam analysis detects your confidence, nervousness, and engagement levels.'
  },
  {
    icon: '🎤',
    title: 'Speech Analysis',
    description: 'Your spoken answers are transcribed and analyzed for clarity, pace, and confidence.'
  },
  {
    icon: '📊',
    title: 'Confidence Scoring',
    description: 'Get a detailed confidence score with areas of improvement highlighted clearly.'
  },
  {
    icon: '📄',
    title: 'PDF Reports',
    description: 'Download detailed PDF feedback reports to track your progress over time.'
  },
  {
    icon: '📈',
    title: 'Progress Analytics',
    description: 'Visual dashboard shows your improvement across multiple interview sessions.'
  },
];