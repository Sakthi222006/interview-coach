// frontend/src/hooks/useRecruiterMode.js
import { useState, useCallback, useRef, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';

/**
 * useRecruiterMode — manages recruiter interview simulation flow
 * 
 * States:
 * IDLE -> LOADING -> IN_PROGRESS -> COMPLETED
 */
export function useRecruiterMode() {
  const [scenario, setScenario] = useState(null);
  const [conversations, setConversations] = useState([]);
  const [currentQuestion, setCurrentQuestion] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [sessionState, setSessionState] = useState('IDLE'); // IDLE, IN_PROGRESS, COMPLETED
  const [summary, setSummary] = useState(null);
  const [followUps, setFollowUps] = useState([]);
  
  const sessionStartTime = useRef(Date.now());

  // Create a new recruiter scenario
  const createScenario = useCallback(async (recruiterType, roundType, title, context, jobDesc) => {
    setIsLoading(true);
    setError(null);
    
    try {
      const response = await axiosInstance.post('/api/recruiter/scenarios', {
        recruiterType,
        roundType,
        title,
        scenarioContext: context,
        jobDescription: jobDesc,
        totalRounds: 5,
      });
      
      setScenario(response.data.data);
      setSessionState('IN_PROGRESS');
      sessionStartTime.current = Date.now();
      
      // Fetch the first question
      await fetchNextQuestion(response.data.data.id);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create scenario');
    } finally {
      setIsLoading(false);
    }
  }, []);

  // Fetch next question from backend
  const fetchNextQuestion = useCallback(async (scenarioId) => {
    try {
      const response = await axiosInstance.get(
        `/api/recruiter/scenarios/${scenarioId}/next-question`
      );
      setCurrentQuestion(response.data.data.question);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch question');
    }
  }, []);

  // Submit candidate answer
  const submitAnswer = useCallback(async (scenarioId, answer, confidenceLevel = 5) => {
    if (isSubmitting) return;
    setIsSubmitting(true);
    setError(null);

    try {
      const response = await axiosInstance.post(
        `/api/recruiter/scenarios/${scenarioId}/submit-answer`,
        {
          scenarioId,
          answer,
          confidenceLevel,
        }
      );

      const conversation = response.data.data;
      setConversations(prev => [...prev, conversation]);
      setCurrentQuestion(null);

      // Check if interview is complete
      if (scenario && scenario.currentRoundIndex >= scenario.totalRounds) {
        await completeScenario(scenarioId);
      } else {
        await fetchNextQuestion(scenarioId);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit answer');
    } finally {
      setIsSubmitting(false);
    }
  }, [scenario, isSubmitting]);

  // Complete the interview scenario
  const completeScenario = useCallback(async (scenarioId) => {
    try {
      const duration = Math.floor((Date.now() - sessionStartTime.current) / 1000);
      
      const response = await axiosInstance.put(
        `/api/recruiter/scenarios/${scenarioId}/complete`,
        { durationSeconds: duration }
      );
      
      setScenario(response.data.data);
      setSessionState('COMPLETED');
      
      // Fetch summary
      await fetchSummary(scenarioId);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to complete scenario');
    }
  }, []);

  // Fetch interview summary
  const fetchSummary = useCallback(async (scenarioId) => {
    try {
      const response = await axiosInstance.get(
        `/api/recruiter/scenarios/${scenarioId}/summary`
      );
      setSummary(response.data.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch summary');
    }
  }, []);

  // Fetch conversation history
  const fetchConversationHistory = useCallback(async (scenarioId) => {
    try {
      const response = await axiosInstance.get(
        `/api/recruiter/scenarios/${scenarioId}/conversation`
      );
      setConversations(response.data.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch conversation');
    }
  }, []);

  // Fetch follow-up questions
  const fetchFollowUps = useCallback(async (scenarioId) => {
    try {
      const response = await axiosInstance.get(
        `/api/recruiter/scenarios/${scenarioId}/follow-ups`
      );
      setFollowUps(response.data.data);
    } catch (err) {
      // Don't error on follow-ups as they might not exist yet
      setFollowUps([]);
    }
  }, []);

  // Load scenario from ID (for continuing an interview)
  const loadScenario = useCallback(async (scenarioId) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await axiosInstance.get(`/api/recruiter/scenarios/${scenarioId}`);
      setScenario(response.data.data);
      
      if (response.data.data.status === 'IN_PROGRESS') {
        setSessionState('IN_PROGRESS');
        await fetchConversationHistory(scenarioId);
        await fetchFollowUps(scenarioId);
        if (response.data.data.currentRoundIndex < response.data.data.totalRounds) {
          await fetchNextQuestion(scenarioId);
        }
      } else if (response.data.data.status === 'COMPLETED') {
        setSessionState('COMPLETED');
        await fetchConversationHistory(scenarioId);
        await fetchSummary(scenarioId);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load scenario');
    } finally {
      setIsLoading(false);
    }
  }, []);

  return {
    scenario,
    conversations,
    currentQuestion,
    isLoading,
    isSubmitting,
    error,
    sessionState,
    summary,
    followUps,
    createScenario,
    submitAnswer,
    completeScenario,
    fetchConversationHistory,
    fetchFollowUps,
    loadScenario,
  };
}
