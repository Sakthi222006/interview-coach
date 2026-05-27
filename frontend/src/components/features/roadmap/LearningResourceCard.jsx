import { Card } from '../../ui';

export function LearningResourceCard({ resources = [] }) {
  return (
    <Card padding="lg" className="space-y-4">
      <div>
        <p className="text-sm font-semibold text-white">Learning resources</p>
        <p className="text-slate-400 text-xs mt-1">Curated guidance for the topics in your roadmap.</p>
      </div>

      {resources.length === 0 ? (
        <p className="text-sm text-slate-400">No resources available yet. Generate your roadmap to unlock recommendations.</p>
      ) : (
        <div className="space-y-3">
          {resources.map((resource) => (
            <a key={resource.title} href={resource.link} target="_blank" rel="noreferrer" className="block rounded-3xl border border-slate-700/50 bg-slate-900/80 p-4 transition hover:border-blue-500/50">
              <p className="text-sm font-semibold text-white">{resource.title}</p>
              <p className="text-xs text-slate-400 mt-1">{resource.category}</p>
            </a>
          ))}
        </div>
      )}
    </Card>
  );
}
