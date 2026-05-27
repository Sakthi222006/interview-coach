import { useEffect, useState } from 'react';
import { Card, Button } from '../components/ui';
import { ResumeScoreCard } from '../components/features/resume/ResumeScoreCard';
import { ATSScoreCard } from '../components/features/resume/ATSScoreCard';
import { SkillGapCard } from '../components/features/resume/SkillGapCard';
import { MissingSkillsTable } from '../components/features/resume/MissingSkillsTable';
import { KeywordSuggestions } from '../components/features/resume/KeywordSuggestions';
import { useResumeAnalysis } from '../hooks/useResumeAnalysis';

export default function ResumeAnalysisPage() {
  const [file, setFile] = useState(null);
  const [manualId, setManualId] = useState('');

  const {
    resumeId,
    analysis,
    history,
    preview,
    loading,
    error,
    submitUpload,
    runAnalysis,
    loadAnalysis,
    loadHistory,
    refresh,
    setResumeId,
  } = useResumeAnalysis();

  useEffect(() => {
    if (resumeId) {
      loadAnalysis(resumeId);
      loadHistory(resumeId);
    }
  }, [resumeId, loadAnalysis, loadHistory]);

  const handleUpload = async () => {
    if (!file) return;
    const result = await submitUpload(file);
    if (result?.success) {
      setResumeId(String(result.data.resumeId));
    }
  };

  const handleManualLoad = async () => {
    if (!manualId) return;
    setResumeId(manualId);
  };

  const atsScore = analysis?.resumeScore ? Math.round(Math.max(0, Math.min(100, analysis.resumeScore * 0.85))) : 0;
  const missingCount = analysis?.weaknesses?.length ?? 0;

  return (
    <div className="min-h-screen bg-slate-950 px-6 py-8">
      <div className="max-w-6xl mx-auto space-y-6">
        <Card className="p-6 bg-slate-900 border border-slate-700">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <p className="text-sm text-slate-400 uppercase tracking-[0.2em]">Resume Intelligence</p>
              <h1 className="text-3xl font-semibold text-white mt-2">Resume Analysis</h1>
              <p className="text-slate-500 mt-2">Upload a resume, generate structured analysis, and review gaps over time.</p>
            </div>
            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 w-full sm:w-auto">
              <Button onClick={refresh} disabled={loading || !resumeId}>Refresh Analysis</Button>
              <Button variant="secondary" onClick={() => window.location.reload()} disabled={loading}>Reset</Button>
            </div>
          </div>
        </Card>

        <div className="grid gap-4 xl:grid-cols-[0.9fr_0.7fr]">
          <div className="space-y-4">
            <Card className="p-6 bg-slate-900 border border-slate-700">
              <div className="grid gap-4 md:grid-cols-[1.1fr_0.9fr]">
                <div>
                  <label className="block text-sm text-slate-300 mb-2">Upload resume file</label>
                  <input
                    type="file"
                    accept="application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    onChange={(event) => setFile(event.target.files?.[0] ?? null)}
                    className="w-full rounded-xl border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white"
                  />
                  <p className="text-xs text-slate-500 mt-2">Supported: PDF or DOCX.</p>
                </div>
                <div className="flex flex-col justify-end gap-3">
                  <Button onClick={handleUpload} disabled={!file || loading}>Upload & Analyze</Button>
                  <div>
                    <label className="text-sm text-slate-300">Or enter resume ID</label>
                    <div className="mt-2 flex gap-2">
                      <input
                        value={manualId}
                        onChange={(event) => setManualId(event.target.value)}
                        placeholder="Resume ID"
                        className="w-full rounded-xl border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white"
                      />
                      <Button variant="secondary" onClick={handleManualLoad} disabled={!manualId || loading}>Load</Button>
                    </div>
                  </div>
                </div>
              </div>
            </Card>

            {error && (
              <Card className="p-4 bg-rose-950 border border-rose-700 text-rose-100">
                <p>{error}</p>
              </Card>
            )}

            {preview && (
              <Card className="p-6 bg-slate-900 border border-slate-700">
                <p className="text-sm text-slate-400">Uploaded Resume</p>
                <p className="text-white font-semibold mt-2">{preview.filename}</p>
                <p className="text-slate-500 text-sm mt-1">Preview: {preview.previewText || 'No preview available'}</p>
                <p className="text-slate-500 text-xs mt-2">Resume ID: {resumeId}</p>
              </Card>
            )}

            <div className="grid gap-4 lg:grid-cols-2">
              <ResumeScoreCard score={analysis?.resumeScore ?? 0} label="Resume Score" subtitle="AI quality rating" />
              <ATSScoreCard atsScore={atsScore} message="Estimated ATS compatibility" />
            </div>
          </div>

          <div className="space-y-4">
            <SkillGapCard missingCount={missingCount} details={analysis?.weaknesses || []} />
            <MissingSkillsTable skills={analysis?.weaknesses || []} />
          </div>
        </div>

        <div className="grid gap-4 xl:grid-cols-[1.1fr_0.9fr]">
          <Card className="p-6 bg-slate-900 border border-slate-700">
            <h2 className="text-xl font-semibold text-white mb-4">Resume Details</h2>
            {analysis ? (
              <div className="space-y-4 text-slate-300">
                <div>
                  <p className="text-sm text-slate-500">Skills</p>
                  <p className="mt-2 text-white">{analysis.skills?.join(', ') || 'None detected'}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-500">Technologies</p>
                  <p className="mt-2 text-white">{analysis.technologies?.join(', ') || 'None detected'}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-500">Recommended Roles</p>
                  <p className="mt-2 text-white">{analysis.recommendedRoles?.join(', ') || 'No recommendations yet'}</p>
                </div>
              </div>
            ) : (
              <p className="text-slate-500">Upload or load a resume to view extracted analysis fields.</p>
            )}
          </Card>

          <Card className="p-6 bg-slate-900 border border-slate-700">
            <h2 className="text-xl font-semibold text-white mb-4">Analysis History</h2>
            {history.length ? (
              <ul className="space-y-3 text-slate-300">
                {history.map((entry, idx) => (
                  <li key={idx} className="rounded-2xl border border-slate-700 bg-slate-950 p-4">
                    <p className="text-sm text-slate-400">{new Date(entry.analyzedAt).toLocaleString()}</p>
                    <p className="text-white mt-2">Score: {entry.analysis?.resumeScore ?? '—'}</p>
                    <p className="text-slate-500 text-sm mt-1">Confidence: {Math.round((entry.analysis?.confidenceScore ?? 0) * 100)}%</p>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="text-slate-500">No analysis history available yet.</p>
            )}
          </Card>
        </div>
      </div>
    </div>
  );
}
