import axiosInstance from '../api/axiosInstance';

const endpoint = '/api/voice/interview';

export async function startVoiceInterview(data) {
  const response = await axiosInstance.post(`${endpoint}/start`, data);
  return response.data;
}

export async function submitVoiceTranscript(sessionId, data) {
  const response = await axiosInstance.post(`${endpoint}/${sessionId}/transcript`, data);
  return response.data;
}

export async function stopVoiceInterview(sessionId) {
  const response = await axiosInstance.post(`${endpoint}/${sessionId}/stop`);
  return response.data;
}

export async function getVoiceInterviewSession(sessionId) {
  const response = await axiosInstance.get(`${endpoint}/${sessionId}`);
  return response.data;
}
