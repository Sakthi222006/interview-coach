import { Card, Button } from '../../ui';

export function AptitudeQuestionCard({ question, selectedAnswer, onSelect, showSolution }) {
  return (
    <Card className="bg-slate-900 border-slate-700" padding="lg">
      <div className="space-y-4">
        <div>
          <div className="flex items-center justify-between gap-3">
            <p className="text-slate-400 text-sm">{question.topic || 'General Aptitude'}</p>
            <span className="text-xs text-slate-500 uppercase tracking-[0.2em]">{question.difficulty}</span>
          </div>
          <h4 className="text-lg font-semibold text-white mt-2">{question.question}</h4>
        </div>

        <div className="grid gap-2">
          {question.options.map((option, index) => {
            const value = ['A', 'B', 'C', 'D'][index];
            const active = selectedAnswer === value;
            return (
              <button
                key={value}
                type="button"
                onClick={() => onSelect(value)}
                className={`w-full rounded-xl border px-4 py-3 text-left transition ${active ? 'border-blue-500 bg-slate-800' : 'border-slate-700 bg-slate-950/70 hover:border-slate-500'}`}>
                <div className="flex items-center gap-3">
                  <span className="w-7 h-7 inline-flex items-center justify-center rounded-full bg-slate-800 text-sm font-semibold text-slate-100">{value}</span>
                  <span className="text-sm text-slate-200">{option}</span>
                </div>
              </button>
            );
          })}
        </div>

        {showSolution && (
          <div className="rounded-xl border border-emerald-500/20 bg-emerald-500/5 p-4 text-sm text-emerald-200">
            <p className="font-semibold">Correct answer: {question.correctAnswer}</p>
            <p className="mt-2 text-slate-300">{question.explanation}</p>
          </div>
        )}
      </div>
    </Card>
  );
}
