// frontend/src/components/features/recruiter/InterviewSummaryCard.jsx
import { Card, Button } from '../../ui';
import { cn } from '../../../utils/cn';

/**
 * InterviewSummaryCard — comprehensive interview summary
 */
export function InterviewSummaryCard({
  recruiterName,
  roundType,
  duration,
  totalTurns,
  scores = {},
  keyStrengths,
  areasForImprovement,
  onShare,
  onRetry,
  className,
}) {
  const formatDuration = (seconds) => {
    if (!seconds) return '0m';
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}m ${secs}s`;
  };

  return (
    <Card padding="lg" className={cn('space-y-6', className)}>
      <div>
        <h2 className="text-2xl font-bold text-content">Interview Summary</h2>
        <p className="text-sm text-content-muted mt-1">
          {roundType.replace(/_/g, ' ')} with {recruiterName}
        </p>
      </div>

      {/* Session Stats */}
      <div className="grid grid-cols-3 gap-4">
        <div className="text-center">
          <p className="text-2xl font-bold text-brand-500">{totalTurns}</p>
          <p className="text-xs text-content-muted">Turns</p>
        </div>
        <div className="text-center">
          <p className="text-2xl font-bold text-brand-500">{formatDuration(duration)}</p>
          <p className="text-xs text-content-muted">Duration</p>
        </div>
        <div className="text-center">
          <p className="text-2xl font-bold text-brand-500">
            {scores.overall ? Math.round(scores.overall) : 0}%
          </p>
          <p className="text-xs text-content-muted">Overall Score</p>
        </div>
      </div>

      {/* Score Breakdown */}
      {Object.keys(scores).length > 0 && (
        <div className="space-y-3">
          <h3 className="text-sm font-semibold text-content">Score Breakdown</h3>
          {Object.entries(scores)
            .filter(([key]) => key !== 'overall')
            .map(([key, value]) => (
              <div key={key} className="space-y-1">
                <div className="flex items-center justify-between">
                  <p className="text-xs font-medium text-content-muted capitalize">
                    {key.replace(/([A-Z])/g, ' $1').trim()}
                  </p>
                  <span className="text-xs font-bold text-content">
                    {Math.round(value)}%
                  </span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div
                    className="bg-brand-500 h-2 rounded-full transition-all"
                    style={{ width: `${Math.min(value, 100)}%` }}
                  />
                </div>
              </div>
            ))}
        </div>
      )}

      {/* Key Insights */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {keyStrengths && (
          <div className="p-4 rounded-lg bg-green-50 border border-green-200">
            <h4 className="text-sm font-semibold text-green-900 mb-2">💪 Strengths</h4>
            <p className="text-xs text-green-800">{keyStrengths}</p>
          </div>
        )}
        {areasForImprovement && (
          <div className="p-4 rounded-lg bg-amber-50 border border-amber-200">
            <h4 className="text-sm font-semibold text-amber-900 mb-2">🎯 Areas to Improve</h4>
            <p className="text-xs text-amber-800">{areasForImprovement}</p>
          </div>
        )}
      </div>

      {/* Action Buttons */}
      <div className="flex gap-3 pt-4 border-t border-border">
        {onShare && (
          <Button 
            variant="secondary" 
            size="sm"
            onClick={onShare}
            className="flex-1"
          >
            📤 Share Results
          </Button>
        )}
        {onRetry && (
          <Button 
            variant="primary" 
            size="sm"
            onClick={onRetry}
            className="flex-1"
          >
            🔄 Try Again
          </Button>
        )}
      </div>
    </Card>
  );
}
