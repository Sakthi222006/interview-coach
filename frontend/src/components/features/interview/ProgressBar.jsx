// frontend/src/components/features/interview/ProgressBar.jsx
import { cn } from '../../../utils/cn';

// Shows question X of Y with a visual progress bar
export function ProgressBar({ current, total, answeredCount }) {
  const percent = total > 0 ? Math.round((answeredCount / total) * 100) : 0;

  return (
    <div className="space-y-2">
      {/* Text label */}
      <div className="flex items-center justify-between">
        <span className="text-xs text-content-muted font-medium">
          Question <span className="text-content-primary">{current + 1}</span> of{' '}
          <span className="text-content-primary">{total}</span>
        </span>
        <span className="text-xs text-content-muted">
          {answeredCount} answered · {percent}% complete
        </span>
      </div>

      {/* Track */}
      <div className="h-1.5 bg-surface-4 rounded-full overflow-hidden">
        {/* Fill bar — animates width change */}
        <div
          className="h-full bg-gradient-to-r from-brand-600 to-brand-400 rounded-full transition-all duration-500 ease-out"
          style={{ width: `${percent}%` }}
        />
      </div>

      {/* Dot indicators for each question */}
      <div className="flex items-center gap-1 flex-wrap">
        {Array.from({ length: total }).map((_, i) => (
          <div
            key={i}
            className={cn(
              'w-2 h-2 rounded-full transition-all duration-300',
              i < answeredCount
                ? 'bg-brand-500'           // answered
                : i === current
                ? 'bg-content-secondary scale-125'  // current
                : 'bg-surface-5'           // upcoming
            )}
          />
        ))}
      </div>
    </div>
  );
}