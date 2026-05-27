import { Card } from '../../ui';

export function SkillGapCard({ skillGaps = [] }) {
  return (
    <Card padding="lg" className="space-y-4">
      <div>
        <p className="text-sm font-semibold text-white">Skill gap summary</p>
        <p className="text-slate-400 text-xs mt-1">See the concepts to close first for faster progress.</p>
      </div>

      {skillGaps.length === 0 ? (
        <p className="text-sm text-slate-400">No gaps detected yet. Generate your roadmap or complete an interview for insight.</p>
      ) : (
        <div className="space-y-3">
          {skillGaps.map((gap, index) => (
            <div key={`${gap.skill}-${index}`} className="rounded-3xl border border-slate-700/60 bg-slate-900/70 p-4">
              <p className="text-sm font-semibold text-white">{gap.skill}</p>
              <p className="text-xs text-slate-400 mt-1">{gap.gap}</p>
              <p className="text-xs text-slate-500 mt-2">{gap.note}</p>
            </div>
          ))}
        </div>
      )}
    </Card>
  );
}
