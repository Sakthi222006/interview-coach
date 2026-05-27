import { Card } from '../../ui';

export function ConfidenceMeter({ score = 0 }) {
  return (
    <Card className="bg-slate-900 border border-slate-700 p-5">
      <p className="text-sm text-slate-400 uppercase tracking-[0.2em]">Confidence</p>
      <div className="mt-4 flex items-end gap-3">
        <span className="text-5xl font-bold text-white">{score}%</span>
        <span className="text-slate-400">Live confidence rating</span>
      </div>
      <div className="mt-4 h-3 rounded-full bg-slate-800 overflow-hidden">
        <div className="h-full bg-emerald-500" style={{ width: `${Math.min(100, Math.max(0, score))}%` }} />
      </div>
    </Card>
  );
}
