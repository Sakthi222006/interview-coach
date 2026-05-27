// frontend/src/pages/InterviewSetupPage.jsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createSession } from '../services/sessionService';
import { useInterviewSession } from '../hooks/useInterviewSession';
import { Card, Button, Badge, Alert } from '../components/ui';
import { PageContainer } from '../components/layout';
import { useEffect } from 'react';
import { getAdaptiveRec } from '../services/analyticsService';

// Topic configuration — icon, description, available counts
const TOPICS = [
  {
    id: 'DSA',
    label: 'Data Structures & Algorithms',
    icon: '🧩',
    desc: 'Arrays, trees, graphs, sorting, searching',
    color: 'blue',
  },
  {
    id: 'JAVA',
    label: 'Java Programming',
    icon: '☕',
    desc: 'OOP, collections, concurrency, Java 8+',
    color: 'purple',
  },
  {
    id: 'SQL',
    label: 'SQL & Databases',
    icon: '🗄️',
    desc: 'Joins, aggregations, indexes, transactions',
    color: 'amber',
  },
  {
    id: 'REACT',
    label: 'React & Frontend',
    icon: '⚛️',
    desc: 'Hooks, state, routing, performance',
    color: 'blue',
  },
  {
    id: 'HR',
    label: 'HR & Behavioural',
    icon: '🤝',
    desc: 'STAR method, situational questions',
    color: 'gray',
  },
];

const DIFFICULTIES = [
  { id: 'EASY',   label: 'Easy',   desc: 'Foundational concepts',   color: 'badge-green'  },
  { id: 'MEDIUM', label: 'Medium', desc: 'Practical application',   color: 'badge-amber'  },
  { id: 'HARD',   label: 'Hard',   desc: 'Advanced problem-solving', color: 'badge-red'    },
];

const QUESTION_COUNTS = [5, 8, 10, 15];

