import { useMemo } from 'react';
import { Link } from 'react-router-dom';
import { Button, Card, Badge } from '../components/ui';
import { useRoadmap } from '../hooks/useRoadmap';
import { RoadmapOverviewCard } from '../components/features/roadmap/RoadmapOverviewCard';
import { RoadmapTimeline } from '../components/features/roadmap/RoadmapTimeline';
import { RoadmapTaskCard } from '../components/features/roadmap/RoadmapTaskCard';
import { SkillGapCard } from '../components/features/roadmap/SkillGapCard';
import { LearningResourceCard } from '../components/features/roadmap/LearningResourceCard';
import { RoadmapProgressChart } from '../components/features/analytics/RoadmapProgressChart';
import { LearningProgressChart } from '../components/features/analytics/LearningProgressChart';

export default function RoadmapPage() {
  const { roadmap, loading, error, refresh, generating, generateRoadmap } = useRoadmap();

  const weekPlan = useMemo(() => {
    return roadmap?.phases?.filter((phase) => phase.durationDays != null && phase.durationDays <= 7) || [];
  }, [roadmap]);

  const monthPlan = useMemo(() => {
    return roadmap?.phases?.filter((phase) => phase.durationDays != null && phase.durationDays > 7 && phase.durationDays <= 30) || [];
  }, [roadmap]);

  const quarterPlan = useMemo(() => {
    return roadmap?.phases?.filter((phase) => phase.durationDays != null && phase.durationDays > 30 && phase.durationDays <= 90) || [];
  }, [roadmap]);

  const resourceList = useMemo(() => {
    if (!roadmap) return [];
    return roadmap.items?.slice(0, 3).map((item) => ({
      title: item.title,
      link: `https://www.google.com/search?q=${encodeURIComponent(item.title + ' learning resources')}`,
      category: item.type || 'Practice',
    })) || [];
  }, [roadmap]);

  return (
    <div className="min-h-screen bg-slate-950">
      <div className="max-w-7xl mx-auto px-6 py-8">
        <div className="flex flex-col gap-6 lg:flex-row lg:items-end lg:justify-between mb-8">
          <div>
            <p className="text-sm text-slate-400 mb-2">Career roadmap</p>
            <h1 className="text-3xl font-bold text-white">Your Learning Plan</h1>
            <p className="text-slate-500 max-w-2xl mt-2">Track a tailored 7/30/90 day learning pathway based on your latest performance analytics.</p>
          </div>

          <div className="flex flex-wrap gap-3">
            <Link to="/dashboard">
              <Button variant="secondary">Back to Dashboard</Button>
            </Link>
            <Button variant="primary" loading={generating} onClick={generateRoadmap}>Refresh Roadmap</Button>
          </div>
        </div>

        {loading ? (
          <div className="grid grid-cols-1 gap-4">
            {[1, 2, 3, 4].map((i) => (
              <div key={i} className="card p-6 animate-pulse"><div className="h-6 mb-4 bg-slate-800 rounded"/><div className="h-40 bg-slate-800 rounded"/></div>
            ))}
          </div>
        ) : error ? (
          <Card padding="lg" className="border border-rose-500/20">
            <div className="flex items-center justify-between gap-4">
              <div>
                <p className="text-sm font-semibold text-rose-300">Roadmap failed to load</p>
                <p className="text-xs text-slate-400 mt-1">{error}</p>
              </div>
              <Button variant="ghost" onClick={refresh}>Retry</Button>
            </div>
          </Card>
        ) : !roadmap ? (
          <Card padding="lg" className="text-center">
            <p className="text-slate-300 mb-4">Generate your first roadmap to see personalized skill gaps and a plan.</p>
            <Button variant="primary" onClick={generateRoadmap}>Generate Roadmap</Button>
          </Card>
        ) : (
          <div className="space-y-8">
            <div className="grid grid-cols-1 xl:grid-cols-[1.2fr_0.8fr] gap-4">
              <RoadmapOverviewCard roadmap={roadmap} />
              <Card padding="lg" className="space-y-6">
                <div className="flex items-center justify-between gap-4">
                  <div>
                    <p className="text-sm font-semibold text-white">Plan progress</p>
                    <p className="text-xs text-slate-400">Roadmap readiness and learning focus</p>
                  </div>
                  <Badge color="blue">{roadmap.overallReadiness || 'Ready'}</Badge>
                </div>
                <RoadmapProgressChart score={roadmap.readinessScore} />
                <LearningProgressChart completed={roadmap.items?.length ? 2 : 0} total={roadmap.items?.length || 1} />
              </Card>
            </div>

            <div className="grid grid-cols-1 xl:grid-cols-[0.9fr_0.7fr] gap-4">
              <SkillGapCard skillGaps={roadmap.items?.slice(0, 4).map((item) => ({ skill: item.title, gap: item.metric || 'Focus area', note: item.description })) || []} />
              <LearningResourceCard resources={resourceList} />
            </div>

            <div className="space-y-4">
              <RoadmapTimeline title="7 Day Plan" phases={weekPlan} />
              <RoadmapTimeline title="30 Day Plan" phases={monthPlan} />
              <RoadmapTimeline title="90 Day Plan" phases={quarterPlan} />
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
              {(roadmap.phases || []).slice(0, 3).map((phase, index) => (
                <RoadmapTaskCard key={`${phase.title}-${index}`} task={{
                  title: phase.title,
                  description: phase.description,
                  priority: phase.priority,
                  durationDays: phase.durationDays,
                  difficulty: phase.difficulty,
                  estimatedHours: phase.estimatedHours,
                }} />
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
