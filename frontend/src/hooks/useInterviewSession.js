// frontend/src/hooks/useInterviewSession.js
import { useState, useCallback, useRef } from 'react';
import { submitAnswer, completeSession } from '../services/sessionService';

// Session states (like a state machine):
// IDLE → ACTIVE → REVIEWING → COMPLETED
//
// IDLE:      no session started yet
// ACTIVE:    question is showing, user hasn't answered yet
// REVIEWING: user answered, showing feedback before moving on
// COMPLETED: all questions done, session finished

export function useInterviewSession() {
  const [session,         setSession]         = useState(null);   // session metadata
  const [questions,       setQuestions]       = useState([]);     // all questions
  const [currentIndex,    setCurrentIndex]    = useState(0);      // which question
  const [answers,         setAnswers]         = useState({});     // { questionId: AnswerResponse }
  const [selectedOption,  setSelectedOption]  = useState(null);   // what user selected
  const [sessionState,    setSessionState]    = useState('IDLE'); // state machine
  const [submitting,      setSubmitting]      = useState(false);  // API loading
  const [aiLoading,       setAiLoading]       = useState(false);  // AI evaluation loading
  const [error,           setError]           = useState(null);
  const [finalResult,     setFinalResult]     = useState(null);   // after completion

  // Track time spent on current question
  const questionStartTime = useRef(Date.now());

  // Called from InterviewSetupPage after createSession() succeeds
  const startSession = useCallback((sessionData, sessionQuestions) => {
    setSession(sessionData);
    setQuestions(sessionQuestions);
    setCurrentIndex(0);
    setAnswers({});
    setSelectedOption(null);
    setSessionState('ACTIVE');
    setError(null);
    questionStartTime.current = Date.now();
  }, []);

  // Called when user selects an MCQ option (doesn't submit yet)
  const selectOption = useCallback((option) => {
    if (sessionState !== 'ACTIVE') return; // can't change after submitting
    setSelectedOption(option);
  }, [sessionState]);

  // Called when user clicks "Submit Answer"
  const submitCurrentAnswer = useCallback(async () => {
  if (!selectedOption && questions[currentIndex]?.questionType === 'MCQ') return;
  if (submitting) return;

  const question = questions[currentIndex];
  const timeSpentMs = Date.now() - questionStartTime.current;
  const timeSpentSecs = Math.round(timeSpentMs / 1000);

  if (question?.questionType === 'TEXT') {
    setAiLoading(true);
  }

  setSubmitting(true);
  setError(null);

  const result = await submitAnswer(
    session.id,
    question.id,
    selectedOption,
    timeSpentSecs
  );

  setSubmitting(false);
  setAiLoading(false);

  if (result.success) {
  console.log("result =", result);
  console.log("result.data =", result.data);

  setAnswers(prev => {
    const next = {
      ...prev,
      [question.id]: result.data,
    };
    console.log("setAnswers -> storing for questionId", question.id, ":", result.data);
    console.log("setAnswers -> next answers state =", next);
    return next;
  });

  setSessionState('REVIEWING');
}
   else {
    setError(result.message);
  }
}, [selectedOption, questions, currentIndex, session, submitting]);

  // Called when user clicks "Next Question"
  const nextQuestion = useCallback(() => {
    if (currentIndex < questions.length - 1) {
      setCurrentIndex(prev => prev + 1);
      setSelectedOption(null);
      setSessionState('ACTIVE');
      questionStartTime.current = Date.now();
    }
  }, [currentIndex, questions.length]);

  // Called when user clicks "Finish Interview"
  const finishSession = useCallback(async (durationSeconds) => {
    if (!session) return;

    const result = await completeSession(session.id, durationSeconds);

    if (result.success) {
      setFinalResult(result.data);
      setSessionState('COMPLETED');
    } else {
      setError(result.message);
    }
  }, [session]);

  // Computed values
  const currentQuestion   = questions[currentIndex] || null;
  const isLastQuestion    = currentIndex === questions.length - 1;
  const currentAnswer     = currentQuestion ? answers[currentQuestion.id] : null;
  const answeredCount     = Object.keys(answers).length;
  const progressPercent   = questions.length > 0
    ? Math.round((answeredCount / questions.length) * 100)
    : 0;

  const correctCount = Object.values(answers)
    .filter((a) => a?.isCorrect === true)
    .length;

  const totalResponseSeconds = Object.values(answers)
    .reduce((sum, a) => sum + (a?.timeSpentSeconds || 0), 0);

  const averageResponseTime = answeredCount > 0
    ? Math.round(totalResponseSeconds / answeredCount)
    : 0;

  const accuracyPercent = answeredCount > 0
    ? Math.round((correctCount / answeredCount) * 100)
    : 0;

  const xpEarned = Object.values(answers).reduce((sum, a) => {
    const base = a?.isCorrect ? 15 : 8;
    const confidenceBonus = a?.confidenceScore ? Math.round((a.confidenceScore / 100) * 5) : 0;
    return sum + base + confidenceBonus;
  }, 0);

  // Debug: log shapes each render
  console.log('useInterviewSession render -> answers =', answers);
  console.log('useInterviewSession render -> currentAnswer =', currentAnswer);
  return {
    // State
    session,
    questions,
    currentIndex,
    answers,
    selectedOption,
    sessionState,
    submitting,
    aiLoading,
    error,
    finalResult,

    // Computed
    currentQuestion,
    isLastQuestion,
    currentAnswer,
    answeredCount,
    progressPercent,
    correctCount,
    accuracyPercent,
    averageResponseTime,
    xpEarned,

    // Actions
    startSession,
    selectOption,
    submitCurrentAnswer,
    nextQuestion,
    finishSession,
  };
}