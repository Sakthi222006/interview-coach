import { Card } from '../../ui';

export function ReadinessGauge({ score = 0, overall = 'LOW' }) {
  const percent = Math.max(0, Math.min(100, score));
  const color = overall === 'HIGH' ? 'text-emerald-400' : overall === 'MEDIUM' ? 'text-amber-300' : 'text-red-400';

  return (
    <Card className="p-5 bg-slate-900 border border-slate-700">
      <div className="flex items-center justify-between gap-4">
        <div>
          <p className="text-sm text-slate-400 uppercase tracking-[0.2em]">Resume Readiness</p>
          <p className={`text-4xl font-bold mt-2 ${color}`}>{overall}</p>
          <p className="text-slate-500 text-sm mt-1">Based on resume strength and role fit</p>
        </div>
        <div className="relative w-28 h-28">
          <svg viewBox="0 0 120 120" className="w-full h-full">
            <circle cx="60" cy="60" r="48" fill="none" stroke="#1e293b" strokeWidth="14" />
            <circle
              cx="60"
              cy="60"
              r="48"
              fill="none"
              stroke="url(#readinessGradient)"
              strokeWidth="14"
              strokeLinecap="round"
              strokeDasharray={`${Math.round(302 * percent / 100)} 302`}
              transform="rotate(-90 60 60)"
            />
            <defs>
              <linearGradient id="readinessGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" stopColor="#22c55e" />
                <stop offset="100%" stopColor="#14b8ff" />
              </linearGradient>
            </defs>
          </svg>
          <div className="absolute inset-0 grid place-items-center text-white text-xl font-semibold">
            {percent}%
          </div>
        </div>
      </div>
    </Card>
  );
}
