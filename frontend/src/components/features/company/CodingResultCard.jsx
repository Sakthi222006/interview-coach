import { Card } from '../../ui';

export function CodingResultCard({ result }) {
  return (
    <Card className="bg-slate-900 border-slate-700" padding="lg">
      <div className="space-y-4">
        <div className="flex items-center justify-between gap-4">
          <div>
            <p className="text-slate-400 text-sm">{result.title}</p>
            <p className={`text-2xl font-semibold ${result.passed ? 'text-emerald-400' : 'text-amber-300'}`}>
              {result.passed ? 'Passed' : 'Needs improvement'}
            </p>
          </div>
          <div className="rounded-full px-4 py-2 bg-slate-800 text-sm text-slate-200">
            {result.passedTests}/{result.totalTests} checks
          </div>
        </div>
        <p className="text-slate-300 text-sm">{result.feedback}</p>
      </div>
    </Card>
  );
}
