// frontend/src/pages/RecruiterModePage.jsx
import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useRecruiterMode } from '../hooks/useRecruiterMode';
import { useTimer } from '../hooks/useTimer';
import {
  RecruiterAvatar,
  FollowUpQuestionCard,
  InterviewTimeline,
  HiringDecisionCard,
  RecruiterFeedbackPanel,
  InterviewSummaryCard,
} from '../components/features/recruiter';
import { Card, Button, InputField, Alert, LoadingSpinner } from '../components/ui';
import { PageContainer } from '../components/layout';
import { cn } from '../utils/cn';

export default function RecruiterModePage() {
  const navigate = useNavigate();
  const location = useLocation();
  const recruiterType = location.state?.recruiterType;

  const {
    scenario,
    conversations,
    currentQuestion,
    isLoading,
    isSubmitting,
    error,
    sessionState,
    summary,
    followUps,
    loadScenario,
    submitAnswer,
  } = useRecruiterMode();

  const [scenarioId, setScenarioId] = useState(location.state?.scenarioId);
  const [candidateAnswer, setCandidateAnswer] = useState('');
  const [confidenceLevel, setConfidenceLevel] = useState(5);
  const [expandedConversation, setExpandedConversation] = useState(false);

  const { seconds, formattedTime } = useTimer({ autoStart: sessionState === 'IN_PROGRESS' });

  // Load scenario on mount or when ID changes
  useEffect(() => {
    if (scenarioId && sessionState === 'IDLE') {
      loadScenario(scenarioId);
    }
  }, [scenarioId, sessionState, loadScenario]);

  // Redirect to setup if no recruiter type
  useEffect(() => {
    if (!recruiterType) {
      navigate('/recruiter/setup');
    }
  }, [recruiterType, navigate]);

  const handleSubmitAnswer = async () => {
    if (!candidateAnswer.trim()) {
      alert('Please provide an answer');
      return;
    }

    await submitAnswer(scenarioId, candidateAnswer, confidenceLevel);
    setCandidateAnswer('');
    setConfidenceLevel(5);
  };

  if (isLoading && !scenario) {
    return (
      <PageContainer>
        <div className="flex items-center justify-center min-h-screen">
          <div className="text-center space-y-4">
            <LoadingSpinner />
            <p className="text-content-muted">Loading interview...</p>
          </div>
        </div>
      </PageContainer>
    );
  }

  if (!scenario) {
    return (
      <PageContainer>
        <Alert type="error" message={error || 'Failed to load interview'} />
        <Button onClick={() => navigate('/recruiter/setup')}>Back to Setup</Button>
      </PageContainer>
    );
  }

  const isCompleted = sessionState === 'COMPLETED';
  const isActive = sessionState === 'IN_PROGRESS';

  return (
    <PageContainer>
      <div className="max-w-6xl mx-auto py-6 space-y-6">
        {/* Top Bar */}
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-content">{scenario.title}</h1>
            <p className="text-sm text-content-muted mt-1">
              {scenario.roundType.replace(/_/g, ' ')}
            </p>
          </div>
          <div className="text-right">
            <p className="text-3xl font-bold text-brand-500">{formattedTime}</p>
            <p className="text-xs text-content-muted">Elapsed time</p>
          </div>
        </div>

        {error && <Alert type="error" message={error} />}

        {/* Main Content Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Left: Recruiter Avatar & Conversation */}
          <div className="lg:col-span-2 space-y-6">
            {/* Recruiter Card */}
            <Card padding="lg" accent="blue">
              <RecruiterAvatar
                recruiterName={scenario.recruiterName}
                recruiterType={scenario.recruiterType}
                className="mb-4"
              />
              {/* Current Status */}
              <div className="mt-4 pt-4 border-t border-border">
                <p className="text-sm text-content-muted mb-2">Round Progress</p>
                <div className="flex items-center gap-2">
                  <div className="flex-1 bg-gray-200 rounded-full h-2">
                    <div
                      className="bg-brand-500 h-2 rounded-full transition-all"
                      style={{
                        width: `${(scenario.currentRoundIndex / scenario.totalRounds) * 100}%`,
                      }}
                    />
                  </div>
                  <span className="text-sm font-semibold text-content">
                    {scenario.currentRoundIndex} / {scenario.totalRounds}
                  </span>
                </div>
              </div>
            </Card>

            {/* Conversation Timeline */}
            <div>
              <div
                className="flex items-center justify-between cursor-pointer"
                onClick={() => setExpandedConversation(!expandedConversation)}
              >
                <h3 className="text-lg font-semibold text-content">Interview Flow</h3>
                <span className="text-2xl">{expandedConversation ? '▼' : '▶'}</span>
              </div>
              {expandedConversation && (
                <Card padding="lg" className="mt-4">
                  <InterviewTimeline
                    conversations={conversations}
                    currentTurn={conversations.length + 1}
                  />
                </Card>
              )}
            </div>

            {/* Current Question & Answer Input */}
            {isActive && currentQuestion && (
              <Card padding="lg" accent="purple">
                <h3 className="text-lg font-semibold text-content mb-4">Question</h3>
                <p className="text-content mb-6 text-lg leading-relaxed">{currentQuestion}</p>

                <div className="space-y-4">
                  {/* Answer Input */}
                  <div>
                    <label className="block text-sm font-medium text-content mb-2">
                      Your Answer
                    </label>
                    <textarea
                      value={candidateAnswer}
                      onChange={(e) => setCandidateAnswer(e.target.value)}
                      placeholder="Type your answer here..."
                      rows={5}
                      className="w-full px-4 py-3 rounded-lg border border-border bg-surface text-content focus:outline-none focus:ring-2 focus:ring-brand-500"
                      disabled={isSubmitting}
                    />
                  </div>

                  {/* Confidence Level */}
                  <div>
                    <label className="block text-sm font-medium text-content mb-2">
                      Confidence Level: {confidenceLevel}/10
                    </label>
                    <input
                      type="range"
                      min="1"
                      max="10"
                      value={confidenceLevel}
                      onChange={(e) => setConfidenceLevel(parseInt(e.target.value))}
                      className="w-full"
                      disabled={isSubmitting}
                    />
                  </div>

                  {/* Submit Button */}
                  <Button
                    variant="primary"
                    size="lg"
                    onClick={handleSubmitAnswer}
                    disabled={isSubmitting || !candidateAnswer.trim()}
                    className="w-full"
                  >
                    {isSubmitting ? 'Evaluating...' : 'Submit Answer'}
                  </Button>
                </div>
              </Card>
            )}

            {/* Follow-up Questions */}
            {isActive && followUps.length > 0 && (
              <div className="space-y-3">
                <h3 className="text-lg font-semibold text-content">Follow-up Questions</h3>
                {followUps.map((followUp) => (
                  <FollowUpQuestionCard
                    key={followUp.id}
                    question={followUp.question}
                    followUpType={followUp.followUpType}
                    triggerReason={followUp.triggerReason}
                    difficultyLevel={followUp.difficultyLevel}
                    coachingHint={followUp.coachingHint}
                    isAnswered={followUp.isAnswered}
                  />
                ))}
              </div>
            )}
          </div>

          {/* Right: Scores & Summary */}
          <div className="lg:col-span-1 space-y-6">
            {/* Live Scores */}
            {isActive && (
              <Card padding="lg" accent="green">
                <h3 className="text-lg font-semibold text-content mb-4">Live Scores</h3>
                <div className="space-y-3">
                  {[
                    { label: 'Technical', value: scenario.technicalScore },
                    { label: 'Communication', value: scenario.communicationScore },
                    { label: 'Problem Solving', value: scenario.problemSolvingScore },
                    { label: 'Confidence', value: scenario.confidenceScore },
                  ].map((item) => (
                    <div key={item.label} className="space-y-1">
                      <p className="text-xs font-medium text-content-muted">{item.label}</p>
                      <div className="flex items-center gap-2">
                        <div className="flex-1 bg-gray-200 rounded-full h-2">
                          <div
                            className="bg-green-500 h-2 rounded-full transition-all"
                            style={{ width: `${Math.min(item.value, 100)}%` }}
                          />
                        </div>
                        <span className="text-xs font-bold text-content w-8 text-right">
                          {Math.round(item.value)}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              </Card>
            )}

            {/* Summary (Completed) */}
            {isCompleted && summary && (
              <Card padding="lg" accent="purple">
                <InterviewSummaryCard
                  recruiterName={scenario.recruiterName}
                  roundType={scenario.roundType}
                  duration={scenario.durationSeconds}
                  totalTurns={conversations.length}
                  scores={{
                    technical: scenario.technicalScore,
                    communication: scenario.communicationScore,
                    problemSolving: scenario.problemSolvingScore,
                    leadership: scenario.leadershipScore,
                    overall: scenario.overallScore,
                  }}
                  keyStrengths={summary.keyStrengths}
                  areasForImprovement={summary.areasForImprovement}
                  onRetry={() => navigate('/recruiter/setup')}
                />
              </Card>
            )}

            {/* Hiring Decision (Completed) */}
            {isCompleted && scenario && (
              <HiringDecisionCard
                recommendation={scenario.hireRecommendation}
                comments={scenario.hiringComments}
                scores={{
                  Technical: scenario.technicalScore,
                  Communication: scenario.communicationScore,
                  Leadership: scenario.leadershipScore,
                  Overall: scenario.overallScore,
                }}
              />
            )}

            {/* Recruiter Feedback (Completed) */}
            {isCompleted && conversations.length > 0 && (
              <RecruiterFeedbackPanel
                recruiterName={scenario.recruiterName}
                reaction={conversations[conversations.length - 1]?.recruiterReaction}
                assessment={conversations[conversations.length - 1]?.answerAssessment}
                scores={{
                  technical: scenario.technicalScore,
                  communication: scenario.communicationScore,
                  problemSolving: scenario.problemSolvingScore,
                  confidence: scenario.confidenceScore,
                }}
              />
            )}
          </div>
        </div>

        {/* Footer Actions */}
        {isCompleted && (
          <div className="flex gap-3 pt-6 border-t border-border">
            <Button
              variant="secondary"
              onClick={() => navigate('/recruiter/setup')}
              className="flex-1"
            >
              🔄 Try Another Round
            </Button>
            <Button
              variant="primary"
              onClick={() => navigate('/dashboard')}
              className="flex-1"
            >
              📊 View Analytics
            </Button>
          </div>
        )}
      </div>
    </PageContainer>
  );
}
