import axiosInstance from '../api/axiosInstance';

const endpoint = '/api/resume';

export async function uploadResume(file) {
  const body = new FormData();
  body.append('file', file);

  const response = await axiosInstance.post(`${endpoint}/upload`, body, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return response.data;
}

export async function analyzeResume(resumeId) {
  const response = await axiosInstance.post(`${endpoint}/${resumeId}/analyze`);
  return response.data;
}

export async function getResumeAnalysis(resumeId) {
  const response = await axiosInstance.get(`${endpoint}/${resumeId}/analysis`);
  return response.data;
}

export async function getResumeHistory(resumeId) {
  const response = await axiosInstance.get(`${endpoint}/${resumeId}/history`);
  return response.data;
}

export async function matchResume({ resumeId, targetRole, jobDescription }) {
  const response = await axiosInstance.post(`${endpoint}/${resumeId}/match`, {
    targetRole,
    jobDescription,
  });
  return response.data;
}

export async function getReadiness(resumeId, targetRole) {
  const params = targetRole ? { targetRole } : {};
  const response = await axiosInstance.get(`${endpoint}/${resumeId}/readiness`, { params });
  return response.data;
}
