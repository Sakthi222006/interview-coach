// frontend/src/pages/HistoryPage.jsx
import { useEffect, useState }         from 'react';
import { useNavigate }                 from 'react-router-dom';
import { getUserSessions }             from '../services/sessionService';
import { Card, Button, Badge }         from '../components/ui';
import { PageContainer }               from '../components/layout';
import { cn }                          from '../utils/cn';

export default function HistoryPage() {
  const [sessions, setSessions] = useState([]);
  const [loading,  setLoading]  = useState(true);
  const navigate                = useNavigate();

  useEffect(() => {
    getUserSessions().then(result => {
      if (result.success) setSessions(result.data || []);
      setLoading(false);
    });
  }, []);

  const formatDate = (dateStr) => {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('en-IN', {
      day: '2-digit', month: 'short', year: 'numeric',
    });
  };

  const formatDuration = (secs) => {
    if (!secs) return '—';
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return m > 0 ? `${m}m ${s}s` : `${s}s`;
  };

  const scoreColor = (score) =>
    score >= 80 ? 'text-status-success' :
    score >= 60 ? 'text-brand-400' :
    score >= 40 ? 'text-amber-400' : 'text-status-error';

  return (
    <div className="min-h-screen" style={{ background: 'var(--color-surface-1)' }}>
      <PageContainer>

        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-2xl font-bold text-content-primary">Interview History</h1>
            <p className="text-content-muted text-sm mt-1">
              {sessions.length} session{sessions.length !== 1 ? 's' : ''} recorded
            </p>
          </div>
          <Button variant="primary" onClick={() => navigate('/interview/setup')}>
            New Interview
          </Button>
        </div>

        {/* Loading */}
        {loading && (
          <div className="space-y-3">
            {[1,2,3].map(i => (
              <div key={i} className="card p-5 h-20 skeleton" />
            ))}
          </div>
        )}

        {/* Empty state */}
        {!loading && sessions.length === 0 && (
          <Card padding="xl" className="text-center py-16">
            <div className="text-5xl mb-4">📋</div>
            <p className="text-content-secondary font-medium mb-1">No interviews yet</p>
            <p className="text-content-muted text-sm mb-6">
              Complete your first session to see it here
            </p>
            <Button variant="primary" onClick={() => navigate('/interview/setup')}>
              Start First Interview
            </Button>
          </Card>
        )}

        {/* Sessions list */}
        {!loading && sessions.length > 0 && (
          <div className="space-y-3">
            {sessions.map((session) => (
              <Card
                key={session.id}
                interactive
                padding="md"
                className="flex items-center justify-between gap-4 flex-wrap"
                onClick={() => navigate(`/feedback`, { state: { session } })}
              >
                <div className="flex items-center gap-4 flex-wrap">
                  {/* Topic badge */}
                  <div className="w-10 h-10 rounded-xl bg-surface-4 flex items-center justify-center text-lg flex-shrink-0">
                    {session.topic === 'DSA'   ? '🧩' :
                     session.topic === 'JAVA'  ? '☕' :
                     session.topic === 'SQL'   ? '🗄️' :
                     session.topic === 'REACT' ? '⚛️' : '🤝'}
                  </div>

                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <span className="text-sm font-semibold text-content-primary">
                        {session.topic}
                      </span>
                      <span className={cn(
                        'badge',
                        session.difficulty === 'EASY'   ? 'badge-green' :
                        session.difficulty === 'MEDIUM' ? 'badge-amber' : 'badge-red'
                      )}>
                        {session.difficulty}
                      </span>
                      <span className={cn(
                        'badge',
                        session.status === 'COMPLETED' ? 'badge-green' :
                        session.status === 'IN_PROGRESS' ? 'badge-blue' : 'badge-gray'
                      )}>
                        {session.status}
                      </span>
                    </div>
                    <p className="text-xs text-content-muted">
                      {formatDate(session.startedAt)} · {formatDuration(session.durationSeconds)} ·{' '}
                      {session.answeredQuestions}/{session.totalQuestions} answered
                    </p>
                  </div>
                </div>

                {/* Score */}
                <div className="text-right flex-shrink-0">
                  {session.status === 'COMPLETED' ? (
                    <>
                      <p className={cn('text-xl font-bold', scoreColor(session.score))}>
                        {session.score?.toFixed(1)}%
                      </p>
                      <p className="text-xs text-content-disabled">score</p>
                    </>
                  ) : (
                    <Badge color="gray">Incomplete</Badge>
                  )}
                </div>
              </Card>
            ))}
          </div>
        )}

      </PageContainer>
    </div>
  );
}