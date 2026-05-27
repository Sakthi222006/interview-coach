import { Card } from '../../ui';

export function KeywordSuggestions({ keywords = [] }) {
  return (
    <Card className="p-5 bg-slate-900 border border-slate-700">
      <p className="text-sm text-slate-400 uppercase tracking-[0.2em] mb-4">Keyword Suggestions</p>
      <div className="flex flex-wrap gap-2">
        {keywords.length ? keywords.map((keyword, idx) => (
          <span key={idx} className="inline-flex items-center rounded-full bg-slate-800 px-3 py-1 text-xs text-slate-200 border border-slate-700">
            {keyword}
          </span>
        )) : (
          <span className="text-slate-500 text-sm">No keyword suggestions available yet.</span>
        )}
      </div>
    </Card>
  );
}
