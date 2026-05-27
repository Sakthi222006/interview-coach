import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { Card, Button } from '../components/ui';
import { AptitudeQuestionCard } from '../components/features/company/AptitudeQuestionCard';
import { getCompanyProfiles, getCompanyQuestions } from '../services/companyService';

const categoryOptions = ['QUANTITATIVE', 'LOGICAL_REASONING', 'VERBAL_ABILITY', 'DATA_INTERPRETATION'];
const difficultyOptions = ['EASY', 'MEDIUM', 'HARD'];

export default function AptitudePracticePage() {
  const [companies, setCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [answers, setAnswers] = useState({});
  const [showResults, setShowResults] = useState(false);
  const [score, setScore] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [category, setCategory] = useState('QUANTITATIVE');
  const [difficulty, setDifficulty] = useState('EASY');
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
      fetchQuestions();
    }
  }, [selectedCompany, category, difficulty]);

  const fetchQuestions = async () => {
    if (!selectedCompany) return;
    setLoading(true);
    const response = await getCompanyQuestions(selectedCompany.id, category, difficulty, 5);
    if (response.success) {
      setQuestions(response.data || []);
      setAnswers({});
      setShowResults(false);
      setScore(0);
      setError('');
    } else {
      setError(response.message);
    }
    setLoading(false);
  };

  const handleAnswer = (questionId, answer) => {
    setAnswers((prev) => ({ ...prev, [questionId]: answer }));
  };

  const handleSubmit = () => {
    const total = questions.length;
    const correct = questions.reduce((count, question) => {
      const selected = answers[question.id];
      return count + (selected === question.correctAnswer ? 1 : 0);
    }, 0);
    setScore(total > 0 ? Math.round((correct / total) * 100) : 0);
    setShowResults(true);
  };

  return (
    <div className="min-h-screen bg-slate-950 py-8">
      <div className="max-w-7xl mx-auto px-6 space-y-8">
        <div className="space-y-2">
          <p className="text-slate-400 uppercase text-xs tracking-[0.3em]">Aptitude practice</p>
          <h1 className="text-3xl font-bold text-white">Company-specific aptitude questions</h1>
          <p className="text-slate-400 max-w-2xl">Practice aptitude questions tailored to the company you want to prepare for.</p>
        </div>

        <Card className="bg-slate-900 border-slate-700" padding="lg">
          <div className="grid gap-4 lg:grid-cols-3">
            <div>
              <p className="text-slate-400 text-sm">Target company</p>
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
              <p className="text-slate-400 text-sm">Question category</p>
              <select
                value={category}
                onChange={(event) => setCategory(event.target.value)}
                className="mt-3 w-full rounded-2xl border border-slate-700 bg-slate-950 px-4 py-3 text-white"
              >
                {categoryOptions.map((option) => (
                  <option key={option} value={option}>{option.replace('_', ' ')}</option>
                ))}
              </select>
            </div>
            <div>
              <p className="text-slate-400 text-sm">Difficulty</p>
              <select
                value={difficulty}
                onChange={(event) => setDifficulty(event.target.value)}
                className="mt-3 w-full rounded-2xl border border-slate-700 bg-slate-950 px-4 py-3 text-white"
              >
                {difficultyOptions.map((level) => (
                  <option key={level} value={level}>{level}</option>
                ))}
              </select>
            </div>
          </div>
        </Card>

        {loading && <div className="text-slate-400">Loading questions…</div>}
        {error && <div className="text-red-400">{error}</div>}

        {questions.length > 0 && (
          <div className="space-y-6">
            {questions.map((question) => (
              <AptitudeQuestionCard
                key={question.id}
                question={question}
                selectedAnswer={answers[question.id]}
                onSelect={(choice) => handleAnswer(question.id, choice)}
                showSolution={showResults}
              />
            ))}

            <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
              <Button variant="secondary" onClick={() => fetchQuestions()}>Refresh questions</Button>
              <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
                {showResults && (
                  <p className="text-white text-sm">Score: <span className="font-semibold">{score}%</span></p>
                )}
                <Button variant="primary" onClick={handleSubmit}>Submit Answers</Button>
              </div>
            </div>
          </div>
        )}

        {!loading && questions.length === 0 && (
          <div className="text-slate-400">No questions available for this company and selection yet. Try a different company, category, or difficulty.</div>
        )}
      </div>
    </div>
  );
}
