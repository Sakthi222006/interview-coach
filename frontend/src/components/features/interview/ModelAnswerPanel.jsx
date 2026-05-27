import { useState } from 'react';

export function ModelAnswerPanel({ modelAnswer, interviewerFeedback }) {
  const [show, setShow] = useState(false);
  if (!modelAnswer && !interviewerFeedback) return null;

  return (
    <div className="space-y-3">
      {interviewerFeedback && (
        <div className="p-4 rounded-lg"
          style={{ background: 'rgba(59,130,246,0.07)', border: '1px solid rgba(59,130,246,0.15)' }}>
          <p className="section-label mb-2">Interviewer Feedback</p>
          <p className="text-sm italic" style={{ color: 'var(--color-content-secondary)', lineHeight: '1.7' }}>
            "{interviewerFeedback}"
          </p>
        </div>
      )}
      {modelAnswer && (
        <div>
          <button
            type="button"
            onClick={() => setShow(!show)}
            className="text-xs font-medium transition-colors"
            style={{ color: 'var(--color-content-link)' }}
          >
            {show ? '▼ Hide Model Answer' : '▶ Show Model Answer'}
          </button>
          {show && (
            <div className="mt-2 p-4 rounded-lg animate-fade-in"
              style={{ background: 'rgba(139,92,246,0.07)', border: '1px solid rgba(139,92,246,0.15)' }}>
              <p className="section-label mb-2">Suggested Answer</p>
              <p className="text-sm" style={{ color: 'var(--color-content-secondary)', lineHeight: '1.7' }}>
                {modelAnswer}
              </p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
