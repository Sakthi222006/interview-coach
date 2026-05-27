import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { Card, Button } from '../components/ui';
import { CodingResultCard } from '../components/features/company/CodingResultCard';
import { getCompanyProfiles, getCompanyCodingChallenges, submitCodingSolution } from '../services/companyService';

export default function CodingAssessmentPage() {
  const [companies, setCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState(null);
  const [challenges, setChallenges] = useState([]);
  const [selectedChallenge, setSelectedChallenge] = useState(null);
  const [code, setCode] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    async function load() {
      setLoading(true);
      const response = await getCompanyProfiles();
      if (response.success) {
        setCompanies(response.data || []);
        const companyId = Number(searchParams.get('companyId')) || response.data?.[0]?.id;
        const selected = response.data?.find((company) => company.id === companyId) || response.data?.[0];
        setSelectedCompany(selected);
      } else {
        setError(response.message);
      }
      setLoading(false);
    }

    load();
  }, [searchParams]);

  useEffect(() => {
    if (selectedCompany) {
      fetchChallenges();
    }
  }, [selectedCompany]);

  const fetchChallenges = async () => {
    if (!selectedCompany) return;
    setLoading(true);
    const response = await getCompanyCodingChallenges(selectedCompany.id);
    if (response.success) {
      setChallenges(response.data || []);
      setSelectedChallenge(response.data?.[0] || null);
      setCode('');
      setResult(null);
      setError('');
    } else {
      setError(response.message);
    }
    setLoading(false);
  };

  const handleSubmit = async () => {
    if (!selectedChallenge) return;
    setSubmitting(true);
    const response = await submitCodingSolution(selectedChallenge.id, code);
    setSubmitting(false);
    if (response.success) {
      setResult(response.data);
      setError('');
    } else {
      setError(response.message);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 py-8">
      <div className="max-w-7xl mx-auto px-6 space-y-8">
        <div className="space-y-2">
          <p className="text-slate-400 uppercase text-xs tracking-[0.3em]">Coding assessment</p>
          <h1 className="text-3xl font-bold text-white">Company-specific coding challenges</h1>
          <p className="text-slate-400 max-w-2xl">Practice coding problems tailored to your selected company’s technical expectations.</p>
        </div>

        <Card className="bg-slate-900 border-slate-700" padding="lg">
          <div className="grid gap-4 md:grid-cols-2">
            <div>
              <p className="text-slate-400 text-sm">Company</p>
              <select
                value={selectedCompany?.id || ''}
                onChange={(event) => {
                  const selected = companies.find((company) => company.id === Number(event.target.value));
                  setSelectedCompany(selected);
                }}
                className="mt-3 w-full rounded-2xl border border-slate-700 bg-slate-950 px-4 py-3 text-white"
              >
                {companies.map((company) => (
                  <option key={company.id} value={company.id}>{company.companyName}</option>
                ))}
              </select>
            </div>
            <div>
              <p className="text-slate-400 text-sm">Challenge</p>
              <select
                value={selectedChallenge?.id || ''}
                onChange={(event) => {
                  const selected = challenges.find((challenge) => challenge.id === Number(event.target.value));
                  setSelectedChallenge(selected);
                  setCode('');
                  setResult(null);
                }}
                className="mt-3 w-full rounded-2xl border border-slate-700 bg-slate-950 px-4 py-3 text-white"
              >
                {challenges.map((challenge) => (
                  <option key={challenge.id} value={challenge.id}>{challenge.title}</option>
                ))}
              </select>
            </div>
          </div>
        </Card>

        {loading && <div className="text-slate-400">Loading challenges…</div>}
        {error && <div className="text-red-400">{error}</div>}

        {selectedChallenge && (
          <div className="grid gap-6 xl:grid-cols-[0.7fr_0.3fr]">
            <div className="space-y-6">
              <Card className="bg-slate-900 border-slate-700" padding="lg">
                <div className="space-y-4">
                  <div className="flex items-center justify-between gap-4">
                    <div>
                      <p className="text-slate-400 text-sm">{selectedChallenge.topic}</p>
                      <h2 className="text-2xl font-semibold text-white">{selectedChallenge.title}</h2>
                    </div>
                    <div className="text-sm text-slate-300">
                      <p>Difficulty: {selectedChallenge.difficulty}</p>
                      <p>Acceptance: {selectedChallenge.acceptanceRate || 'N/A'}%</p>
                    </div>
                  </div>
                  <p className="text-slate-300">{selectedChallenge.description}</p>
                  <div className="grid gap-3 sm:grid-cols-2">
                    <div className="rounded-2xl bg-slate-950 p-4 border border-slate-700">
                      <p className="text-slate-400 text-xs uppercase tracking-[0.2em]">Example input</p>
                      <p className="mt-2 text-slate-200 text-sm">{selectedChallenge.exampleInput}</p>
                    </div>
                    <div className="rounded-2xl bg-slate-950 p-4 border border-slate-700">
                      <p className="text-slate-400 text-xs uppercase tracking-[0.2em]">Expected output</p>
                      <p className="mt-2 text-slate-200 text-sm">{selectedChallenge.exampleOutput}</p>
                    </div>
                  </div>
                  <div className="rounded-2xl bg-slate-950 p-4 border border-slate-700">
                    <p className="text-slate-400 text-xs uppercase tracking-[0.2em]">Constraints</p>
                    <p className="mt-2 text-slate-200 text-sm">{selectedChallenge.constraints}</p>
                  </div>
                </div>
              </Card>

              <Card className="bg-slate-900 border-slate-700" padding="lg">
                <div className="space-y-4">
                  <p className="text-slate-400 text-sm uppercase tracking-[0.24em]">Coding editor</p>
                  <textarea
                    value={code}
                    onChange={(event) => setCode(event.target.value)}
                    rows={12}
                    className="w-full rounded-2xl border border-slate-700 bg-slate-950 px-4 py-3 font-mono text-sm text-slate-100"
                    placeholder="Write your code here..."
                  />
                  <Button variant="primary" onClick={handleSubmit} loading={submitting}>
                    Submit Solution
                  </Button>
                </div>
              </Card>
            </div>

            {result && (
              <div className="space-y-6">
                <CodingResultCard result={result} />
                <Card className="bg-slate-900 border-slate-700" padding="lg">
                  <div className="space-y-3">
                    <p className="text-slate-400 text-sm uppercase tracking-[0.24em]">Next step</p>
                    <p className="text-slate-300">Use the feedback above to refine your implementation. Try a new company or difficulty from the menu.</p>
                    <Button variant="secondary" onClick={() => navigate('/company/preparation')}>Back to company prep</Button>
                  </div>
                </Card>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
