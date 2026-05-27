// frontend/src/components/features/recruiter/RecruiterFeedbackPanel.jsx
import { Card } from '../../ui';
import { cn } from '../../../utils/cn';

/**
 * RecruiterFeedbackPanel — shows recruiter comments and reaction
 */
export function RecruiterFeedbackPanel({
  recruiterName,
  reaction,
  assessment,
  scores,
  className,
}) {
  return (
    <Card 
      padding="lg" 
      accent="purple"
      className={cn('space-y-4', className)}
    >
      <div className="flex items-center gap-3 mb-4">
        <span className="text-2xl">💼</span>
        <h3 className="text-lg font-semibold text-content">
          {recruiterName}'s Feedback
        </h3>
      </div>

      {reaction && (
        <div className="space-y-2">
          <h4 className="text-sm font-semibold text-content">Immediate Reaction</h4>
          <p className="text-sm text-content-muted italic">{reaction}</p>
        </div>
      )}

      {assessment && (
        <div className="space-y-2">
          <h4 className="text-sm font-semibold text-content">Assessment</h4>
          <p className="text-sm text-content-muted">{assessment}</p>
        </div>
      )}

      {scores && (
        <div className="grid grid-cols-2 gap-4 mt-4 pt-4 border-t border-border">
          {[
            { label: 'Technical', value: scores.technical },
            { label: 'Communication', value: scores.communication },
            { label: 'Problem Solving', value: scores.problemSolving },
            { label: 'Confidence', value: scores.confidence },
          ].map((item) => (
            item.value !== undefined && (
              <div key={item.label} className="space-y-2">
                <p className="text-xs font-medium text-content-muted">{item.label}</p>
                <div className="flex items-center gap-2">
                  <div className="flex-1 bg-gray-200 rounded-full h-2">
                    <div
                      className="bg-purple-500 h-2 rounded-full transition-all"
                      style={{ width: `${Math.min(item.value, 100)}%` }}
                    />
                  </div>
                  <span className="text-xs font-bold text-content w-8 text-right">
                    {Math.round(item.value)}
                  </span>
                </div>
              </div>
            )
          ))}
        </div>
      )}
    </Card>
  );
}
