// frontend/src/pages/FeedbackPage.jsx
import { useLocation, useNavigate, Link } from 'react-router-dom';
import { Card, Button, Badge }            from '../components/ui';
import { PageContainer }                  from '../components/layout';
import { cn }                             from '../utils/cn';
import { useState, useEffect, useMemo }    from 'react';
import { getSessionReview }                from '../services/analyticsService';
import { SessionReviewTable }              from '../components/features/analytics/SessionReviewTable';
import { StarBreakdown }                   from '../components/features/interview/StarBreakdown';
import { useAuth }                         from '../context/AuthContext';

export default function FeedbackPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { user } = useAuth();

  const session   = location.state?.session;
  const totalTime = location.state?.totalTime || 0;

  const [review, setReview] = useState(null);
  const [answers, setAnswers] = useState(location.state?.answers || []);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const aggregatedStrengths = useMemo(() => {
    const s = new Set();
    (answers || []).forEach(a => (a.strengths || []).forEach(x => x && s.add(x)));
    return Array.from(s).slice(0, 8);
  }, [answers]);

  const aggregatedImprovements = useMemo(() => {
    const m = new Set();
    (answers || []).forEach(a => (a.improvements || []).forEach(x => x && m.add(x)));
    return Array.from(m).slice(0, 8);
  }, [answers]);

  // Guard: if accessed directly without state
  if (!session) {
    return (
      <div className="min-h-screen flex items-center justify-center"
           style={{ background: 'var(--color-surface-1)' }}>
        <div className="text-center space-y-4">
          <p className="text-content-muted">No session data found.</p>
          <Button variant="primary" onClick={() => navigate('/dashboard')}>
            Go to Dashboard
          </Button>
        </div>
      </div>
    );
  }

  const score        = session.score || 0;
  const totalQ       = session.totalQuestions || 0;
  const answered     = session.answeredQuestions || 0;
  const correctCount = Math.round((score / 100) * totalQ);

  // Score tier
  const tier =
    score >= 80 ? { label: 'Excellent!',    color: 'text-status-success', emoji: '🏆' } :
    score >= 60 ? { label: 'Good Job!',      color: 'text-brand-400',      emoji: '👍' } :
    score >= 40 ? { label: 'Keep Practicing', color: 'text-amber-400',     emoji: '📚' } :
                  { label: 'Keep Going!',    color: 'text-content-muted',  emoji: '💪' };

  const formatDuration = (secs) => {
    if (!secs) return '—';
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return m > 0 ? `${m}m ${s}s` : `${s}s`;
  };

  useEffect(() => {
    const load = async () => {
      if (!session) return;
      setLoading(true);
      setError('');
      const res = await getSessionReview(session.id || session.sessionId || session._id);
      if (res.success) {
        setReview(res.data);
        if (res.data?.answers) setAnswers(res.data.answers);
      } else {
        setError('Failed to load session review.');
      }
      setLoading(false);
    };
    load();
  }, [session]);

  return (
    <div className="min-h-screen" style={{ background: 'var(--color-surface-1)' }}>
      <PageContainer size="md">

        {/* ── Score Hero ── */}
        <div className="text-center py-10">
          <div className="text-6xl mb-4">{tier.emoji}</div>
          <h1 className={cn('text-4xl font-bold mb-2', tier.color)}>
            {score.toFixed(1)}%
          </h1>
          <p className="text-content-primary text-xl font-semibold">{tier.label}</p>
          <p className="text-content-muted text-sm mt-2">
            {correctCount} correct out of {totalQ} questions
          </p>
        </div>

        {/* ── Stats grid ── */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-8">
          {[
            { label: 'Topic',      value: session.topic,               color: 'blue'   },
            { label: 'Difficulty', value: session.difficulty,          color: 'amber'  },
            { label: 'Duration',   value: formatDuration(session.durationSeconds || totalTime), color: 'purple' },
            { label: 'Questions',  value: `${answered}/${totalQ}`,     color: 'green'  },
          ].map((stat, i) => (
            <Card key={i} accent={stat.color} padding="md" className="text-center">
              <p className="text-2xl font-bold text-content-primary mb-1">{stat.value}</p>
              <p className="text-xs text-content-muted uppercase tracking-wide">{stat.label}</p>
            </Card>
          ))}
        </div>

        {/* ── Score bar ── */}
        <Card padding="lg" className="mb-8">
          <div className="flex items-center justify-between mb-3">
            <span className="text-sm font-medium text-content-secondary">Score Breakdown</span>
            <span className="text-sm font-bold text-content-primary">{score.toFixed(1)}%</span>
          </div>
          <div className="h-3 bg-surface-4 rounded-full overflow-hidden">
            <div
              className={cn(
                'h-full rounded-full transition-all duration-1000',
                score >= 80 ? 'bg-status-success' :
                score >= 60 ? 'bg-brand-500' :
                score >= 40 ? 'bg-amber-500' : 'bg-status-error'
              )}
              style={{ width: `${score}%` }}
            />
          </div>
          <div className="flex justify-between text-xs text-content-disabled mt-2">
            <span>0%</span>
            <span>Pass: 60%</span>
            <span>100%</span>
          </div>
        </Card>

        {/* ── Action buttons ── */}
        <div className="flex flex-col sm:flex-row gap-3">
          <Button
            variant="primary"
            size="lg"
            fullWidth
            onClick={() => navigate('/interview/setup')}
          >
            Practice Again
          </Button>
          <Button
            variant="secondary"
            size="lg"
            fullWidth
            onClick={() => navigate('/dashboard')}
          >
            Back to Dashboard
          </Button>
          <Button
            variant="secondary"
            size="lg"
            fullWidth
            onClick={() => {
              const shareData = {
                userName: user?.name,
                overallScore: review?.finalScore ?? session.score,
                topStrength: aggregatedStrengths?.[0] || '',
                recommendation: '',
                snapshot: review?.answers ? `Reviewed ${review.answers.length} questions` : '',
                title: `Interview Snapshot — ${session.topic}`,
              };
              navigate('/share-card', { state: { shareData } });
            }}
          >
            Share Result
          </Button>
          <Button
            variant="secondary"
            size="lg"
            fullWidth
            onClick={() => navigate('/history')}
          >
            View History
          </Button>
        </div>

        {/* ── Session Review & AI Feedback ── */}
        <div className="mt-8 grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2">
            {loading && (
              <div className="card p-6 text-center">
                <div className="w-8 h-8 border-2 border-brand-500 border-t-transparent rounded-full animate-spin mx-auto" />
                <p className="text-content-muted text-sm mt-2">Loading review...</p>
              </div>
            )}

            {error && (
              <div className="card p-4" style={{ borderColor: 'rgba(239,68,68,0.12)' }}>
                <p className="text-sm" style={{ color: 'var(--color-status-error)' }}>{error}</p>
              </div>
            )}

            {!loading && !error && (
              <div>
                <SessionReviewTable answers={answers} />
              </div>
            )}
          </div>

          <div className="space-y-4">
            {!loading && review && (
              <Card padding="md">
                <p className="text-sm font-semibold">Session Summary</p>
                <p className="text-xs text-content-muted mt-2">Overall score: {(review.finalScore ?? session.score)?.toFixed ? (review.finalScore ?? session.score).toFixed(1) + '%' : (review.finalScore ?? session.score)}</p>
                <p className="text-xs text-content-muted mt-1">Questions reviewed: {Array.isArray(review.answers) ? review.answers.length : '—'}</p>
              </Card>
            )}

            {!loading && (
              <Card padding="md">
                <p className="text-sm font-semibold mb-2">Top Strengths</p>
                <div className="flex flex-wrap gap-2">
                  {aggregatedStrengths.map((s,i) => <span key={i} className="badge badge-green text-xs">{s}</span>)}
                </div>
              </Card>
            )}

            {!loading && (
              <Card padding="md">
                <p className="text-sm font-semibold mb-2">Common Improvements</p>
                <ul className="text-sm list-disc pl-5" style={{ color: 'var(--color-content-muted)' }}>
                  {aggregatedImprovements.map((imp,i) => <li key={i}>{imp}</li>)}
                </ul>
              </Card>
            )}

            {/* model answer is per-question; leave detailed content in the table */}
          </div>
        </div>

      </PageContainer>
    </div>
  );
}