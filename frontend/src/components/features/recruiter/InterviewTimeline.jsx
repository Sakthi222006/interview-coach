// frontend/src/components/features/recruiter/InterviewTimeline.jsx
import { cn } from '../../../utils/cn';
import { Card } from '../../ui';

/**
 * InterviewTimeline — displays conversation flow and turn-by-turn progression
 */
export function InterviewTimeline({ 
  conversations = [],
  currentTurn,
  className 
}) {
  return (
    <div className={cn('space-y-4', className)}>
      <h3 className="text-lg font-semibold text-content">Interview Timeline</h3>
      
      {conversations.length === 0 ? (
        <p className="text-sm text-content-muted">No conversation yet. Interview will start shortly.</p>
      ) : (
        <div className="space-y-3">
          {conversations.map((conv, idx) => (
            <Card
              key={conv.id}
              padding="md"
              className={cn(
                'border-l-4 transition-all',
                conv.speaker === 'RECRUITER' ? 'border-l-blue-500 bg-blue-50' : 'border-l-green-500 bg-green-50'
              )}
            >
              <div className="flex items-start gap-3">
                <div className={cn(
                  'w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold text-white flex-shrink-0',
                  conv.speaker === 'RECRUITER' ? 'bg-blue-500' : 'bg-green-500'
                )}>
                  {conv.turnNumber}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-xs font-semibold text-content-muted mb-1">
                    {conv.speaker === 'RECRUITER' ? '📋 Recruiter' : '👤 You'}
                  </p>
                  <p className="text-sm text-content leading-relaxed break-words">
                    {conv.message}
                  </p>
                  
                  {conv.speaker === 'RECRUITER' && conv.recruiterReaction && (
                    <p className="text-xs text-blue-700 mt-2 italic">
                      Reaction: {conv.recruiterReaction}
                    </p>
                  )}
                  
                  {conv.speaker === 'CANDIDATE' && conv.aiComment && (
                    <div className="text-xs text-green-700 mt-2 p-2 bg-green-100 rounded">
                      💬 {conv.aiComment}
                    </div>
                  )}
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
