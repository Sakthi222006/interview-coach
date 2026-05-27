import { Card, Badge } from '../../ui';

export function RoadmapTaskCard({ task }) {
  if (!task) return null;

  return (
    <Card padding="lg" className="space-y-4">
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-sm font-semibold text-white">{task.title}</p>
          <p className="text-xs text-slate-400 mt-1">{task.description}</p>
        </div>
        <Badge color={task.difficulty === 'HARD' ? 'red' : task.difficulty === 'EASY' ? 'green' : 'blue'}>{task.difficulty || 'Medium'}</Badge>
      </div>
      <div className="grid grid-cols-2 gap-2 text-slate-400 text-xs">
        <div className="rounded-2xl bg-slate-900/80 p-3">Duration: {task.durationDays ?? 'N/A'} days</div>
        <div className="rounded-2xl bg-slate-900/80 p-3">Est. Hours: {task.estimatedHours ?? '—'}</div>
      </div>
      <Badge color={task.priority <= 2 ? 'blue' : 'gray'}>Priority {task.priority ?? '—'}</Badge>
    </Card>
  );
}
