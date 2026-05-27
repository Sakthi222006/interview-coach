export function InterviewProgressSummary({ questionsCompleted, totalQuestions, accuracyPercent, averageResponseTime, xpEarned }) {
  return (
    <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
      <ProgressTile label="Questions Completed" value={`${questionsCompleted}/${totalQuestions}`} accent="blue" />
      <ProgressTile label="Current Score" value={`${accuracyPercent}%`} accent="green" />
      <ProgressTile label="Avg Response Time" value={`${averageResponseTime}s`} accent="amber" />
      <ProgressTile label="XP Earned" value={`${xpEarned}`} accent="purple" />
    </div>
  );
}

function ProgressTile({ label, value, accent }) {
  const accentStyles = {
    blue:  'bg-gradient-to-r from-sky-500/10 to-sky-400/5 border-sky-500/20 text-sky-200',
    green: 'bg-gradient-to-r from-emerald-500/10 to-emerald-400/5 border-emerald-500/20 text-emerald-200',
    amber: 'bg-gradient-to-r from-amber-500/10 to-amber-400/5 border-amber-500/20 text-amber-200',
    purple:'bg-gradient-to-r from-violet-500/10 to-violet-400/5 border-violet-500/20 text-violet-200',
  };

  return (
    <div className={`rounded-3xl border p-4 ${accentStyles[accent] ?? accentStyles.blue}`}>
      <p className="text-xs uppercase tracking-[0.28em] text-slate-400">{label}</p>
      <p className="mt-3 text-2xl font-semibold">{value}</p>
    </div>
  );
}
