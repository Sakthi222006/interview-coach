export function EvaluationScoreCard({ score, label, size = 'md' }) {
  const color =
    score >= 80 ? '#10b981' :
    score >= 60 ? '#3b82f6' :
    score >= 40 ? '#f59e0b' : '#ef4444';

  const cfg = {
    sm: { d: 44, sw: 4, fs: '13px' },
    md: { d: 64, sw: 5, fs: '17px' },
    lg: { d: 88, sw: 6, fs: '23px' },
  }[size];

  const r   = (cfg.d - cfg.sw * 2) / 2;
  const c   = 2 * Math.PI * r;
  const off = c - (score / 100) * c;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '6px' }}>
      <svg width={cfg.d} height={cfg.d} viewBox={`0 0 ${cfg.d} ${cfg.d}`}>
        <circle
          cx={cfg.d / 2} cy={cfg.d / 2} r={r}
          fill="none" stroke="rgba(255,255,255,0.06)" strokeWidth={cfg.sw}
        />
        <circle
          cx={cfg.d / 2} cy={cfg.d / 2} r={r}
          fill="none" stroke={color} strokeWidth={cfg.sw}
          strokeDasharray={c} strokeDashoffset={off}
          strokeLinecap="round"
          transform={`rotate(-90 ${cfg.d / 2} ${cfg.d / 2})`}
          style={{ transition: 'stroke-dashoffset 1s ease-out' }}
        />
        <text
          x="50%" y="50%" textAnchor="middle" dominantBaseline="middle"
          fill={color} fontSize={cfg.fs} fontWeight="700"
          fontFamily="Inter, sans-serif"
        >
          {score}
        </text>
      </svg>
      <span style={{ fontSize: '11px', color: 'var(--color-content-muted)', textAlign: 'center' }}>
        {label}
      </span>
    </div>
  );
}
