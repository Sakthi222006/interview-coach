import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Button } from '../components/ui';
import { CompanyCard } from '../components/features/company/CompanyCard';
import { CompanyRoadmapCard } from '../components/features/company/CompanyRoadmapCard';
import { getCompanies, getCompanyMockInterview } from '../services/companyService';

export default function CompanyPreparationPage() {
  const [companies, setCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [difficultyFilter, setDifficultyFilter] = useState('ALL');
  const [mockInterview, setMockInterview] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [mockLoading, setMockLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    async function loadProfiles() {
      setLoading(true);
      const response = await getCompanies();
      if (response.success) {
        setCompanies(response.data || []);
        setSelectedCompany(response.data?.[0] || null);
      } else {
        setError(response.message);
      }
      setLoading(false);
    }

    loadProfiles();
  }, []);

  const handleSelect = (company) => {
    setSelectedCompany(company);
    setMockInterview(null);
  };

  const visibleCompanies = useMemo(() => {
    return companies.filter((company) => {
      const matchesSearch = company.companyName.toLowerCase().includes(searchQuery.toLowerCase())
        || company.description?.toLowerCase().includes(searchQuery.toLowerCase());
      const matchesDifficulty = difficultyFilter === 'ALL' || company.difficulty === difficultyFilter;
      return matchesSearch && matchesDifficulty;
    });
  }, [companies, searchQuery, difficultyFilter]);

  const loadMockInterview = async (company) => {
    setMockLoading(true);
    const response = await getCompanyMockInterview(company.id);
    if (response.success) {
      setMockInterview(response.data);
      setSelectedCompany(company);
    }
    setMockLoading(false);
  };

  const renderMain = () => {
    if (loading) {
      return <div className="text-slate-400">Loading company profiles…</div>;
    }
    if (error) {
      return <div className="text-red-400">{error}</div>;
    }
    if (!companies.length) {
      return <div className="text-slate-400">No company profiles available yet.</div>;
    }

    return (
      <div className="grid gap-6 xl:grid-cols-[0.7fr_0.3fr]">
        <div className="space-y-6">
          <Card className="bg-slate-900 border-slate-700" padding="lg">
            <div className="grid gap-4 lg:grid-cols-[1fr_auto] items-end">
              <div>
                <p className="text-slate-400 text-sm uppercase tracking-[0.24em]">Explore companies</p>
                <h2 className="text-xl font-semibold text-white mt-2">Find the right target company</h2>
                <p className="text-slate-500 text-sm mt-1">Search, filter, and compare placement-ready profiles.</p>
              </div>
              <div className="flex flex-col sm:flex-row gap-3">
                <div className="min-w-[220px]">
                  <label className="block text-slate-400 text-xs uppercase tracking-[0.2em] mb-2">Difficulty</label>
                  <select
                    value={difficultyFilter}
                    onChange={(event) => setDifficultyFilter(event.target.value)}
                    className="w-full rounded-2xl border border-slate-700 bg-slate-950 px-4 py-3 text-white"
                  >
                    <option value="ALL">All difficulties</option>
                    <option value="EASY">Easy</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HARD">Hard</option>
                  </select>
                </div>
                <div className="min-w-[220px]">
                  <label className="block text-slate-400 text-xs uppercase tracking-[0.2em] mb-2">Search</label>
                  <input
                    value={searchQuery}
                    onChange={(event) => setSearchQuery(event.target.value)}
                    placeholder="Search companies"
                    className="w-full rounded-2xl border border-slate-700 bg-slate-950 px-4 py-3 text-white"
                  />
                </div>
              </div>
            </div>
          </Card>

          {visibleCompanies.length === 0 ? (
            <div className="rounded-2xl border border-slate-700 bg-slate-900 p-8 text-slate-400">
              No company profiles match your filter. Try another keyword or difficulty.
            </div>
          ) : (
            <div className="grid gap-4 md:grid-cols-2">
              {visibleCompanies.map((company) => (
                <CompanyCard
                  key={company.id}
                  company={company}
                  onPractice={() => navigate(`/company/aptitude?companyName=${encodeURIComponent(company.companyName)}`)}
                  onReadiness={() => navigate(`/company/readiness?companyName=${encodeURIComponent(company.companyName)}`)}
                  onMockInterview={() => loadMockInterview(company)}
                />
              ))}
            </div>
          )}
        </div>

        <aside className="space-y-6">
          {selectedCompany && (
            <Card className="bg-slate-900 border-slate-700" padding="lg">
              <div className="space-y-5">
                <div>
                  <p className="text-slate-400 text-sm uppercase tracking-[0.24em]">Selected company</p>
                  <h3 className="text-2xl font-semibold text-white mt-2">{selectedCompany.companyName}</h3>
                  <p className="text-slate-400 text-sm mt-2">{selectedCompany.description}</p>
                </div>
                <div className="grid gap-3">
                  <div className="rounded-2xl bg-slate-950 p-4 border border-slate-700">
                    <p className="text-slate-400 text-xs uppercase tracking-[0.2em]">Difficulty</p>
                    <p className="mt-2 text-white font-semibold">{selectedCompany.difficulty || 'N/A'}</p>
                  </div>
                  <div className="rounded-2xl bg-slate-950 p-4 border border-slate-700">
                    <p className="text-slate-400 text-xs uppercase tracking-[0.2em]">Hiring Pattern</p>
                    <p className="mt-2 text-white font-semibold">{selectedCompany.hiringPattern || 'N/A'}</p>
                  </div>
                  <div className="rounded-2xl bg-slate-950 p-4 border border-slate-700">
                    <p className="text-slate-400 text-xs uppercase tracking-[0.2em]">Interview Rounds</p>
                    <p className="mt-2 text-white font-semibold">{selectedCompany.interviewRounds?.join(' → ') || 'N/A'}</p>
                  </div>
                </div>
                <div className="grid gap-3">
                  <div className="rounded-2xl bg-slate-950 p-4 border border-slate-700">
                    <p className="text-slate-400 text-xs uppercase tracking-[0.2em]">Aptitude weightage</p>
                    <p className="mt-2 text-white font-semibold">{selectedCompany.aptitudeWeightage ?? 0}%</p>
                  </div>
                  <div className="rounded-2xl bg-slate-950 p-4 border border-slate-700">
                    <p className="text-slate-400 text-xs uppercase tracking-[0.2em]">Coding weightage</p>
                    <p className="mt-2 text-white font-semibold">{selectedCompany.codingWeightage ?? 0}%</p>
                  </div>
                  <div className="rounded-2xl bg-slate-950 p-4 border border-slate-700">
                    <p className="text-slate-400 text-xs uppercase tracking-[0.2em]">Communication weightage</p>
                    <p className="mt-2 text-white font-semibold">{selectedCompany.communicationWeightage ?? 0}%</p>
                  </div>
                </div>
              </div>
            </Card>
          )}

          {selectedCompany && <CompanyRoadmapCard company={selectedCompany} />}

          <Card className="bg-slate-900 border-slate-700" padding="lg">
            <div className="space-y-4">
              <p className="text-sm uppercase tracking-[0.24em] text-slate-400">Quick actions</p>
              <button
                className="btn btn-secondary w-full"
                onClick={() => navigate(`/company/coding?companyName=${encodeURIComponent(selectedCompany?.companyName || '')}`)}
              >
                Browse Coding Challenges
              </button>
              <button
                className="btn btn-secondary w-full"
                onClick={() => navigate(`/company/aptitude?companyName=${encodeURIComponent(selectedCompany?.companyName || '')}`)}
              >
                Practice Aptitude
              </button>
              <button
                className="btn btn-secondary w-full"
                onClick={() => navigate(`/company/readiness?companyName=${encodeURIComponent(selectedCompany?.companyName || '')}`)}
              >
                View Readiness Scores
              </button>
            </div>
          </Card>

          {mockInterview && (
            <Card className="bg-slate-900 border-slate-700" padding="lg">
              <div className="space-y-4">
                <div>
                  <p className="text-sm text-slate-400">{mockInterview.companyName} interview plan</p>
                  <h3 className="text-lg font-semibold text-white">{mockInterview.briefing}</h3>
                </div>
                <div className="space-y-3">
                  {mockInterview.rounds.map((round, index) => (
                    <div key={index} className="rounded-2xl border border-slate-700 p-4 bg-slate-950/80">
                      <p className="text-sm text-slate-300 font-semibold">{round.title}</p>
                      <p className="text-slate-400 text-sm mt-2">{round.description}</p>
                      <p className="text-slate-500 text-xs mt-3">Focus: {round.focusAreas.join(', ')}</p>
                    </div>
                  ))}
                </div>
              </div>
            </Card>
          )}

          <button
            disabled={mockLoading || !selectedCompany}
            onClick={() => selectedCompany && loadMockInterview(selectedCompany)}
            className="btn btn-primary w-full"
          >
            {mockLoading ? 'Generating interview…' : 'Generate Mock Interview'}
          </button>
        </aside>
      </div>
    );
  };

  return (
    <div className="min-h-screen bg-slate-950 py-8">
      <div className="max-w-7xl mx-auto px-6 space-y-8">
        <div className="flex flex-col gap-2">
          <p className="text-slate-400 uppercase text-xs tracking-[0.3em]">Milestone 10</p>
          <h1 className="text-3xl font-bold text-white">Company Preparation</h1>
          <p className="text-slate-400 max-w-2xl">Explore placement-specific company profiles, generate mock interviews, and begin company-focused practice.</p>
        </div>

        {renderMain()}
      </div>
    </div>
  );
}
