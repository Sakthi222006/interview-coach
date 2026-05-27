import { Card } from '../../ui';

export function JobMatchCard({ matchedRole, matchScore, summary, highlights = [], gaps = [] }) {
  return (
    <Card className="p-5 bg-slate-900 border border-slate-700">
      <div className="mb-4">
        <p className="text-sm text-slate-400 uppercase tracking-[0.2em]">Best Matching Role</p>
        <h2 className="text-2xl font-semibold text-white mt-2">{matchedRole || 'No role found'}</h2>
      </div>

      <div className="grid gap-4 md:grid-cols-[1.2fr_0.8fr]">
        <div>
          <p className="text-sm text-slate-400">Role fit summary</p>
          <p className="text-slate-200 mt-2 leading-6">{summary || 'Complete a resume analysis to identify the best fit.'}</p>
        </div>
        <div className="rounded-3xl bg-slate-950 p-4 border border-slate-700 text-center">
          <p className="text-sm text-slate-400 uppercase tracking-[0.2em]">Match score</p>
          <p className="text-5xl font-bold text-cyan-400 mt-3">{matchScore ?? '—'}</p>
        </div>
      </div>

      <div className="mt-5 grid gap-3 sm:grid-cols-2">
        <div>
          <p className="text-xs uppercase tracking-[0.2em] text-slate-500">Highlights</p>
          <ul className="mt-2 space-y-2 text-slate-300 text-sm">
            {highlights.length ? highlights.map((item, idx) => <li key={idx}>• {item}</li>) : <li>Upload and analyze your resume to see match highlights.</li>}
          </ul>
        </div>
        <div>
          <p className="text-xs uppercase tracking-[0.2em] text-slate-500">Suggested gaps</p>
          <ul className="mt-2 space-y-2 text-slate-300 text-sm">
            {gaps.length ? gaps.map((item, idx) => <li key={idx}>• {item}</li>) : <li>Suggested gaps will appear after matching.</li>}
          </ul>
        </div>
      </div>
    </Card>
  );
}
