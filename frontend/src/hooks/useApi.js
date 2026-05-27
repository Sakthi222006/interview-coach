// frontend/src/hooks/useApi.js

/**
 * useApi — generic hook for async API calls with loading/error state
 *
 * WHY: In every component that calls an API, you end up writing:
 *   const [data, setData] = useState(null);
 *   const [loading, setLoading] = useState(false);
 *   const [error, setError] = useState('');
 *   ...and the same try/catch/finally pattern
 *
 * useApi() extracts that pattern into one reusable hook.
 *
 * USAGE:
 *   const { data, loading, error, execute } = useApi(getInterviewHistory);
 *
 *   useEffect(() => { execute(); }, []);
 *   // OR
 *   <Button onClick={() => execute(params)}>Load Data</Button>
 */

import { useState, useCallback } from 'react';

export function useApi(apiFunction) {
  const [data,    setData]    = useState(null);
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState(null);

  const execute = useCallback(async (...args) => {
    setLoading(true);
    setError(null);

    try {
      const result = await apiFunction(...args);

      if (result.success) {
        setData(result.data);
        return result;
      } else {
        setError(result.message || 'An error occurred');
        return result;
      }
    } catch (err) {
      const message = err?.message || 'Unexpected error';
      setError(message);
      return { success: false, message };
    } finally {
      setLoading(false);
    }
  }, [apiFunction]);

  // reset() — clears state back to initial (useful before re-fetching)
  const reset = useCallback(() => {
    setData(null);
    setError(null);
    setLoading(false);
  }, []);

  return { data, loading, error, execute, reset };
}