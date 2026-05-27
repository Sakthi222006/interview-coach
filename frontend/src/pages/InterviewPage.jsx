// frontend/src/pages/InterviewPage.jsx
import { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useInterviewSession } from '../hooks/useInterviewSession';
import { useTimer }            from '../hooks/useTimer';
import { SessionTimer }        from '../components/features/interview/SessionTimer';
import { ProgressBar }         from '../components/features/interview/ProgressBar';
import { QuestionCard }        from '../components/features/interview/QuestionCard';
import { InterviewProgressSummary } from '../components/features/interview/InterviewProgressSummary';
import { Button, Alert }       from '../components/ui';
import { cn }                  from '../utils/cn';

export default function InterviewPage() {
  const location = useLocation();
  const navigate = useNavigate();

  // Get session data passed via navigate() from setup page
  const passedSession   = location.state?.session;
  const passedQuestions = location.state?.questions;

  const {
    session, questions, currentIndex, selectedOption,
    sessionState, submitting, aiLoading, error, finalResult,
    currentQuestion, isLastQuestion, currentAnswer,
    answers, answeredCount, correctCount,
    accuracyPercent, averageResponseTime, xpEarned,
    startSession, selectOption, submitCurrentAnswer, nextQuestion, finishSession,
  } = useInterviewSession();

  // include answers for passing to feedback


  // Global session timer — starts on mount
  const { seconds, formattedTime } = useTimer({ autoStart: true });

  // Initialize session from navigation state
  useEffect(() => {
    if (passedSession && passedQuestions) {
      startSession(passedSession, passedQuestions);
    } else {
      // No session data — redirect to setup
      navigate('/interview/setup');
    }
  }, []);

  // After session completes, navigate to feedback page
  useEffect(() => {
    if (sessionState === 'COMPLETED' && finalResult) {
      navigate('/feedback', {
        state: {
          session:    finalResult,
          answers:    Object.values(answers),
          totalTime:  seconds,
        },
      });
    }
  }, [sessionState, finalResult, answers]);

  // Handle finish — called on last question after submitting
  const handleFinish = async () => {
    await finishSession(seconds);
  };

  // Guard: session not loaded yet
  if (sessionState === 'IDLE' || !currentQuestion) {
    return (
      <div className="min-h-screen flex items-center justify-center"
           style={{ background: 'var(--color-surface-1)' }}>
        <div className="text-center space-y-3">
          <div className="w-8 h-8 border-2 border-brand-500 border-t-transparent rounded-full animate-spin mx-auto" />
          <p className="text-content-muted text-sm">Loading interview...</p>
        </div>
      </div>
    );
  }

  const isReviewing = sessionState === 'REVIEWING';
  const isActive    = sessionState === 'ACTIVE';

  console.log('InterviewPage render -> answers =', answers);
  console.log('InterviewPage render -> currentAnswer =', currentAnswer);

  return (
    <div className="min-h-screen" style={{ background: 'var(--color-surface-1)' }}>
      <div className="max-w-3xl mx-auto px-4 py-6 space-y-5">

        {/* ── Top bar: timer + progress ── */}
        <div className="flex items-start justify-between gap-4 flex-col xl:flex-row">
          <div className="flex-1">
            <ProgressBar
              current={currentIndex}
              total={questions.length}
              answeredCount={answeredCount}
            />
          </div>
          <div className="mt-4 xl:mt-0">
            <SessionTimer formattedTime={formattedTime} seconds={seconds} />
          </div>
        </div>

        <InterviewProgressSummary
          questionsCompleted={answeredCount}
          totalQuestions={questions.length}
          accuracyPercent={accuracyPercent}
          averageResponseTime={averageResponseTime}
          xpEarned={xpEarned}
        />

        {/* ── Error alert ── */}
        {error && (
          <Alert type="error" message={error} />
        )}

        {/* ── Question card ── */}
        <QuestionCard
          question={currentQuestion}
          questionNumber={currentIndex + 1}
          selectedOption={selectedOption}
          onSelect={selectOption}
          answerResult={currentAnswer}
          isReviewing={isReviewing}
          isActive={isActive}
          aiLoading={aiLoading}
        />

        {/* ── Action bar ── */}
        <div className="flex items-center justify-between gap-4 pt-2">

          {/* Left: score preview during session */}
          <div className="text-sm text-content-muted">
            {answeredCount > 0 && (
              <span>
                Score so far:{' '}
                <span className="text-content-primary font-semibold">
                  {correctCount}/{answeredCount}
                </span>
                {' '}correct
              </span>
            )}
          </div>

          {/* Right: action buttons */}
          <div className="flex gap-3">
            {/* Abandon session */}
            <Button
              variant="ghost"
              size="sm"
              onClick={() => navigate('/dashboard')}
              disabled={submitting}
            >
              Abandon
            </Button>

            {/* ACTIVE state: submit answer */}
            {isActive && (
              <Button
                variant="primary"
                size="md"
                loading={submitting}
                disabled={
                  submitting ||
                  (currentQuestion?.questionType === 'MCQ' && !selectedOption)
                }
                onClick={submitCurrentAnswer}
              >
                Submit Answer
              </Button>
            )}

            {/* REVIEWING state: next or finish */}
            {isReviewing && (
              <>
                {isLastQuestion ? (
                  <Button
                    variant="primary"
                    size="md"
                    loading={submitting}
                    onClick={handleFinish}
                  >
                    Finish Interview 🎉
                  </Button>
                ) : (
                  <Button
                    variant="primary"
                    size="md"
                    onClick={nextQuestion}
                  >
                    Next Question →
                  </Button>
                )}
              </>
            )}
          </div>
        </div>

        {/* ── Question nav dots (click to jump — disabled for answered) ── */}
        <div className="border-t border-border-subtle pt-4">
          <p className="section-label mb-3">Questions</p>
          <div className="flex flex-wrap gap-2">
            {questions.map((q, i) => {
              const isAnswered = answeredCount > i; // simplified
              const isCurrent  = i === currentIndex;
              return (
                <button
                  key={q.id}
                  disabled={true} // navigation disabled mid-session
                  className={cn(
                    'w-8 h-8 rounded-lg text-xs font-medium border transition-all',
                    isCurrent
                      ? 'border-brand-500 bg-brand-500/20 text-brand-400'
                      : isAnswered
                      ? 'border-status-success/40 bg-status-success/10 text-status-success cursor-default'
                      : 'border-border-default bg-surface-4 text-content-disabled cursor-default'
                  )}
                >
                  {i + 1}
                </button>
              );
            })}
          </div>
        </div>

      </div>
    </div>
  );
}