// frontend/src/pages/RecruiterSetupPage.jsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Button, InputField, Alert } from '../components/ui';
import { PageContainer } from '../components/layout';
import { useRecruiterMode } from '../hooks/useRecruiterMode';
import { RecruiterAvatar } from '../components/features/recruiter';

export default function RecruiterSetupPage() {
  const navigate = useNavigate();
  const { createScenario, isLoading, error } = useRecruiterMode();

  const [selectedRecruiter, setSelectedRecruiter] = useState('HR_RECRUITER');
  const [selectedRound, setSelectedRound] = useState('HR_ROUND');
  const [title, setTitle] = useState('');
  const [context, setContext] = useState('');
  const [jobDesc, setJobDesc] = useState('');

  const recruiterTypes = [
    { value: 'HR_RECRUITER', label: 'HR Recruiter', emoji: '👩‍💼' },
    { value: 'SENIOR_DEVELOPER', label: 'Senior Developer', emoji: '👨‍💻' },
    { value: 'TECH_LEAD', label: 'Tech Lead', emoji: '🎯' },
    { value: 'ENGINEERING_MANAGER', label: 'Engineering Manager', emoji: '📊' },
    { value: 'SYSTEM_DESIGN_INTERVIEWER', label: 'System Design', emoji: '🏗️' },
  ];

  const roundTypes = [
    { value: 'HR_ROUND', label: 'HR Round' },
    { value: 'TECHNICAL_ROUND', label: 'Technical Round' },
    { value: 'MANAGERIAL_ROUND', label: 'Managerial Round' },
    { value: 'SYSTEM_DESIGN_ROUND', label: 'System Design' },
    { value: 'BEHAVIORAL_ROUND', label: 'Behavioral Round' },
  ];

  const handleStart = async () => {
    if (!title.trim()) {
      alert('Please enter an interview title');
      return;
    }

    await createScenario(
      selectedRecruiter,
      selectedRound,
      title,
      context,
      jobDesc
    );

    if (!error) {
      navigate('/recruiter/interview', { 
        state: { recruiterType: selectedRecruiter, roundType: selectedRound } 
      });
    }
  };

  const recruiterInfo = recruiterTypes.find(r => r.value === selectedRecruiter);

  return (
    <PageContainer>
      <div className="max-w-4xl mx-auto py-8 space-y-8">
        <div>
          <h1 className="text-3xl font-bold text-content">AI Recruiter Interview</h1>
          <p className="text-content-muted mt-2">
            Practice with realistic recruiter simulations tailored to different interview rounds
          </p>
        </div>

        {error && <Alert type="error" message={error} />}

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Left: Recruiter Selection */}
          <div className="lg:col-span-1 space-y-4">
            <h2 className="text-lg font-semibold text-content">Select Recruiter</h2>
            <div className="space-y-3">
              {recruiterTypes.map((rec) => (
                <Card
                  key={rec.value}
                  interactive
                  onClick={() => setSelectedRecruiter(rec.value)}
                  padding="md"
                  className={`cursor-pointer transition-all ${
                    selectedRecruiter === rec.value
                      ? 'ring-2 ring-brand-500 bg-brand-50'
                      : 'hover:bg-gray-50'
                  }`}
                >
                  <p className="text-lg mb-2">{rec.emoji}</p>
                  <p className="font-medium text-content text-sm">{rec.label}</p>
                </Card>
              ))}
            </div>
          </div>

          {/* Right: Configuration */}
          <div className="lg:col-span-2 space-y-6">
            {/* Recruiter Preview */}
            {recruiterInfo && (
              <Card padding="lg" accent="blue">
                <p className="text-xs text-content-muted mb-3">Selected Recruiter</p>
                <RecruiterAvatar
                  recruiterName={recruiterInfo.label}
                  recruiterType={selectedRecruiter}
                  personality="You'll be interviewing with this recruiter profile"
                />
              </Card>
            )}

            {/* Interview Configuration */}
            <Card padding="lg">
              <h3 className="text-lg font-semibold text-content mb-4">Interview Setup</h3>
              
              <div className="space-y-4">
                {/* Round Type */}
                <div>
                  <label className="block text-sm font-medium text-content mb-2">
                    Interview Round
                  </label>
                  <select
                    value={selectedRound}
                    onChange={(e) => setSelectedRound(e.target.value)}
                    className="w-full px-4 py-2 rounded-lg border border-border bg-surface text-content focus:outline-none focus:ring-2 focus:ring-brand-500"
                  >
                    {roundTypes.map((round) => (
                      <option key={round.value} value={round.value}>
                        {round.label}
                      </option>
                    ))}
                  </select>
                </div>

                {/* Title */}
                <InputField
                  label="Interview Title"
                  placeholder="e.g., Software Engineer - L3 Position"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                />

                {/* Context */}
                <div>
                  <label className="block text-sm font-medium text-content mb-2">
                    Interview Context (Optional)
                  </label>
                  <textarea
                    value={context}
                    onChange={(e) => setContext(e.target.value)}
                    placeholder="Provide context about the interview (team, project, etc.)"
                    rows={3}
                    className="w-full px-4 py-2 rounded-lg border border-border bg-surface text-content focus:outline-none focus:ring-2 focus:ring-brand-500"
                  />
                </div>

                {/* Job Description */}
                <div>
                  <label className="block text-sm font-medium text-content mb-2">
                    Job Description (Optional)
                  </label>
                  <textarea
                    value={jobDesc}
                    onChange={(e) => setJobDesc(e.target.value)}
                    placeholder="Paste the job description to make the interview more relevant"
                    rows={4}
                    className="w-full px-4 py-2 rounded-lg border border-border bg-surface text-content focus:outline-none focus:ring-2 focus:ring-brand-500"
                  />
                </div>

                {/* Start Button */}
                <Button
                  variant="primary"
                  size="lg"
                  onClick={handleStart}
                  disabled={isLoading}
                  className="w-full"
                >
                  {isLoading ? 'Starting Interview...' : 'Start Interview'}
                </Button>
              </div>
            </Card>

            {/* Tips */}
            <Card padding="md" accent="green">
              <h4 className="font-semibold text-content text-sm mb-2">💡 Tips</h4>
              <ul className="text-xs text-content-muted space-y-1">
                <li>• Answer naturally and conversationally</li>
                <li>• The recruiter will provide follow-up questions</li>
                <li>• Your performance will be evaluated on multiple dimensions</li>
                <li>• You'll receive detailed feedback after the interview</li>
              </ul>
            </Card>
          </div>
        </div>
      </div>
    </PageContainer>
  );
}
