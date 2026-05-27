import { EvaluationScoreCard } from './EvaluationScoreCard';
import { StrengthsPanel }      from './StrengthsPanel';
import { ImprovementPanel }    from './ImprovementPanel';
import { ModelAnswerPanel }    from './ModelAnswerPanel';
import { StarBreakdown }       from './StarBreakdown';

export function AIFeedbackCard({ evaluation, loading = false }) {
  if (loading) {
    return (
      <div className="card p-6 space-y-4 animate-fade-in">
        <div className="flex items-center gap-2">
          <div className="w-2 h-2 rounded-full animate-pulse" style={{ background: 'var(--color-brand-500)' }} />
          <span className="text-sm font-medium" style={{ color: 'var(--color-brand-400)' }}>
            AI is evaluating your answer...
          </span>
        </div>
        <div className="skeleton h-4 rounded w-3/4" />
        <div className="skeleton h-4 rounded w-1/2" />
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4,1fr)', gap: '16px' }}>
          {[1,2,3,4].map(i => <div key={i} className="skeleton rounded-xl" style={{ height: '80px' }} />)}
        </div>
        <div className="skeleton rounded-lg" style={{ height: '80px' }} />
      </div>
    );
  }

  if (!evaluation) return null;

  if (!evaluation.success) {
    const errorText = evaluation.errorMessage ||
      'AI feedback is temporarily unavailable. Please try again later.';

    return (
      <div className="card p-5" style={{ borderColor: 'rgba(239,68,68,0.2)' }}>
        <div className="flex items-center gap-2" style={{ color: 'var(--color-status-error)' }}>
          <span>⚠</span>
          <span className="text-sm font-medium">AI evaluation unavailable</span>
        </div>
        <p className="text-xs mt-1" style={{ color: 'var(--color-content-muted)' }}>
          {errorText}
        </p>
      </div>
    );
  }

  const badgeColor =
    evaluation.overallScore >= 80 ? 'badge-green' :
    evaluation.overallScore >= 60 ? 'badge-blue'  :
    evaluation.overallScore >= 40 ? 'badge-amber' : 'badge-red';

  return (
    <div className="card p-6 space-y-6 animate-fade-in">
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{ fontSize: '18px' }}>🤖</span>
          <span className="text-sm font-semibold" style={{ color: 'var(--color-content-primary)' }}>
            AI Evaluation
          </span>
        </div>
        <span className={`badge ${badgeColor}`} style={{ fontSize: '14px', fontWeight: 700 }}>
          {evaluation.overallScore}/100
        </span>
      </div>

      <div>
        <p className="section-label mb-4">Score Breakdown</p>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, minmax(0,1fr))', gap: '16px' }}>
          <EvaluationScoreCard score={evaluation.overallScore} label="Overall" />
          <EvaluationScoreCard score={evaluation.technicalAccuracy || evaluation.technicalScore || 0} label="Technical" />
          <EvaluationScoreCard score={evaluation.communication || evaluation.communicationScore || 0} label="Communication" />
          <EvaluationScoreCard score={evaluation.confidence || evaluation.confidenceScore || 0} label="Confidence" />
        </div>
      </div>

      <StrengthsPanel strengths={evaluation.strengths} />

      <ImprovementPanel
        improvements={evaluation.improvements}
        missingConcepts={evaluation.missingConcepts}
      />

      <ModelAnswerPanel
        modelAnswer={evaluation.modelAnswer}
        interviewerFeedback={evaluation.interviewerFeedback}
      />

      {/* STAR breakdown for HR/behavioral answers */}
      {(evaluation.questionTopic === 'HR' || evaluation.topic === 'HR' || evaluation.questionType === 'HR') && evaluation.starTotalScore != null && (
        <StarBreakdown starData={evaluation} />
      )}
    </div>
  );
}
