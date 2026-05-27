// frontend/src/components/features/recruiter/FollowUpQuestionCard.jsx
import { Card, Button } from '../../ui';
import { cn } from '../../../utils/cn';

/**
 * FollowUpQuestionCard — displays follow-up questions with context
 */
export function FollowUpQuestionCard({
  question,
  followUpType,
  triggerReason,
  difficultyLevel,
  coachingHint,
  isAnswered,
  onSelect,
  className,
}) {
  const getDifficultyColor = (level) => {
    switch (level) {
      case 'EASY': return 'text-green-600';
      case 'MEDIUM': return 'text-amber-600';
      case 'HARD': return 'text-red-600';
      default: return 'text-gray-600';
    }
  };

  const getFollowUpBadge = (type) => {
    switch (type) {
      case 'COACHING': return { label: '💡 Coaching', color: 'bg-blue-100 text-blue-700' };
      case 'CHALLENGE': return { label: '⭐ Challenge', color: 'bg-purple-100 text-purple-700' };
      case 'DEEPER_DIVE': return { label: '🔍 Deeper Dive', color: 'bg-indigo-100 text-indigo-700' };
      default: return { label: 'Follow-up', color: 'bg-gray-100 text-gray-700' };
    }
  };

  const badge = getFollowUpBadge(followUpType);

  return (
    <Card 
      padding="md" 
      accent="blue" 
      className={cn('space-y-3', className)}
      interactive={!isAnswered}
    >
      <div className="flex items-start justify-between gap-4">
        <div className="flex-1 space-y-2">
          <div className="flex items-center gap-2">
            <span className={cn('px-2 py-1 text-xs font-medium rounded', badge.color)}>
              {badge.label}
            </span>
            <span className={cn('text-xs font-medium', getDifficultyColor(difficultyLevel))}>
              {difficultyLevel}
            </span>
          </div>
          <p className="text-content font-medium">{question}</p>
        </div>
        {!isAnswered && onSelect && (
          <Button 
            size="sm" 
            onClick={onSelect}
            variant="primary"
          >
            Answer
          </Button>
        )}
      </div>

      {coachingHint && (
        <div className="mt-3 p-3 bg-blue-50 rounded-lg border border-blue-200">
          <p className="text-xs text-blue-900">
            💡 <strong>Tip:</strong> {coachingHint}
          </p>
        </div>
      )}

      {isAnswered && (
        <div className="text-xs text-green-600 font-medium">✓ Answered</div>
      )}
    </Card>
  );
}
