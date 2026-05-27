import { Card } from '../../ui';

export function FillerWordDetector({ count = 0, words = [] }) {
  return (
    <Card className="bg-slate-900 border border-slate-700 p-5">
      <p className="text-sm text-slate-400 uppercase tracking-[0.2em]">Filler Words</p>
      <p className="text-4xl font-semibold text-white mt-4">{count}</p>
      <p className="text-slate-400 mt-2">Detected filler words in the current transcript.</p>
      {words.length ? (
        <div className="mt-4 flex flex-wrap gap-2 text-slate-200 text-sm">
          {words.map((word, idx) => (
            <span key={idx} className="rounded-full bg-slate-950 px-3 py-1">{word}</span>
          ))}
        </div>
      ) : null}
    </Card>
  );
}
