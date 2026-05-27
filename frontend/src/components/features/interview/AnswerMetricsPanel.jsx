export function AnswerMetricsPanel({ answer = '', minWords = 40, maxWords = 180 }) {
  const cleaned = answer.trim();
  const words = cleaned ? cleaned.split(/\s+/).filter(Boolean).length : 0;
  const chars = cleaned.length;
  const wordPercent = Math.min(100, Math.round((words / maxWords) * 100));
  const isTooShort = words > 0 && words < minWords;

  return (
    <div className="rounded-3xl bg-slate-950 border border-slate-800 p-4 space-y-4 animate-fade-in">
      <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-slate-400 text-xs uppercase tracking-[0.28em]">Answer Metrics</p>
          <h3 className="text-base font-semibold text-white">Live response insights</h3>
        </div>
        <div className="flex flex-wrap gap-3 text-sm text-slate-300">
          <span>{words} words</span>
          <span>{chars} characters</span>
        </div>
      </div>

      <div className="space-y-2">
        <div className="flex items-center justify-between text-sm text-slate-400">
          <span>Recommended length</span>
          <span>{minWords}-{maxWords} words</span>
        </div>
        <div className="h-2 rounded-full bg-slate-800 overflow-hidden">
          <div
            className="h-full rounded-full bg-gradient-to-r from-brand-500 to-cyan-400 transition-all duration-500"
            style={{ width: `${wordPercent}%` }}
          />
        </div>
      </div>

      {isTooShort && (
        <div className="rounded-2xl bg-amber-950/40 border border-amber-700/30 p-3 text-sm text-amber-200">
          Your answer is very short. Add more detail and examples so the AI can evaluate your reasoning clearly.
        </div>
      )}

      <div className="grid gap-3 sm:grid-cols-2">
        <MetricTile label="Word count" value={words} />
        <MetricTile label="Character count" value={chars} />
        <MetricTile label="Length goal" value={`${minWords}-${maxWords} words`} />
        <MetricTile label="Quality pulse" value={`${wordPercent}%`} />
      </div>
    </div>
  );
}

function MetricTile({ label, value }) {
  return (
    <div className="rounded-2xl border border-slate-800 bg-slate-900 p-3">
      <p className="text-xs uppercase tracking-[0.22em] text-slate-500">{label}</p>
      <p className="mt-2 text-lg font-semibold text-white">{value}</p>
    </div>
  );
}
