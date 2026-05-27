import { useState } from 'react';
import { Badge } from '../../ui';
import { StarBreakdown } from '../interview/StarBreakdown';
import { cn } from '../../../utils/cn';

export function SessionReviewTable({ answers = [] }) {
  const [expanded, setExpanded] = useState(null);
  if (!answers.length) return null;

  return (
    <div className="space-y-2">
      <p className="section-label">Per-Question Breakdown</p>

      {answers.map((a) => {
        const isExpanded = expanded === a.questionNumber;
        const isMCQ = a.questionType === 'MCQ';
        const score = isMCQ ? (a.isCorrect ? 100 : 0) : (a.aiOverallScore || 0);
        const scoreColor = score >= 75 ? 'text-status-success' : score >= 50 ? 'text-brand-400' : 'text-status-error';

        return (
          <div key={a.questionNumber} className="card overflow-hidden">
            <button onClick={() => setExpanded(isExpanded ? null : a.questionNumber)} className="w-full text-left p-4 flex items-center gap-4 hover:bg-surface-4 transition-colors">
              <span className="w-7 h-7 rounded-lg flex items-center justify-center text-xs font-bold flex-shrink-0" style={{ background: 'var(--color-surface-5)', color: 'var(--color-content-muted)' }}>{a.questionNumber}</span>

              <div className="flex-1 min-w-0">
                <p className="text-sm text-content-primary truncate">{a.questionText}</p>
                <div className="flex items-center gap-2 mt-1">
                  <Badge color={a.questionType === 'MCQ' ? 'blue' : 'purple'}>{a.questionType}</Badge>
                  <Badge color={a.difficulty === 'EASY' ? 'green' : a.difficulty === 'MEDIUM' ? 'amber' : 'red'}>{a.difficulty}</Badge>
                  {a.timeSpentSeconds && <span className="text-xs" style={{ color: 'var(--color-content-disabled)' }}>{a.timeSpentSeconds}s</span>}
                </div>
              </div>

              <div className="flex items-center gap-3 flex-shrink-0">
                <span className={cn('text-sm font-bold', scoreColor)}>{isMCQ ? (a.isCorrect ? '✓' : '✗') : `${score}%`}</span>
                <span className="text-xs" style={{ color: 'var(--color-content-disabled)' }}>{isExpanded ? '▼' : '▶'}</span>
              </div>
            </button>

            {isExpanded && (
              <div className="px-4 pb-4 border-t space-y-4 animate-fade-in" style={{ borderColor: 'var(--color-border-default)' }}>
                <div className="pt-3">
                  <p className="section-label mb-1">Your Answer</p>
                  <p className="text-sm" style={{ color: 'var(--color-content-secondary)' }}>{a.userAnswer || '(no answer provided)'}</p>
                </div>

                {isMCQ && a.explanation && (
                  <div>
                    <p className="section-label mb-1">Explanation</p>
                    <p className="text-sm" style={{ color: 'var(--color-content-secondary)' }}>{a.explanation}</p>
                  </div>
                )}

                {!isMCQ && a.interviewerFeedback && (
                  <div className="p-3 rounded-lg" style={{ background: 'rgba(59,130,246,0.07)', border: '1px solid rgba(59,130,246,0.15)' }}>
                    <p className="text-xs italic" style={{ color: 'var(--color-content-secondary)' }}>&quot;{a.interviewerFeedback}&quot;</p>
                  </div>
                )}

                {!isMCQ && a.strengths?.length > 0 && (
                  <div className="flex flex-wrap gap-2">{a.strengths.map((s,i) => <span key={i} className="badge badge-green text-xs">{s}</span>)}</div>
                )}

                {a.starTotalScore != null && (
                  <StarBreakdown starData={a} />
                )}
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
}
