export function StrengthsPanel({ strengths = [] }) {
  if (!strengths.length) return null;
  return (
    <div className="space-y-2">
      <p className="section-label">Strengths</p>
      {strengths.map((s, i) => (
        <div key={i} className="flex items-start gap-2.5 p-3 rounded-lg"
          style={{ background: 'rgba(16,185,129,0.07)', border: '1px solid rgba(16,185,129,0.15)' }}>
          <span style={{ color: 'var(--color-status-success)', flexShrink: 0, fontSize: '14px', marginTop: '1px' }}>✓</span>
          <span className="text-sm" style={{ color: 'var(--color-content-secondary)', lineHeight: '1.6' }}>{s}</span>
        </div>
      ))}
    </div>
  );
}
