import axiosInstance from '../api/axiosInstance';

const get = async (url, params = {}) => {
  try {
    const response = await axiosInstance.get(url, { params });
    return { success: true, data: response?.data?.data ?? null, message: response?.data?.message ?? '' };
  } catch (error) {
    console.error(`Analytics fetch failed [${url}]:`, error?.message || error);
    return { success: false, data: null, message: error?.response?.data?.message || error?.message || 'Request failed' };
  }
};

export const getTrend = () => get('/api/analytics/trend');
export const getTopicAnalysis = () => get('/api/analytics/topics');
export const getSkillRadar = () => get('/api/analytics/radar');
export const getRoadmap = () => get('/api/analytics/roadmap');
export const getAdaptiveRec = (topic) => get('/api/analytics/adaptive', { topic });
export const getSessionReview = (id) => get(`/api/analytics/sessions/${id}/review`);
