import { Card } from '../../ui';

export function SkillGapCard({ missingCount = 0, details = [] }) {
  return (
    <Card className="p-5 bg-slate-900 border border-slate-700">
      <p className="text-sm text-slate-400 uppercase tracking-[0.2em]">Missing Skills</p>
      <p className="text-4xl font-bold text-white mt-3">{missingCount}</p>
      <p className="text-slate-500 text-sm mt-2">Skills identified as gaps from your resume or role context.</p>

      {details.length ? (
        <ul className="mt-4 space-y-2 text-slate-300 text-sm">
          {details.slice(0, 5).map((skill, idx) => (
            <li key={idx} className="rounded-xl bg-slate-950 p-3">• {skill}</li>
          ))}
        </ul>
      ) : (
        <p className="text-slate-500 text-sm mt-4">Complete a resume match or readiness check to reveal missing skills.</p>
      )}
    </Card>
  );
}
