import { useCallback, useState } from 'react';
import { uploadResume, analyzeResume, getResumeAnalysis, getResumeHistory } from '../services/resumeService';

export function useResumeAnalysis() {
  const [resumeId, setResumeId] = useState('');
  const [analysis, setAnalysis] = useState(null);
  const [history, setHistory] = useState([]);
  const [preview, setPreview] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const clear = useCallback(() => {
    setAnalysis(null);
    setHistory([]);
    setPreview(null);
    setError(null);
    setLoading(false);
  }, []);

  const loadAnalysis = useCallback(async (id) => {
    setLoading(true);
    setError(null);
    try {
      const result = await getResumeAnalysis(id);
      if (result?.success) {
        setAnalysis(result.data);
      } else {
        setError(result?.message || 'Failed to load resume analysis');
      }
    } catch (e) {
      setError(e?.message || 'Failed to load resume analysis');
    } finally {
      setLoading(false);
    }
  }, []);

  const loadHistory = useCallback(async (id) => {
    setLoading(true);
    setError(null);
    try {
      const result = await getResumeHistory(id);
      if (result?.success) {
        setHistory(result.data.history || []);
      } else {
        setError(result?.message || 'Failed to load resume history');
      }
    } catch (e) {
      setError(e?.message || 'Failed to load resume history');
    } finally {
      setLoading(false);
    }
  }, []);

  const submitUpload = useCallback(async (file) => {
    setLoading(true);
    setError(null);
    try {
      const result = await uploadResume(file);
      if (result?.success) {
        setResumeId(result.data.resumeId);
        setPreview({ filename: result.data.filename, uploadedAt: result.data.uploadedAt, previewText: result.data.previewText });
      } else {
        setError(result?.message || 'Upload failed');
      }
      return result;
    } catch (e) {
      setError(e?.message || 'Upload failed');
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  const runAnalysis = useCallback(async (id) => {
    setLoading(true);
    setError(null);
    try {
      const result = await analyzeResume(id);
      if (result?.success) {
        setAnalysis(result.data);
      } else {
        setError(result?.message || 'Analyze request failed');
      }
      return result;
    } catch (e) {
      setError(e?.message || 'Analyze request failed');
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  const refresh = useCallback(async () => {
    if (!resumeId) return;
    await Promise.all([loadAnalysis(resumeId), loadHistory(resumeId)]);
  }, [resumeId, loadAnalysis, loadHistory]);

  return {
    resumeId,
    setResumeId,
    analysis,
    history,
    preview,
    loading,
    error,
    submitUpload,
    runAnalysis,
    loadAnalysis,
    loadHistory,
    refresh,
    clear,
  };
}
