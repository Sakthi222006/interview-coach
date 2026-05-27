// frontend/src/components/features/interview/QuestionCard.jsx
import { cn } from '../../../utils/cn';
import { Badge } from '../../ui';
import { AIFeedbackCard } from './AIFeedbackCard';
import { AnswerMetricsPanel } from './AnswerMetricsPanel';
import { VoiceAnswerControls } from './VoiceAnswerControls';

// Renders a single question — handles MCQ and TEXT types
// Props:
//   question: { id, questionText, optionA-D, questionType, topic, difficulty }
//   selectedOption: 'A' | 'B' | 'C' | 'D' | null
//   onSelect: (option) => void
//   answerResult: { isCorrect, correctAnswer, explanation } after submission
//   isReviewing: bool — shows feedback state
//   isActive: bool — can user still select options?

export function QuestionCard({
  question,
  questionNumber,
  selectedOption,
  onSelect,
  answerResult,
  isReviewing,
  isActive,
  aiLoading,
}) {
  console.log('QuestionCard props -> answerResult =', answerResult, 'isReviewing =', isReviewing, 'isActive =', isActive);
  if (!question) return null;

  const answerText = question.questionType === 'TEXT' ? (selectedOption || '') : '';
  const difficultyColors = {
    EASY:   'badge-green',
    MEDIUM: 'badge-amber',
    HARD:   'badge-red',
  };

  return (
    <div className="card p-6 space-y-6 animate-fade-in">

      {/* ── Header ── */}
      <div className="flex items-start justify-between gap-4">
        <div className="flex items-center gap-2 flex-wrap">
          <Badge color={
            question.topic === 'DSA'   ? 'blue'   :
            question.topic === 'JAVA'  ? 'purple' :
            question.topic === 'SQL'   ? 'amber'  :
            question.topic === 'REACT' ? 'blue'   : 'gray'
          }>
            {question.topic}
          </Badge>
          <span className={cn('badge', difficultyColors[question.difficulty])}>
            {question.difficulty}
          </span>
          {question.questionType === 'TEXT' && (
            <Badge color="gray">Written Answer</Badge>
          )}
        </div>
        <span className="text-xs text-content-disabled flex-shrink-0">
          #{questionNumber}
        </span>
      </div>

      {/* ── Question Text ── */}
      <p className="text-content-primary text-base leading-relaxed font-medium">
        {question.questionText}
      </p>

      {/* ── MCQ Options ── */}
      {question.questionType === 'MCQ' && (
        <div className="grid grid-cols-1 gap-3">
          {['A', 'B', 'C', 'D'].map((letter) => {
            const optionText = question[`option${letter}`];
            if (!optionText) return null;

            const isSelected = selectedOption === letter;
            const isCorrect  = answerResult?.correctAnswer === letter;
            const isWrong    = isReviewing && isSelected && !isCorrect;
            const showCorrect = isReviewing && isCorrect;

            return (
              <OptionButton
                key={letter}
                letter={letter}
                text={optionText}
                isSelected={isSelected}
                isCorrect={showCorrect}
                isWrong={isWrong}
                isReviewing={isReviewing}
                disabled={!isActive || isReviewing}
                onClick={() => isActive && !isReviewing && onSelect(letter)}
              />
            );
          })}
        </div>
      )}

      {/* ── TEXT type: written answer area ── */}
      {question.questionType === 'TEXT' && (
        <div className="space-y-4">
          <div className="space-y-2">
            <label className="section-label">Your Answer</label>
            <textarea
              rows={6}
              value={answerText}
              placeholder="Type your answer here..."
              disabled={isReviewing}
              onChange={(e) => onSelect(e.target.value)}
              className={cn(
                'input-base resize-none min-h-[160px] transition-colors',
                isReviewing && 'opacity-60 cursor-not-allowed'
              )}
            />
          </div>

          <AnswerMetricsPanel answer={answerText} />
          <VoiceAnswerControls onTranscript={(value) => onSelect(value)} />
        </div>
      )}

      {/* ── Feedback panel (shown after submitting) ── */}
      {isReviewing && answerResult && (
        question.questionType === 'TEXT'
          ? <AIFeedbackCard evaluation={answerResult.evaluation} loading={aiLoading} />
          : <FeedbackPanel answerResult={answerResult} questionType={question.questionType} />
      )}
    </div>
  );
}

