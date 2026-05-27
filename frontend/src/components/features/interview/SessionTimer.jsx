// frontend/src/components/features/interview/SessionTimer.jsx
import { cn } from '../../../utils/cn';

// Displays elapsed time during an interview
// Props:
//   formattedTime: "12:34" string from useTimer
//   seconds: total elapsed seconds (for color coding)

export function SessionTimer({ formattedTime, seconds }) {
  // Turn amber after 10 mins, red after 20 mins
  const urgency = seconds >= 1200 ? 'red' : seconds >= 600 ? 'amber' : 'normal';

  const colors = {
    normal: 'text-content-secondary border-border-default',
    amber:  'text-amber-400 border-amber-500/30',
    red:    'text-red-400 border-red-500/30 animate-pulse',
  };

  return (
    <div className={cn(
      'flex items-center gap-2 px-3 py-1.5',
      'border rounded-lg font-mono text-sm font-medium',
      'bg-surface-3 transition-colors duration-500',
      colors[urgency]
    )}>
      {/* Clock icon using a simple SVG */}
      <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <circle cx="12" cy="12" r="10" strokeWidth="2"/>
        <polyline points="12 6 12 12 16 14" strokeWidth="2"/>
      </svg>
      <span>{formattedTime}</span>
    </div>
  );
}