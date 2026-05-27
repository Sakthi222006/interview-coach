export function StarBreakdown({ starData }) {
  if (!starData || starData.starTotalScore == null) return null;

  const bars = [
    { label: 'Situation', score: starData.starSituationScore, max: 25 },
    { label: 'Task', score: starData.starTaskScore, max: 25 },
    { label: 'Action', score: starData.starActionScore, max: 25 },
    { label: 'Result', score: starData.starResultScore, max: 25 },
  ];

  const total = starData.starTotalScore || 0;
  const color = total >= 75 ? '#10b981' : total >= 50 ? '#3b82f6' : '#f59e0b';

  return (
    <div className="p-4 rounded-xl space-y-3" style={{ background: 'rgba(139,92,246,0.07)', border: '1px solid rgba(139,92,246,0.2)' }}>
      <div className="flex items-center justify-between">
        <p className="text-xs font-semibold uppercase tracking-wide" style={{ color: '#a78bfa' }}>STAR Method Score</p>
        <span className="text-sm font-bold" style={{ color }}>{total}/100</span>
      </div>

      <div className="space-y-2">
        {bars.map(bar => (
          <div key={bar.label}>
            <div className="flex justify-between text-xs mb-1" style={{ color: 'var(--color-content-muted)' }}>
              <span>{bar.label}</span>
              <span>{bar.score || 0}/{bar.max}</span>
            </div>
            <div className="h-1.5 rounded-full overflow-hidden" style={{ background: 'var(--color-surface-5)' }}>
              <div className="h-full rounded-full transition-all duration-700" style={{ width: `${((bar.score || 0) / bar.max) * 100}%`, background: '#8b5cf6' }} />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
