import { Card } from '../../ui';

export function ResumeScoreCard({ score = 0, label = 'Resume Score', subtitle = 'Quality rating' }) {
  return (
    <Card className="p-5 bg-slate-900 border border-slate-700">
      <div className="flex items-center justify-between gap-4">
        <div>
          <p className="text-sm text-slate-400 uppercase tracking-[0.2em]">{label}</p>
          <p className="text-4xl font-bold text-white mt-2">{score}</p>
          <p className="text-sm text-slate-500 mt-1">{subtitle}</p>
        </div>
        <div className="h-20 w-20 rounded-full bg-slate-800 grid place-items-center text-3xl font-semibold text-cyan-400">
          {score}
        </div>
      </div>
    </Card>
  );
}
