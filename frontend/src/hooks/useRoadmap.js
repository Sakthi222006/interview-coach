import { useCallback, useEffect, useState } from 'react';
import { getRoadmap, generateRoadmap as generateRoadmapRequest } from '../services/roadmapService';

const CACHE_KEY = 'interviewCoach.roadmap.cache';

export function useRoadmap() {
  const [roadmap, setRoadmap] = useState(() => {
    try {
      const cached = sessionStorage.getItem(CACHE_KEY);
      return cached ? JSON.parse(cached) : null;
    } catch {
      return null;
    }
  });
  const [loading, setLoading] = useState(!roadmap);
  const [error, setError] = useState(null);
  const [generating, setGenerating] = useState(false);
  const [generateError, setGenerateError] = useState(null);

  const saveCache = useCallback((value) => {
    setRoadmap(value);
    try {
      if (value) sessionStorage.setItem(CACHE_KEY, JSON.stringify(value));
      else sessionStorage.removeItem(CACHE_KEY);
    } catch {
      // ignore storage failures
    }
  }, []);

  const fetchRoadmap = useCallback(async (force = false) => {
    if (!force && roadmap) return roadmap;
    setLoading(true);
    setError(null);
    const result = await getRoadmap();
    if (result.success) {
      saveCache(result.data);
      setLoading(false);
      return result.data;
    }
    setError(result.message || 'Failed to load roadmap');
    setLoading(false);
    return null;
  }, [roadmap, saveCache]);

  const generateRoadmap = useCallback(async () => {
    setGenerating(true);
    setGenerateError(null);
    const result = await generateRoadmapRequest();
    if (result.success) {
      saveCache(result.data);
    } else {
      setGenerateError(result.message || 'Failed to generate roadmap');
    }
    setGenerating(false);
    return result;
  }, [saveCache]);

  useEffect(() => {
    if (!roadmap) {
      fetchRoadmap();
    }
  }, [fetchRoadmap, roadmap]);

  return {
    roadmap,
    loading,
    error,
    generating,
    generateError,
    refresh: () => fetchRoadmap(true),
    generateRoadmap,
  };
}
