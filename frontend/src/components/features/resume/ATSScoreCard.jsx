import { Card } from '../../ui';

export function ATSScoreCard({ atsScore = 0, message = 'ATS friendly score' }) {
  const status = atsScore >= 80 ? 'Excellent' : atsScore >= 60 ? 'Good' : 'Needs improvement';
  return (
    <Card className="p-5 bg-slate-900 border border-slate-700">
      <div className="flex items-center justify-between gap-4">
        <div>
          <p className="text-sm text-slate-400 uppercase tracking-[0.2em]">ATS Score</p>
          <p className="text-4xl font-bold text-white mt-2">{atsScore}</p>
          <p className="text-sm text-slate-500 mt-1">{message}</p>
        </div>
        <div className="rounded-full px-4 py-2 text-xs font-semibold text-white" style={{ backgroundColor: atsScore >= 80 ? '#16a34a' : atsScore >= 60 ? '#eab308' : '#dc2626' }}>
          {status}
        </div>
      </div>
    </Card>
  );
}
