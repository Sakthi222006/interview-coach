import { useCallback, useState } from 'react';
import { getReadiness } from '../services/resumeService';

export function useResumeReadiness() {
  const [readiness, setReadiness] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [resumeId, setResumeId] = useState('');
  const [targetRole, setTargetRole] = useState('');

  const loadReadiness = useCallback(async (id, role) => {
    setLoading(true);
    setError(null);
    try {
      const result = await getReadiness(id, role);
      if (result?.success) {
        setReadiness(result.data);
      } else {
        setError(result?.message || 'Failed to load readiness');
      }
      return result;
    } catch (e) {
      setError(e?.message || 'Failed to load readiness');
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  const refresh = useCallback(async () => {
    if (!resumeId) return;
    return loadReadiness(resumeId, targetRole);
  }, [resumeId, targetRole, loadReadiness]);

  return {
    readiness,
    loading,
    error,
    resumeId,
    targetRole,
    setResumeId,
    setTargetRole,
    loadReadiness,
    refresh,
  };
}
