// frontend/src/components/features/recruiter/HiringDecisionCard.jsx
import { Card } from '../../ui';
import { cn } from '../../../utils/cn';

/**
 * HiringDecisionCard — displays the final hiring recommendation
 */
export function HiringDecisionCard({
  recommendation,
  comments,
  scores = {},
  className,
}) {
  const getRecommendationColor = (rec) => {
    switch (rec) {
      case 'HIRE': return { 
        bg: 'bg-green-100 border-green-300', 
        text: 'text-green-800',
        icon: '✓'
      };
      case 'NO_HIRE': return { 
        bg: 'bg-red-100 border-red-300', 
        text: 'text-red-800',
        icon: '✗'
      };
      case 'ON_HOLD': return { 
        bg: 'bg-amber-100 border-amber-300', 
        text: 'text-amber-800',
        icon: '⏸'
      };
      default: return { 
        bg: 'bg-gray-100 border-gray-300', 
        text: 'text-gray-800',
        icon: '?'
      };
    }
  };

  const recStyle = getRecommendationColor(recommendation);

  return (
    <Card padding="lg" className={cn('space-y-4', className)}>
      <div className={cn('p-4 rounded-lg border', recStyle.bg)}>
        <div className="flex items-center gap-3">
          <span className={cn('text-3xl font-bold', recStyle.text)}>
            {recStyle.icon}
          </span>
          <div>
            <p className={cn('text-sm font-medium', recStyle.text)}>
              Hiring Decision
            </p>
            <p className={cn('text-2xl font-bold', recStyle.text)}>
              {recommendation.replace(/_/g, ' ')}
            </p>
          </div>
        </div>
      </div>

      {comments && (
        <div className="space-y-2">
          <h4 className="text-sm font-semibold text-content">Feedback</h4>
          <p className="text-sm text-content-muted">{comments}</p>
        </div>
      )}

      {Object.keys(scores).length > 0 && (
        <div className="grid grid-cols-2 gap-3">
          {Object.entries(scores).map(([key, value]) => (
            <div key={key} className="space-y-1">
              <p className="text-xs font-medium text-content-muted">
                {key.replace(/([A-Z])/g, ' $1').trim()}
              </p>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div
                  className="bg-brand-500 h-2 rounded-full transition-all"
                  style={{ width: `${Math.min(value, 100)}%` }}
                />
              </div>
              <p className="text-xs font-semibold text-content">{Math.round(value)}%</p>
            </div>
          ))}
        </div>
      )}
    </Card>
  );
}
