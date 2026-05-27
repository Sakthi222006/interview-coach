import axiosInstance from '../api/axiosInstance';

const handleResponse = async (request) => {
  try {
    const response = await request;
    const data = response?.data?.data ?? response?.data ?? null;
    return {
      success: true,
      data,
      message: response?.data?.message || 'Success',
    };
  } catch (error) {
    console.error('Roadmap request failed:', error?.message || error);
    return {
      success: false,
      data: null,
      message: error?.response?.data?.message || error?.message || 'Request failed',
    };
  }
};

export const getRoadmap = () => handleResponse(axiosInstance.get('/api/analytics/roadmap'));

export const generateRoadmap = (requestBody = null) => {
  if (requestBody && Object.keys(requestBody).length > 0) {
    return handleResponse(axiosInstance.post('/api/roadmap/generate', requestBody));
  }
  return handleResponse(axiosInstance.get('/api/analytics/roadmap'));
};
