import { useState } from 'react';
import { Card, Button } from '../components/ui';
import { ResumeScoreCard } from '../components/features/resume/ResumeScoreCard';
import { SkillGapCard } from '../components/features/resume/SkillGapCard';
import { KeywordSuggestions } from '../components/features/resume/KeywordSuggestions';
import { useResumeReadiness } from '../hooks/useResumeReadiness';

export default function ResumeReadinessPage() {
  const [resumeId, setResumeId] = useState('');
  const [targetRole, setTargetRole] = useState('Product Manager');
  const [error, setError] = useState('');
  const { readiness, loading, loadReadiness } = useResumeReadiness();

  const handleReadiness = async () => {
    if (!resumeId || !targetRole) {
      setError('Please provide a resume ID and target role.');
      return;
    }

    setError('');
    await loadReadiness(resumeId, targetRole);
  };

  return (
    <div className="min-h-screen bg-slate-950 px-6 py-8">
      <div className="max-w-6xl mx-auto space-y-6">
        <Card className="p-6 bg-slate-900 border border-slate-700">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <p className="text-sm text-slate-400 uppercase tracking-[0.2em]">Resume Readiness</p>
              <h1 className="text-3xl font-semibold text-white mt-2">Readiness Review</h1>
              <p className="text-slate-500 mt-2">Get an AI-powered readiness rating and improvement plan for your selected role.</p>
            </div>
            <Button onClick={handleReadiness} disabled={loading}>Evaluate Readiness</Button>
          </div>
        </Card>

        <Card className="p-6 bg-slate-900 border border-slate-700">
          <div className="grid gap-4 lg:grid-cols-2">
            <div className="space-y-4">
              <label className="block text-sm text-slate-300">Resume ID</label>
              <input
                value={resumeId}
                onChange={(event) => setResumeId(event.target.value)}
                placeholder="Enter resume ID"
                className="w-full rounded-xl border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white"
              />
            </div>
            <div className="space-y-4">
              <label className="block text-sm text-slate-300">Target Role</label>
              <input
                value={targetRole}
                onChange={(event) => setTargetRole(event.target.value)}
                placeholder="Product Manager"
                className="w-full rounded-xl border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white"
              />
            </div>
          </div>

          {error && (
            <p className="mt-4 rounded-2xl bg-rose-950 p-4 text-sm text-rose-200">{error}</p>
          )}
        </Card>

        {readiness ? (
          <div className="grid gap-4 xl:grid-cols-[1.1fr_0.9fr]">
            <div className="space-y-4">
              <ResumeScoreCard score={readiness.readinessScore ?? 0} label="Readiness Score" subtitle="How prepared your resume is" />
              <Card className="p-6 bg-slate-900 border border-slate-700">
                <p className="text-sm text-slate-400 uppercase tracking-[0.2em] mb-4">Improvement Plan</p>
                <p className="text-white leading-7">{readiness.improvementPlan || 'No plan available yet.'}</p>
              </Card>
            </div>

            <div className="space-y-4">
              <SkillGapCard missingCount={readiness.gapSummary?.length ?? 0} details={readiness.gapSummary || []} />
              <KeywordSuggestions keywords={readiness.keyStrengths || []} />
            </div>
          </div>
        ) : (
          <Card className="p-6 bg-slate-900 border border-slate-700">
            <p className="text-slate-500">Submit a resume ID and target role above to see your readiness score and upgrade path.</p>
          </Card>
        )}
      </div>
    </div>
  );
}
