import React from 'react';

export function LearningProgressChart({ completed = 0, total = 1 }) {
  const safeTotal = Math.max(1, total);
  const safeCompleted = Math.max(0, Math.min(safeTotal, completed));
  const ratio = Math.round((safeCompleted / safeTotal) * 100);

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between text-xs text-slate-400">
        <span>Learning progress</span>
        <span>{ratio}% complete</span>
      </div>
      <div className="h-3 rounded-full bg-slate-800 overflow-hidden">
        <div className="h-full rounded-full bg-gradient-to-r from-emerald-500 to-sky-500" style={{ width: `${ratio}%` }} />
      </div>
      <p className="text-xs text-slate-500">{safeCompleted} of {safeTotal} roadmap items ready to review.</p>
    </div>
  );
}
