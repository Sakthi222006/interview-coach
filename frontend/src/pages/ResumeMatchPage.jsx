import { useState } from 'react';
import { Card, Button } from '../components/ui';
import { ResumeScoreCard } from '../components/features/resume/ResumeScoreCard';
import { SkillGapCard } from '../components/features/resume/SkillGapCard';
import { MissingSkillsTable } from '../components/features/resume/MissingSkillsTable';
import { KeywordSuggestions } from '../components/features/resume/KeywordSuggestions';
import * as resumeService from '../services/resumeService';

export default function ResumeMatchPage() {
  const [resumeId, setResumeId] = useState('');
  const [targetRole, setTargetRole] = useState('Software Engineer');
  const [jobDescription, setJobDescription] = useState('');
  const [matchResult, setMatchResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleMatch = async () => {
    if (!resumeId || !targetRole) {
      setError('Please enter both a resume ID and target role.');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await resumeService.matchResume({
        resumeId,
        targetRole,
        jobDescription,
      });
      setMatchResult(response.data);
    } catch (e) {
      setError(e.message || 'Unable to calculate match at this time.');
    } finally {
      setLoading(false);
    }
  };

  const skills = matchResult?.suggestedSkills || [];
  const gaps = matchResult?.missingSkills || [];

  return (
    <div className="min-h-screen bg-slate-950 px-6 py-8">
      <div className="max-w-6xl mx-auto space-y-6">
        <Card className="p-6 bg-slate-900 border border-slate-700">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <p className="text-sm text-slate-400 uppercase tracking-[0.2em]">Resume Match</p>
              <h1 className="text-3xl font-semibold text-white mt-2">Role Fit & Match</h1>
              <p className="text-slate-500 mt-2">Compare your resume to a job title and job description to see how well you fit.</p>
            </div>
            <Button onClick={handleMatch} disabled={loading}>Run Match</Button>
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
                placeholder="Software Engineer"
                className="w-full rounded-xl border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white"
              />
            </div>
          </div>

          <div className="mt-4">
            <label className="block text-sm text-slate-300">Job Description</label>
            <textarea
              rows="6"
              value={jobDescription}
              onChange={(event) => setJobDescription(event.target.value)}
              placeholder="Paste the job description or role brief here"
              className="w-full rounded-2xl border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white"
            />
          </div>

          {error && (
            <p className="mt-4 rounded-2xl bg-rose-950 p-4 text-sm text-rose-200">{error}</p>
          )}
        </Card>

        {matchResult && (
          <div className="grid gap-4 xl:grid-cols-[1.1fr_0.9fr]">
            <div className="space-y-4">
              <ResumeScoreCard score={matchResult.matchScore ?? 0} label="Match Score" subtitle="Fit against target role" />
              <Card className="p-6 bg-slate-900 border border-slate-700">
                <p className="text-sm text-slate-400 uppercase tracking-[0.2em]">Match Summary</p>
                <p className="mt-4 text-white">{matchResult.summary || 'No summary available.'}</p>
              </Card>
            </div>

            <div className="space-y-4">
              <SkillGapCard missingCount={gaps.length} details={gaps} />
              <KeywordSuggestions keywords={skills} />
            </div>
          </div>
        )}

        {matchResult && (
          <div className="grid gap-4 lg:grid-cols-2">
            <MissingSkillsTable skills={gaps} />
            <Card className="p-6 bg-slate-900 border border-slate-700">
              <p className="text-sm text-slate-400 uppercase tracking-[0.2em] mb-4">Recommended Skills</p>
              <div className="space-y-3 text-slate-300">
                {skills.length ? skills.map((keyword, idx) => (
                  <p key={idx} className="rounded-2xl bg-slate-950 p-3">{keyword}</p>
                )) : <p className="text-slate-500">No skill recommendations yet.</p>}
              </div>
            </Card>
          </div>
        )}
      </div>
    </div>
  );
}
