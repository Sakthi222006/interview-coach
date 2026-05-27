import { Card } from '../../ui';

export function MissingSkillsTable({ skills = [] }) {
  return (
    <Card className="p-5 bg-slate-900 border border-slate-700 overflow-x-auto">
      <p className="text-sm text-slate-400 uppercase tracking-[0.2em] mb-4">Skill Gaps</p>
      {skills.length ? (
        <table className="w-full text-left text-sm text-slate-200">
          <thead>
            <tr className="border-b border-slate-700 text-slate-400">
              <th className="py-3">Skill</th>
              <th className="py-3">Reason</th>
            </tr>
          </thead>
          <tbody>
            {skills.map((skill, idx) => (
              <tr key={idx} className="border-b border-slate-800 even:bg-slate-950/50">
                <td className="py-3">{skill}</td>
                <td className="py-3 text-slate-500">Add more context or projects for this skill.</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p className="text-slate-500">No missing skills detected yet. Run a match or readiness review.</p>
      )}
    </Card>
  );
}
