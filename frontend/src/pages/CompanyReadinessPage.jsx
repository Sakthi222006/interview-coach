import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { Card } from '../components/ui';
import { ReadinessGauge } from '../components/features/resume/ReadinessGauge';
import { getCompanies, getCompanyReadiness } from '../services/companyService';

export default function CompanyReadinessPage() {
  const [companies, setCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState(null);
  const [readiness, setReadiness] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const companyNameParam = useMemo(() => searchParams.get('companyName'), [searchParams]);

  useEffect(() => {
    async function init() {
      setLoading(true);
      const profileResponse = await getCompanies();
      if (profileResponse.success) {
        setCompanies(profileResponse.data || []);
        const selected = profileResponse.data?.find((company) => company.companyName === companyNameParam)
          || profileResponse.data?.[0];
        setSelectedCompany(selected);
        if (selected) {
          const readinessResponse = await getCompanyReadiness(selected.companyName);
          if (readinessResponse.success) {
            setReadiness(readinessResponse.data);
          } else {
            setError(readinessResponse.message);
          }
        }
      } else {
        setError(profileResponse.message);
      }
      setLoading(false);
    }

    init();
  }, [companyNameParam]);

  const handleSelectCompany = async (company) => {
    setSelectedCompany(company);
    setReadiness(null);
    setError('');
    const readinessResponse = await getCompanyReadiness(company.companyName);
    if (readinessResponse.success) {
      setReadiness(readinessResponse.data);
    } else {
      setError(readinessResponse.message);
    }
  };

  const readinessValues = useMemo(() => {
    if (!readiness) return [];
    return [
      { label: 'Resume', value: readiness.resumeScore },
      { label: 'Aptitude', value: readiness.aptitudeScore },
      { label: 'Coding', value: readiness.codingScore },
      { label: 'Communication', value: readiness.communicationScore },
      { label: 'Interview', value: readiness.interviewScore },
    ];
  }, [readiness]);

  const renderScoreBar = (label, value) => (
    <div key={label} className="space-y-2">
      <div className="flex justify-between text-sm text-slate-400">
        <span>{label}</span>
        <span>{value}%</span>
      </div>
      <div className="h-2 rounded-full bg-slate-800 overflow-hidden">
        <div className="h-full rounded-full bg-emerald-500" style={{ width: `${value}%` }} />
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-slate-950 py-8">
      <div className="max-w-7xl mx-auto px-6 space-y-8">
        <div className="space-y-2">
          <p className="text-slate-400 uppercase text-xs tracking-[0.3em]">Placement readiness</p>
          <h1 className="text-3xl font-bold text-white">Company readiness score</h1>
          <p className="text-slate-400 max-w-2xl">See where you stand for the company you want to prepare for, based on resume strength and interview weights.</p>
        </div>

        <div className="grid gap-6 lg:grid-cols-[0.7fr_0.3fr]">
          <div className="space-y-6">
            <Card className="bg-slate-900 border-slate-700" padding="lg">
              <div className="flex flex-col gap-4">
                <div>
                  <p className="text-slate-400 text-sm uppercase tracking-[0.24em]">Choose company</p>
                  <div className="mt-4 flex flex-wrap gap-3">
                    {companies.map((company) => (
                      <button
                        key={company.id}
                        type="button"
                        onClick={() => handleSelectCompany(company)}
                        className={`rounded-full px-4 py-2 text-sm ${selectedCompany?.id === company.id ? 'bg-blue-600 text-white' : 'bg-slate-800 text-slate-300 hover:bg-slate-700'}`}>
                        {company.companyName}
                      </button>
                    ))}
                  </div>
                </div>
                <div className="grid gap-3 md:grid-cols-2">
                  <div className="rounded-2xl bg-slate-950 p-5 border border-slate-800">
                    <p className="text-slate-400 text-sm">Hiring pattern</p>
                    <p className="text-white mt-2">{selectedCompany?.hiringPattern || 'N/A'}</p>
                  </div>
                  <div className="rounded-2xl bg-slate-950 p-5 border border-slate-800">
                    <p className="text-slate-400 text-sm">Interview rounds</p>
                    <p className="text-white mt-2">{selectedCompany?.interviewRounds?.join(' → ') || 'N/A'}</p>
                  </div>
                </div>
              </div>
            </Card>

            {loading && <div className="text-slate-400">Loading readiness data…</div>}
            {error && <div className="text-red-400">{error}</div>}

            {readiness && (
              <>
                <Card className="bg-slate-900 border-slate-700" padding="lg">
                  <div className="grid gap-6 lg:grid-cols-[0.6fr_0.4fr]">
                    <div className="space-y-4">
                      <p className="text-slate-400 uppercase tracking-[0.24em] text-xs">Summary</p>
                      <h2 className="text-2xl font-semibold text-white">{readiness.companyName} readiness</h2>
                      <p className="text-slate-400">{readiness.summary}</p>
                      <div className="space-y-3">
                        <div>
                          <p className="text-slate-400 text-sm">Strengths</p>
                          <ul className="list-disc list-inside text-slate-300 mt-2 space-y-1">
                            {readiness.strengths.map((point, index) => (
                              <li key={index}>{point}</li>
                            ))}
                          </ul>
                        </div>
                        <div>
                          <p className="text-slate-400 text-sm">Improvement areas</p>
                          <ul className="list-disc list-inside text-slate-300 mt-2 space-y-1">
                            {readiness.improvementAreas.map((point, index) => (
                              <li key={index}>{point}</li>
                            ))}
                          </ul>
                        </div>
                      </div>
                    </div>
                    <div className="space-y-4">
                      <ReadinessGauge score={readiness.readinessScore} overall={readiness.readinessLevel} />
                      <button className="btn btn-primary w-full" onClick={() => navigate('/company/preparation')}>
                        Explore company preparation
                      </button>
                    </div>
                  </div>
                </Card>

                <Card className="bg-slate-900 border-slate-700" padding="lg">
                  <div className="space-y-6">
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="text-slate-400 uppercase tracking-[0.24em] text-xs">Readiness breakdown</p>
                        <h3 className="text-xl font-semibold text-white">Where to focus next</h3>
                      </div>
                      <span className="text-sm text-slate-400">Overall score: {readiness.readinessScore}%</span>
                    </div>

                    <div className="grid gap-4 lg:grid-cols-2">
                      <div className="space-y-4">
                        {readinessValues.map(({ label, value }) => renderScoreBar(label, value))}
                      </div>
                      <div className="rounded-3xl bg-slate-950 p-5 border border-slate-800">
                        <p className="text-slate-400 text-sm uppercase tracking-[0.22em]">Top recommendations</p>
                        <ol className="list-decimal list-inside text-slate-300 mt-4 space-y-3">
                          {readiness.improvementAreas.slice(0, 4).map((item, index) => (
                            <li key={index}>{item}</li>
                          ))}
                        </ol>
                      </div>
                    </div>
                  </div>
                </Card>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
