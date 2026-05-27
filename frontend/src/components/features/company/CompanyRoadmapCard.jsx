import { Card } from '../../ui';

export function CompanyRoadmapCard({ company }) {
  return (
    <Card className="bg-slate-900 border-slate-700" padding="lg">
      <div className="space-y-4">
        <div>
          <p className="text-sm uppercase tracking-[0.24em] text-slate-400">Preparation roadmap</p>
          <h3 className="text-xl font-semibold text-white">{company.companyName}</h3>
        </div>
        <div className="grid gap-3">
          <div className="rounded-2xl bg-slate-950 p-4">
            <p className="text-slate-200 text-sm">Primary focus</p>
            <p className="mt-2 text-white font-semibold">{company.focusTechnologies?.slice(0, 3).join(', ') || 'General interview readiness'}</p>
          </div>
          <div className="grid gap-2">
            <div className="rounded-2xl bg-slate-950 p-4">
              <p className="text-slate-400 text-xs uppercase tracking-[0.2em]">Aptitude</p>
              <p className="text-white text-lg font-semibold">{company.aptitudeWeightage}%</p>
            </div>
            <div className="rounded-2xl bg-slate-950 p-4">
              <p className="text-slate-400 text-xs uppercase tracking-[0.2em]">Coding</p>
              <p className="text-white text-lg font-semibold">{company.codingWeightage}%</p>
            </div>
            <div className="rounded-2xl bg-slate-950 p-4">
              <p className="text-slate-400 text-xs uppercase tracking-[0.2em]">Communication</p>
              <p className="text-white text-lg font-semibold">{company.communicationWeightage}%</p>
            </div>
          </div>
        </div>
      </div>
    </Card>
  );
}
