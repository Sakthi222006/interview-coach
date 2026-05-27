import { useCallback, useState } from 'react';
import {
  startVoiceInterview,
  submitVoiceTranscript,
  stopVoiceInterview,
  getVoiceInterviewSession,
} from '../services/voiceService';

export function useVoiceInterview() {
  const [session, setSession] = useState(null);
  const [answers, setAnswers] = useState([]);
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const startSession = useCallback(async (data) => {
    setLoading(true);
    setError(null);
    try {
      const result = await startVoiceInterview(data);
      if (result?.success) {
        const sessionData = result.data;
        setSession(sessionData);
        return sessionData;
      }
      setError(result?.message || 'Unable to start voice interview');
      return null;
    } catch (err) {
      setError(err?.message || 'Unable to start voice interview');
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  const submitTranscript = useCallback(async (sessionId, payload) => {
    setLoading(true);
    setError(null);
    try {
      const result = await submitVoiceTranscript(sessionId, payload);
      if (result?.success) {
        setAnswers((prev) => [...prev, result.data]);
        return result.data;
      }
      setError(result?.message || 'Unable to evaluate transcript');
      return null;
    } catch (err) {
      setError(err?.message || 'Unable to evaluate transcript');
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  const stopSession = useCallback(async (sessionId) => {
    setLoading(true);
    setError(null);
    try {
      const result = await stopVoiceInterview(sessionId);
      if (result?.success) {
        setSession(result.data);
        return result.data;
      }
      setError(result?.message || 'Unable to stop session');
      return null;
    } catch (err) {
      setError(err?.message || 'Unable to stop session');
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  const loadSession = useCallback(async (sessionId) => {
    setLoading(true);
    setError(null);
    try {
      const result = await getVoiceInterviewSession(sessionId);
      if (result?.success) {
        setSummary(result.data);
        return result.data;
      }
      setError(result?.message || 'Unable to load voice interview session');
      return null;
    } catch (err) {
      setError(err?.message || 'Unable to load voice interview session');
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    session,
    answers,
    summary,
    loading,
    error,
    startSession,
    submitTranscript,
    stopSession,
    loadSession,
  };
}
