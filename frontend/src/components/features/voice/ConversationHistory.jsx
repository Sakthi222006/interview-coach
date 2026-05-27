import { Card } from '../../ui';

export function ConversationHistory({ history = [] }) {
  return (
    <Card className="bg-slate-900 border border-slate-700 p-5">
      <p className="text-sm text-slate-400 uppercase tracking-[0.2em] mb-4">Conversation History</p>
      {history.length ? (
        <div className="space-y-3">
          {history.map((item, idx) => (
            <div key={idx} className="rounded-2xl border border-slate-800 bg-slate-950 p-4">
              <p className="text-slate-400 text-xs">Answer {idx + 1}</p>
              <p className="text-slate-200 mt-2 whitespace-pre-wrap">{item.transcript}</p>
              {item.nextQuestion && <p className="text-slate-400 text-sm mt-3">Next question: {item.nextQuestion}</p>}
            </div>
          ))}
        </div>
      ) : (
        <p className="text-slate-500">No voice answers submitted yet.</p>
      )}
    </Card>
  );
}
