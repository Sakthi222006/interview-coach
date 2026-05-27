// frontend/src/pages/Dashboard.jsx

import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Card, Button, Badge, Avatar } from '../components/ui';
import { useAuth }                     from '../context/AuthContext';
import { useAnalytics }                from '../hooks/useAnalytics';
import { TrendChart }                  from '../components/features/analytics/TrendChart';
import { SkillRadar }                  from '../components/features/analytics/SkillRadar';
import { RoadmapCard }                 from '../components/features/analytics/RoadmapCard';
import { WeakTopicBanner }             from '../components/features/analytics/WeakTopicBanner';
import { SessionReviewTable }          from '../components/features/analytics/SessionReviewTable';

function AnalyticsArea() {
  const navigate = useNavigate();
  const { trend, topics, radar, roadmap, adaptive, sessionReview, loading, error, refresh } = useAnalytics();

  if (loading) {
    return (
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div className="card p-6"><div className="skeleton h-48 rounded"/></div>
        <div className="card p-6"><div className="skeleton h-48 rounded"/></div>
        <div className="card p-6"><div className="skeleton h-48 rounded"/></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="card p-6" style={{ borderColor: 'rgba(239,68,68,0.12)' }}>
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm font-semibold" style={{ color: 'var(--color-status-error)' }}>Analytics failed to load</p>
            <p className="text-xs" style={{ color: 'var(--color-content-muted)' }}>{error}</p>
          </div>
          <div>
            <button className="btn btn-ghost" onClick={refresh}>Retry</button>
          </div>
        </div>
      </div>
    );
  }

  const hasData = (trend && Array.isArray(trend.dataPoints) && trend.dataPoints.length > 0)
    || radar
    || (roadmap?.items?.length > 0)
    || (topics?.weakTopics?.length > 0)
    || adaptive
    || (sessionReview?.answers?.length > 0);

  if (!hasData) {
    return (
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div className="card p-6 text-center">No analytics yet — complete interviews to populate insights.</div>
        <div className="card p-6 text-center">Start practicing to unlock roadmap and recommendations.</div>
        <div className="card p-6 text-center">Your performance trend will appear here.</div>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="card p-6">
        <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between mb-4">
          <div>
            <p className="text-sm font-semibold text-white">Performance Trend</p>
            <p className="text-slate-400 text-xs mt-1">Monitor your progress across recent sessions.</p>
          </div>
          <div className="flex flex-wrap gap-3">
            <button className="btn btn-secondary" onClick={refresh}>Refresh Analytics</button>
            <button className="btn btn-primary" onClick={() => navigate('/roadmap')}>View Full Roadmap</button>
          </div>
        </div>
        <TrendChart dataPoints={trend?.dataPoints || []} />
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-[1.4fr_0.6fr] gap-4">
        <div className="space-y-4">
          <WeakTopicBanner weakTopics={topics?.weakTopics || []} strongTopics={topics?.strongTopics || []} />

          <div className="card p-6">
            <div className="flex items-center justify-between mb-4">
              <div>
                <p className="text-sm font-semibold text-white">Adaptive recommendation</p>
                <p className="text-slate-400 text-xs mt-1">Recommended next practice based on your current weak topic.</p>
              </div>
            </div>
            {adaptive ? (
              <div className="space-y-2">
                <p className="text-sm text-white">Focus: <span className="font-semibold">{adaptive.topic}</span></p>
                <p className="text-sm text-slate-300">Difficulty: {adaptive.recommendedDifficulty || 'Balanced'}</p>
                <p className="text-xs text-slate-400">{adaptive.reason || 'Practice this topic to improve your score and confidence.'}</p>
              </div>
            ) : (
              <p className="text-sm text-slate-400">Complete at least one interview to unlock adaptive recommendations.</p>
            )}
          </div>

          <div className="card p-6">
            <div className="flex items-center justify-between mb-4">
              <div>
                <p className="text-sm font-semibold text-white">Recent session review</p>
                <p className="text-slate-400 text-xs mt-1">Inspect the latest session breakdown and improvement areas.</p>
              </div>
            </div>
            {sessionReview?.answers?.length ? (
              <SessionReviewTable answers={sessionReview.answers.slice(0, 2)} />
            ) : (
              <p className="text-sm text-slate-400">No recent session review available yet.</p>
            )}
          </div>
        </div>

        <div className="space-y-4">
          <SkillRadar radar={radar} />
          <RoadmapCard roadmap={roadmap} />
        </div>
      </div>
    </div>
  );
}

export default function Dashboard() {
  const { user, logout } = useAuth();
  const navigate         = useNavigate();
  const [greeting, setGreeting] = useState('');

  // Set greeting based on time of day
  useEffect(() => {
    const hour = new Date().getHours();
    if (hour < 12) setGreeting('Good morning');
    else if (hour < 17) setGreeting('Good afternoon');
    else setGreeting('Good evening');
  }, []);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // Placeholder stats — real data in Phase 6
  const stats = [
    { label: 'Interviews Done',  value: '0',  icon: '🎯', color: 'text-blue-400',   bg: 'bg-blue-500/10'   },
    { label: 'Avg Confidence',   value: '—',  icon: '💪', color: 'text-green-400',  bg: 'bg-green-500/10'  },
    { label: 'Questions Solved', value: '0',  icon: '❓', color: 'text-purple-400', bg: 'bg-purple-500/10' },
    { label: 'Practice Hours',   value: '0h', icon: '⏱️', color: 'text-orange-400', bg: 'bg-orange-500/10' },
  ];

  const quickActions = [
    {
      title: 'Start New Interview',
      desc:  'Begin an AI-powered mock interview session',
      icon:  '🚀',
      link:  '/interview/setup',
      gradient: 'from-blue-600 to-blue-700',
      primary: true,
    },
    {
      title: 'Resume Analysis',
      desc:  'Upload your resume and discover gaps, scores, and keywords',
      icon:  '📄',
      link:  '/resume-analysis',
      gradient: 'from-green-600 to-emerald-600',
    },
    {
      title: 'Voice Mock Interview',
      desc:  'Practice answers by speaking and receive live AI coaching.',
      icon:  '🎤',
      link:  '/voice-interview',
      gradient: 'from-purple-600 to-fuchsia-600',
    },
    {
      title: 'Readiness Review',
      desc:  'Check your resume readiness for a target role',
      icon:  '✅',
      link:  '/resume-readiness',
      gradient: 'from-indigo-600 to-slate-700',
    },
  ];

  return (
    <div className="min-h-screen bg-slate-950">
      <div className="max-w-7xl mx-auto px-6 py-8">

        {/* ── Header ── */}
        <div className="flex items-start justify-between mb-8">
          <div>
            <p className="text-slate-400 text-sm mb-1">{greeting} 👋</p>
            <h1 className="text-3xl font-bold text-white">
              {user?.name || 'User'}
            </h1>
            <p className="text-slate-500 text-sm mt-1">{user?.email}</p>
          </div>
          <Button
  variant="ghost"
  size="sm"
  onClick={handleLogout}
>
  Sign Out
</Button>
        </div>

        {/* ── Welcome Banner (shown when 0 interviews) ── */}
        <div className="bg-gradient-to-r from-blue-600/20 to-cyan-600/20 border border-blue-500/20 rounded-2xl p-6 mb-8">
          <div className="flex items-center gap-4">
            <div className="text-4xl">🎉</div>
            <div>
              <h2 className="text-white font-semibold text-lg">
                Welcome to InterviewCoach!
              </h2>
              <p className="text-blue-300/80 text-sm mt-1">
                Your account is set up. Start your first mock interview to get AI-powered feedback.
              </p>
            </div>
          </div>
        </div>

        {/* ── Stats Grid ── */}
       
<div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
  {stats.map((stat, i) => (
    <Card
      key={i}
      accent="blue"
      padding="md"
      className="relative overflow-hidden"
    >
      <div
        className={`w-10 h-10 ${stat.bg} rounded-lg flex items-center justify-center mb-3 text-xl`}
      >
        {stat.icon}
      </div>

      <div className={`text-2xl font-bold ${stat.color}`}>
        {stat.value}
      </div>

      <div className="text-slate-400 text-xs mt-1">
        {stat.label}
      </div>
    </Card>
  ))}
</div>

        {/* ── Quick Actions ── */}
        <div className="mb-8">
          <h2 className="text-lg font-semibold text-white mb-4">Quick Actions</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {quickActions.map((action, i) => (
              <Link
                key={i}
                to={action.link}
                className={`
                  bg-gradient-to-br ${action.gradient}
                  border border-slate-700/50 rounded-xl p-6
                  transition-all hover:scale-[1.02] hover:shadow-xl
                  hover:shadow-blue-500/10 group
                `}
              >
                <div className="text-3xl mb-4">{action.icon}</div>
                <h3 className="text-white font-semibold mb-1">{action.title}</h3>
                <p className="text-slate-400 text-sm">{action.desc}</p>
                <div className="mt-4 text-blue-400 text-sm group-hover:text-blue-300 transition-colors">
                  Get started →
                </div>
              </Link>
            ))}
          </div>
        </div>

        {/* ── Analytics Section (Phase 6) ── */}
        <div className="mb-8">
          <h2 className="text-lg font-semibold text-white mb-4">Performance Analytics</h2>

          {/* loading / error handling via hook */}
          <AnalyticsArea />
        </div>

        {/* ── Recent Activity ── */}
        <div>
          <h2 className="text-lg font-semibold text-white mb-4">Recent Interviews</h2>
          <div className="bg-slate-900 border border-slate-700/50 rounded-xl p-12 text-center">
            <div className="text-6xl mb-4 opacity-50">📋</div>
            <p className="text-slate-400 font-medium">No interviews yet</p>
            <p className="text-slate-500 text-sm mt-1">
              Complete your first session to see results here
            </p>
            <Button
  variant="primary"
  onClick={() => navigate('/interview/setup')}
>
  Start First Interview
</Button>
          </div>
        </div>

        {/* ── Logged in as info (dev helper) ── */}
        <div className="mt-8 p-4 bg-slate-900/50 border border-slate-800 rounded-xl">
          <p className="text-slate-500 text-xs text-center">
            Logged in as <span className="text-slate-300">{user?.email}</span>
            {' '}· User ID: <span className="text-slate-300">#{user?.userId}</span>
            {' '}· JWT stored in localStorage ✅
          </p>
        </div>
      </div>
    </div>
  );
}