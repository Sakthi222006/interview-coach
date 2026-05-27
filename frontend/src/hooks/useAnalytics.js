import { useState, useEffect, useCallback } from 'react';
import { getTrend, getTopicAnalysis, getSkillRadar, getRoadmap, getAdaptiveRec, getSessionReview } from '../services/analyticsService';

export function useAnalytics() {
  const [trend, setTrend] = useState(null);
  const [topics, setTopics] = useState(null);
  const [radar, setRadar] = useState(null);
  const [roadmap, setRoadmap] = useState(null);
  const [adaptive, setAdaptive] = useState(null);
  const [sessionReview, setSessionReview] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchAll = useCallback(async () => {
    setLoading(true);
    setError(null);
    setAdaptive(null);
    setSessionReview(null);

    try {
      const [t, tp, r, rm] = await Promise.all([
        getTrend(), getTopicAnalysis(), getSkillRadar(), getRoadmap(),
      ]);

      if (t?.success) setTrend(t.data ?? null); else setError(t?.message || 'Failed to load trend');
      if (tp?.success) setTopics(tp.data ?? null); else setError(prev => prev || tp?.message || 'Failed to load topics');
      if (r?.success) setRadar(r.data ?? null); else setError(prev => prev || r?.message || 'Failed to load radar');
      if (rm?.success) setRoadmap(rm.data ?? null); else setError(prev => prev || rm?.message || 'Failed to load roadmap');

      const weakTopic = tp?.success ? tp.data?.weakTopics?.[0] : null;
      const sessionId = t?.success && t.data?.dataPoints?.length ? t.data.dataPoints[t.data.dataPoints.length - 1]?.sessionId || t.data.dataPoints[0]?.sessionId : null;

      if (weakTopic) {
        const adaptiveResult = await getAdaptiveRec(weakTopic);
        if (adaptiveResult?.success) {
          setAdaptive(adaptiveResult.data ?? null);
        }
      }

      if (sessionId) {
        const reviewResult = await getSessionReview(sessionId);
        if (reviewResult?.success) {
          setSessionReview(reviewResult.data ?? null);
        }
      }
    } catch (e) {
      setError(e?.message || 'Failed to load analytics');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchAll(); }, [fetchAll]);

  return { trend, topics, radar, roadmap, adaptive, sessionReview, loading, error, refresh: fetchAll };
}
