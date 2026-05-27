import axiosInstance from '../api/axiosInstance';

const handleResponse = async (request) => {
  try {
    const response = await request;
    return {
      success: true,
      data: response?.data?.data || null,
      message: response?.data?.message || 'Success',
    };
  } catch (error) {
    return {
      success: false,
      data: null,
      message: error?.response?.data?.message || error?.message || 'Request failed',
    };
  }
};

export const getCompanies = () => handleResponse(axiosInstance.get('/api/company/profiles'));
export const getCompanyProfile = (companyName) =>
  handleResponse(axiosInstance.get('/api/company/profile', { params: { companyName } }));
export const getCompanyQuestions = (companyName, category, difficulty, limit = 10) => {
  const params = { companyName, limit };
  if (category) params.category = category;
  if (difficulty) params.difficulty = difficulty;
  return handleResponse(axiosInstance.get('/api/company/questions', { params }));
};
export const getCodingChallenges = (companyName, difficulty, topic) => {
  const params = { companyName };
  if (difficulty) params.difficulty = difficulty;
  if (topic) params.topic = topic;
  return handleResponse(axiosInstance.get('/api/company/coding', { params }));
};
export const submitCodingSolution = (challengeId, code) =>
  handleResponse(axiosInstance.post(`/api/company/coding/${challengeId}/submit`, { code }));
export const getCompanyReadiness = (companyName) =>
  handleResponse(axiosInstance.get('/api/company/readiness', { params: { companyName } }));
export const getCompanyMockInterview = (companyId) =>
  handleResponse(axiosInstance.get('/api/company/mock-interview', { params: { companyId } }));

// Legacy aliases for compatibility
export const getCompanyProfiles = getCompanies;
export const getCompanyCodingChallengesById = (companyId, difficulty, topic) => {
  const params = { companyId };
  if (difficulty) params.difficulty = difficulty;
  if (topic) params.topic = topic;
  return handleResponse(axiosInstance.get('/api/company/coding', { params }));
};
export const getCompanyQuestionsById = (companyId, category, difficulty, limit = 10) => {
  const params = { companyId, limit };
  if (category) params.category = category;
  if (difficulty) params.difficulty = difficulty;
  return handleResponse(axiosInstance.get('/api/company/questions', { params }));
};

export const getCompanyCodingChallenges = getCodingChallenges;
