import { Card, Badge } from '../../ui';

export function RoadmapOverviewCard({ roadmap }) {
  if (!roadmap) return null;

  const nextPhase = roadmap.phases?.[0];
  const totalPhases = roadmap.phases?.length || 0;
  const totalTasks = roadmap.items?.length || 0;

  return (
    <Card padding="lg" className="space-y-6">
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-sm font-semibold text-white">Roadmap overview</p>
          <p className="text-slate-400 text-sm mt-1">A guided plan built from your performance analytics and current skill gaps.</p>
        </div>
        <Badge color={roadmap.readinessScore >= 70 ? 'green' : roadmap.readinessScore >= 40 ? 'blue' : 'amber'}>
          {roadmap.overallReadiness || 'Learning'}
        </Badge>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div className="rounded-2xl bg-slate-900/70 p-4">
          <p className="text-xs uppercase tracking-[0.2em] text-slate-400">Readiness</p>
          <p className="mt-3 text-3xl font-semibold text-white">{roadmap.readinessScore ?? 0}%</p>
        </div>
        <div className="rounded-2xl bg-slate-900/70 p-4">
          <p className="text-xs uppercase tracking-[0.2em] text-slate-400">Phases</p>
          <p className="mt-3 text-3xl font-semibold text-white">{totalPhases}</p>
        </div>
      </div>

      <div className="rounded-2xl bg-slate-900/70 p-4 text-sm leading-6 text-slate-300">
        <p className="font-semibold text-white">Next checkpoint</p>
        <p className="mt-2">{nextPhase?.title || 'Review your performance insights to continue.'}</p>
      </div>
    </Card>
  );
}
