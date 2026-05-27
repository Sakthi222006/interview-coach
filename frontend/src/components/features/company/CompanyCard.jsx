import { Card, Button } from '../../ui';

export function CompanyCard({ company, onPractice, onReadiness, onMockInterview }) {
  return (
    <Card className="border-slate-700 hover:border-slate-500 transition-colors" padding="lg">
      <div className="flex flex-col h-full gap-4">
        <div>
          <p className="text-sm uppercase tracking-[0.24em] text-slate-400">{company.difficulty}</p>
          <h3 className="text-xl font-semibold text-white mt-2">{company.companyName}</h3>
          <p className="text-slate-400 text-sm mt-2">{company.description}</p>
        </div>

        <div className="grid grid-cols-2 gap-3 text-sm text-slate-300">
          <div>
            <p className="text-slate-500">Aptitude</p>
            <p className="mt-1 font-semibold">{company.aptitudeWeightage}%</p>
          </div>
          <div>
            <p className="text-slate-500">Coding</p>
            <p className="mt-1 font-semibold">{company.codingWeightage}%</p>
          </div>
          <div>
            <p className="text-slate-500">Communication</p>
            <p className="mt-1 font-semibold">{company.communicationWeightage}%</p>
          </div>
          <div>
            <p className="text-slate-500">Technical</p>
            <p className="mt-1 font-semibold">{company.technicalWeightage}%</p>
          </div>
        </div>

        <div className="flex flex-wrap gap-2">
          {company.focusTechnologies?.slice(0, 4).map((tech) => (
            <span key={tech} className="px-2 py-1 rounded-full bg-slate-800 text-slate-300 text-xs">{tech}</span>
          ))}
        </div>

        <div className="mt-auto grid grid-cols-1 gap-2 sm:grid-cols-3">
          <Button size="sm" variant="secondary" onClick={onReadiness}>Readiness</Button>
          <Button size="sm" variant="primary" onClick={onPractice}>Practice</Button>
          <Button size="sm" variant="ghost" onClick={onMockInterview}>Interview</Button>
        </div>
      </div>
    </Card>
  );
}
