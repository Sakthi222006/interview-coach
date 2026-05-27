import { Card, Badge } from '../../ui';

export function RoadmapTimeline({ title, phases = [] }) {
  return (
    <Card padding="lg" className="space-y-4">
      <div className="flex items-center justify-between gap-4">
        <div>
          <p className="text-sm font-semibold text-white">{title}</p>
          <p className="text-slate-400 text-xs mt-1">What to focus on in the coming days.</p>
        </div>
        <Badge color={phases.length > 0 ? 'blue' : 'gray'}>{phases.length} steps</Badge>
      </div>

      {phases.length === 0 ? (
        <p className="text-sm text-slate-400">No specific milestones available yet. Refresh your roadmap for the latest plan.</p>
      ) : (
        <div className="space-y-4">
          {phases.map((phase, index) => (
            <div key={phase.title || index} className="group rounded-3xl border border-slate-700/50 bg-slate-900/80 p-4 transition-all hover:border-slate-500">
              <div className="flex items-start justify-between gap-4">
                <div>
                  <p className="text-sm font-semibold text-white">{phase.title}</p>
                  <p className="text-xs text-slate-400 mt-1">{phase.description}</p>
                </div>
                <div className="text-right">
                  <p className="text-sm text-slate-300">{phase.durationDays ?? '—'} days</p>
                  <p className="text-xs text-slate-400 mt-1">{phase.difficulty || 'Medium'}</p>
                </div>
              </div>
              <div className="mt-4 flex flex-wrap gap-2">
                <Badge color={phase.priority <= 2 ? 'blue' : 'gray'}>{phase.priority ? `Priority ${phase.priority}` : 'Standard'}</Badge>
                <Badge color={phase.difficulty === 'HARD' ? 'red' : phase.difficulty === 'EASY' ? 'green' : 'blue'}>{phase.difficulty || 'Balanced'}</Badge>
              </div>
            </div>
          ))}
        </div>
      )}
    </Card>
  );
}
