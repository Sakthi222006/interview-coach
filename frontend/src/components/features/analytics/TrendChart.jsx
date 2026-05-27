import React from 'react';

export function TrendChart({ dataPoints = [], width = 700, height = 220 }) {
  if (!dataPoints || dataPoints.length === 0) {
    return (
      <div className="card p-6">
        <p className="text-sm" style={{ color: 'var(--color-content-muted)' }}>Complete sessions to see your trend.</p>
      </div>
    );
  }

  const PAD = { top: 20, right: 20, bottom: 40, left: 40 };
  const W = width - PAD.left - PAD.right;
  const H = height - PAD.top - PAD.bottom;
  const scores = dataPoints.map(p => p.score || 0);
  const minY = Math.max(0, Math.min(...scores) - 10);
  const maxY = Math.min(100, Math.max(...scores) + 10);
  const scaleX = i => (i / Math.max(dataPoints.length - 1, 1)) * W;
  const scaleY = v => H - ((v - minY) / Math.max((maxY - minY), 1)) * H;

  const linePath = scores.map((s, i) => `${i === 0 ? 'M' : 'L'} ${scaleX(i)} ${scaleY(s)}`).join(' ');
  const areaPath = [
    ...scores.map((s, i) => `${i === 0 ? 'M' : 'L'} ${scaleX(i)} ${scaleY(s)}`),
    `L ${scaleX(scores.length - 1)} ${H}`,
    `L 0 ${H}`,
    'Z'
  ].join(' ');

  const yTicks = [0, 25, 50, 75, 100].filter(t => t >= minY && t <= maxY);

  return (
    <svg viewBox={`0 0 ${width} ${height}`} style={{ width: '100%', height: 'auto' }}>
      <defs>
        <linearGradient id="areaGrad" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#3b82f6" stopOpacity="0.25" />
          <stop offset="100%" stopColor="#3b82f6" stopOpacity="0.02" />
        </linearGradient>
      </defs>

      <g transform={`translate(${PAD.left},${PAD.top})`}>
        {yTicks.map(t => (
          <g key={t}>
            <line x1={0} y1={scaleY(t)} x2={W} y2={scaleY(t)} stroke="rgba(255,255,255,0.05)" strokeWidth={1} />
            <text x={-8} y={scaleY(t)} textAnchor="end" dominantBaseline="middle" fill="rgba(255,255,255,0.3)" fontSize={10}>{t}</text>
          </g>
        ))}

        <path d={areaPath} fill="url(#areaGrad)" />
        <path d={linePath} fill="none" stroke="#3b82f6" strokeWidth={2} strokeLinecap="round" strokeLinejoin="round" />

        {scores.map((s, i) => (
          <circle key={i} cx={scaleX(i)} cy={scaleY(s)} r={4} fill="#3b82f6" stroke="var(--color-surface-2)" strokeWidth={2}>
            <title>{dataPoints[i].topic} — {s.toFixed(1)}%</title>
          </circle>
        ))}

        {dataPoints.map((p, i) => (
          (dataPoints.length <= 7 || i % Math.ceil(dataPoints.length / 7) === 0) && (
            <text key={i} x={scaleX(i)} y={H + 20} textAnchor="middle" fill="rgba(255,255,255,0.3)" fontSize={9}>{p.topic?.substring(0,4)}</text>
          )
        ))}
      </g>
    </svg>
  );
}
