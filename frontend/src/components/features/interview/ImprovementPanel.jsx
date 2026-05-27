export function ImprovementPanel({ improvements = [], missingConcepts = [] }) {
  if (!improvements.length && !missingConcepts.length) return null;
  return (
    <div className="space-y-4">
      {improvements.length > 0 && (
        <div className="space-y-2">
          <p className="section-label">Areas to Improve</p>
          {improvements.map((item, i) => (
            <div key={i} className="flex items-start gap-2.5 p-3 rounded-lg"
              style={{ background: 'rgba(245,158,11,0.07)', border: '1px solid rgba(245,158,11,0.15)' }}>
              <span style={{ color: '#f59e0b', flexShrink: 0, fontSize: '14px', marginTop: '1px' }}>↑</span>
              <span className="text-sm" style={{ color: 'var(--color-content-secondary)', lineHeight: '1.6' }}>{item}</span>
            </div>
          ))}
        </div>
      )}
      {missingConcepts.length > 0 && (
        <div className="space-y-2">
          <p className="section-label">Missing Concepts</p>
          <div className="flex flex-wrap gap-2">
            {missingConcepts.map((c, i) => (
              <span key={i} className="badge badge-red">{c}</span>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
