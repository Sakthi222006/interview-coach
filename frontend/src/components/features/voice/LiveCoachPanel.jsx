import { Card } from '../../ui';

export function LiveCoachPanel({ feedback, improvements, nextQuestion }) {
  return (
    <Card className="bg-slate-900 border border-slate-700 p-5">
      <p className="text-sm text-slate-400 uppercase tracking-[0.2em] mb-4">Live Coach</p>
      {feedback ? (
        <>
          <p className="text-white font-semibold">AI Feedback</p>
          <p className="text-slate-300 mt-3 leading-7">{feedback}</p>
          <div className="mt-4 space-y-3">
            <div>
              <p className="text-sm text-slate-400">Improvements</p>
              <ul className="list-disc list-inside text-slate-300 text-sm mt-2 space-y-1">
                {improvements.map((item, idx) => (
                  <li key={idx}>{item}</li>
                ))}
              </ul>
            </div>
            {nextQuestion && (
              <div className="rounded-2xl bg-slate-950 p-4 border border-slate-700">
                <p className="text-sm text-slate-400">Follow-up question</p>
                <p className="text-white mt-2">{nextQuestion}</p>
              </div>
            )}
          </div>
        </>
      ) : (
        <p className="text-slate-500">Complete a spoken answer to receive live coaching feedback.</p>
      )}
    </Card>
  );
}