// ── Individual MCQ option button ──
function OptionButton({ letter, text, isSelected, isCorrect, isWrong, isReviewing, disabled, onClick }) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      className={cn(
        'w-full text-left px-4 py-3.5 rounded-lg border',
        'flex items-start gap-3 transition-all duration-200',
        'focus:outline-none focus-visible:ring-2 focus-visible:ring-brand-500',

        // Default (not selected, not reviewing)
        !isSelected && !isReviewing &&
          'border-border-default bg-surface-4 hover:border-border-strong hover:bg-surface-5 cursor-pointer',

        // Selected but not yet reviewed
        isSelected && !isReviewing &&
          'border-brand-500 bg-brand-500/10 text-content-primary',

        // Correct answer (revealed during review)
        isCorrect &&
          'border-status-success/50 bg-status-success/10 text-status-success',

        // Wrong answer (what user selected that was wrong)
        isWrong &&
          'border-status-error/50 bg-status-error/10 text-status-error',

        // Other options during review (not selected, not the answer)
        isReviewing && !isSelected && !isCorrect &&
          'border-border-subtle bg-surface-3 opacity-50 cursor-not-allowed',

        disabled && !isReviewing && 'cursor-not-allowed'
      )}
    >
      {/* Letter bubble */}
      <span className={cn(
        'flex-shrink-0 w-6 h-6 rounded-md flex items-center justify-center',
        'text-xs font-bold transition-colors',
        isCorrect ? 'bg-status-success/20 text-status-success' :
        isWrong   ? 'bg-status-error/20 text-status-error' :
        isSelected ? 'bg-brand-500/20 text-brand-400' :
                     'bg-surface-5 text-content-muted'
      )}>
        {letter}
      </span>

      {/* Option text */}
      <span className="text-sm leading-relaxed">{text}</span>

      {/* Checkmark/cross icon during review */}
      {isCorrect && <span className="ml-auto text-status-success flex-shrink-0">✓</span>}
      {isWrong   && <span className="ml-auto text-status-error flex-shrink-0">✗</span>}
    </button>
  );
}

// ── Feedback shown after submitting ──
function FeedbackPanel({ answerResult, questionType }) {
  const isCorrect = answerResult?.isCorrect;
  console.log('FeedbackPanel props -> answerResult =', answerResult, 'isCorrect =', isCorrect);

  return (
    <div className={cn(
      'rounded-lg border p-4 space-y-2 animate-fade-in',
      isCorrect || questionType === 'TEXT'
        ? 'border-status-success/30 bg-status-success/5'
        : 'border-status-error/30 bg-status-error/5'
    )}>
      {/* Result banner */}
      <div className="flex items-center gap-2">
        <span className="text-lg">
          {questionType === 'TEXT' ? '📝' : isCorrect ? '✅' : '❌'}
        </span>
        <span className={cn(
          'font-semibold text-sm',
          questionType === 'TEXT' ? 'text-content-secondary' :
          isCorrect ? 'text-status-success' : 'text-status-error'
        )}>
          {questionType === 'TEXT'
            ? 'Answer recorded — AI evaluation in Phase 5'
            : isCorrect ? 'Correct!' : 'Incorrect'}
        </span>
      </div>

      {/* Explanation */}
      {answerResult.explanation && (
        <div className="pt-2 border-t border-border-subtle">
          <p className="text-xs text-content-muted mb-1 font-medium uppercase tracking-wide">
            Explanation
          </p>
          <p className="text-sm text-content-secondary leading-relaxed">
            {answerResult.explanation}
          </p>
        </div>
      )}
    </div>
  );
}