export default function InterviewSetupPage() {
  const [selectedTopic,      setSelectedTopic]      = useState(null);
  const [selectedDifficulty, setSelectedDifficulty] = useState('MEDIUM');
  const [questionCount,      setQuestionCount]      = useState(10);
  const [loading,            setLoading]            = useState(false);
  const [error,              setError]              = useState('');

  const { startSession } = useInterviewSession();
  const navigate         = useNavigate();

  const handleStart = async () => {
    if (!selectedTopic) {
      setError('Please select a topic to continue.');
      return;
    }
    setError('');
    setLoading(true);

    const result = await createSession(selectedTopic, selectedDifficulty, questionCount);

    if (result.success) {
      // Navigate to interview page, passing session data via state
      navigate('/interview', {
        state: {
          session:   result.data,
          questions: result.data.questions,
        },
      });
    } else {
      setError(result.message || 'Failed to start session. Please try again.');
      setLoading(false);
    }
  };

  useEffect(() => {
    let mounted = true;
    const load = async () => {
      if (!selectedTopic) return;
      const res = await getAdaptiveRec(selectedTopic);
      if (!mounted) return;
      if (res.success && res.data) {
        // expected shape: { recommendedDifficulty, explanation, readinessScore }
        const rec = res.data;
        if (rec.recommendedDifficulty) setSelectedDifficulty(rec.recommendedDifficulty);
      }
    };
    load();
    return () => { mounted = false; };
  }, [selectedTopic]);

  return (
    <div className="min-h-screen" style={{ background: 'var(--color-surface-1)' }}>
      <PageContainer size="md">

        {/* ── Header ── */}
        <div className="mb-8">
          <button
            onClick={() => navigate('/dashboard')}
            className="text-sm text-content-muted hover:text-content-primary mb-4 flex items-center gap-1 transition-colors"
          >
            ← Back to Dashboard
          </button>
          <h1 className="text-2xl font-bold text-content-primary">Set Up Your Interview</h1>
          <p className="text-content-muted text-sm mt-1">
            Choose a topic and difficulty level to begin your practice session
          </p>
        </div>

        {error && (
          <div className="mb-6">
            <Alert type="error" message={error} onClose={() => setError('')} />
          </div>
        )}

        {/* ── Topic Selection ── */}
        <section className="mb-8">
          <p className="section-label mb-4">Select Topic</p>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            {TOPICS.map(topic => (
              <button
                key={topic.id}
                onClick={() => { setSelectedTopic(topic.id); setError(''); }}
                className={`
                  text-left p-4 rounded-xl border transition-all duration-200
                  focus:outline-none focus-visible:ring-2 focus-visible:ring-brand-500
                  ${selectedTopic === topic.id
                    ? 'border-brand-500 bg-brand-500/10 shadow-sm'
                    : 'border-border-default bg-surface-3 hover:border-border-strong hover:bg-surface-4'
                  }
                `}
              >
                <div className="flex items-start gap-3">
                  <span className="text-2xl flex-shrink-0">{topic.icon}</span>
                  <div className="min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="text-sm font-semibold text-content-primary">
                        {topic.label}
                      </span>
                      {selectedTopic === topic.id && (
                        <span className="text-brand-400 text-xs">✓ Selected</span>
                      )}
                    </div>
                    <p className="text-xs text-content-muted leading-relaxed">{topic.desc}</p>
                  </div>
                </div>
              </button>
            ))}
          </div>
        </section>

        {/* ── Difficulty Selection ── */}
        <section className="mb-8">
          <p className="section-label mb-4">Difficulty Level</p>
          <div className="grid grid-cols-3 gap-3">
            {DIFFICULTIES.map(diff => (
              <button
                key={diff.id}
                onClick={() => setSelectedDifficulty(diff.id)}
                className={`
                  p-4 rounded-xl border text-center transition-all duration-200
                  focus:outline-none focus-visible:ring-2 focus-visible:ring-brand-500
                  ${selectedDifficulty === diff.id
                    ? 'border-brand-500 bg-brand-500/10'
                    : 'border-border-default bg-surface-3 hover:border-border-strong'
                  }
                `}
              >
                <span className={`badge ${diff.color} mb-2`}>{diff.label}</span>
                <p className="text-xs text-content-muted mt-1">{diff.desc}</p>
              </button>
            ))}
          </div>
        </section>

        {/* ── Question Count ── */}
        <section className="mb-8">
          <p className="section-label mb-4">Number of Questions</p>
          <div className="flex gap-3">
            {QUESTION_COUNTS.map(count => (
              <button
                key={count}
                onClick={() => setQuestionCount(count)}
                className={`
                  w-14 h-14 rounded-xl border font-bold text-sm transition-all duration-200
                  focus:outline-none focus-visible:ring-2 focus-visible:ring-brand-500
                  ${questionCount === count
                    ? 'border-brand-500 bg-brand-500/10 text-brand-400'
                    : 'border-border-default bg-surface-3 text-content-secondary hover:border-border-strong'
                  }
                `}
              >
                {count}
              </button>
            ))}
          </div>
        </section>

        {/* ── Summary + Start ── */}
        <Card padding="md" className="flex items-center justify-between gap-4 flex-wrap">
          <div className="flex items-center gap-3 flex-wrap">
            {selectedTopic ? (
              <>
                <Badge color="blue">{selectedTopic}</Badge>
                <span className={`badge ${DIFFICULTIES.find(d => d.id === selectedDifficulty)?.color}`}>
                  {selectedDifficulty}
                </span>
                <Badge color="gray">{questionCount} questions</Badge>
              </>
            ) : (
              <p className="text-content-muted text-sm">Select a topic above to begin</p>
            )}
          </div>
          <Button
            variant="primary"
            size="lg"
            loading={loading}
            disabled={!selectedTopic || loading}
            onClick={handleStart}
          >
            {loading ? 'Starting...' : 'Start Interview →'}
          </Button>
        </Card>

      </PageContainer>
    </div>
  );
}