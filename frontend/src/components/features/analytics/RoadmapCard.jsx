import { Card, Badge } from '../../ui';

export function RoadmapCard({ roadmap }) {
  if (!roadmap) return null;

  const readinessColor =
    roadmap.overallReadiness === 'Advanced' ? 'badge-green' :
    roadmap.overallReadiness === 'Intermediate' ? 'badge-blue' : 'badge-gray';

  return (
    <Card padding="lg" className="space-y-5">
      <div className="flex items-center justify-between">
        <h3 className="text-sm font-semibold" style={{ color: 'var(--color-content-primary)' }}>Your Improvement Roadmap</h3>
        <div className="flex items-center gap-2">
          <span className={`badge ${readinessColor}`}>{roadmap.overallReadiness}</span>
          <span className="text-xs" style={{ color: 'var(--color-content-muted)' }}>{roadmap.readinessScore}/100</span>
        </div>
      </div>

      <div className="space-y-3">
        {(roadmap.items || []).map(item => (
          <div key={item.priority} className="flex items-start gap-3 p-4 rounded-xl" style={{ background: item.priority === 1 ? 'rgba(59,130,246,0.08)' : 'var(--color-surface-4)', border: `1px solid ${item.priority === 1 ? 'rgba(59,130,246,0.2)' : 'var(--color-border-default)'}` }}>
            <div className="flex-shrink-0 w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold" style={{ background: item.priority === 1 ? 'var(--color-brand-600)' : 'var(--color-surface-5)', color: item.priority === 1 ? 'white' : 'var(--color-content-muted)' }}>{item.priority}</div>
            <div className="min-w-0">
              <div className="flex items-center gap-2 mb-1">
                <span>{item.icon}</span>
                <p className="text-sm font-semibold" style={{ color: 'var(--color-content-primary)' }}>{item.title}</p>
              </div>
              <p className="text-xs leading-relaxed mb-2" style={{ color: 'var(--color-content-muted)' }}>{item.description}</p>
              <span className="badge badge-gray text-xs">🎯 {item.metric}</span>
            </div>
          </div>
        ))}
      </div>
    </Card>
  );
}
