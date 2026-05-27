import React from 'react';

export function RoadmapProgressChart({ score = 0 }) {
  const safeScore = Math.max(0, Math.min(100, score));
  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between text-xs text-slate-400">
        <span>Roadmap readiness</span>
        <span>{safeScore}%</span>
      </div>
      <div className="h-3 rounded-full bg-slate-800 overflow-hidden">
        <div className="h-full rounded-full bg-gradient-to-r from-blue-500 to-cyan-400" style={{ width: `${safeScore}%` }} />
      </div>
      <div className="text-xs text-slate-500">Higher values mean your plan matches your current performance profile.</div>
    </div>
  );
}
