// frontend/src/services/sessionService.js
import axiosInstance from '../api/axiosInstance';

// ── Create a new interview session ──
// Sends: { topic, difficulty, totalQuestions }
// Gets back: { id, topic, difficulty, questions[], status, ... }
export async function createSession(topic, difficulty, totalQuestions) {
  try {
    const response = await axiosInstance.post('/api/sessions', {
      topic,
      difficulty,
      totalQuestions,
    });
    return { success: true, data: response.data.data };
  } catch (error) {
    return {
      success: false,
      message: error.response?.data?.message || 'Failed to create session',
    };
  }
}

// ── Submit an answer for a question ──
export async function submitAnswer(sessionId, questionId, userAnswer, timeSpentSeconds) {
  try {
    const response = await axiosInstance.post(`/api/sessions/${sessionId}/answers`, {
      questionId,
      userAnswer,
      timeSpentSeconds,
    });
   console.log("Submit Answer Response:", response.data);
   console.log("submitAnswer() -> returning:", {
     success: response.data.success,
     data: response.data.data,
     message: response.data.message,
   });

return {
  success: response.data.success,
  data: response.data.data,
  message: response.data.message
};
    
  } catch (error) {
    return {
      success: false,
      message: error.response?.data?.message || 'Failed to submit answer',
    };
  }
}

// ── Complete a session ──
export async function completeSession(sessionId, durationSeconds) {
  try {
    const response = await axiosInstance.put(`/api/sessions/${sessionId}/complete`, {
      durationSeconds,
    });
    return { success: true, data: response.data.data };
  } catch (error) {
    return {
      success: false,
      message: error.response?.data?.message || 'Failed to complete session',
    };
  }
}

// ── Get all sessions for current user ──
export async function getUserSessions() {
  try {
    const response = await axiosInstance.get('/api/sessions');
    return { success: true, data: response.data.data };
  } catch (error) {
    return { success: false, message: 'Failed to load history', data: [] };
  }
}

// ── Get single session by ID ──
export async function getSessionById(sessionId) {
  try {
    const response = await axiosInstance.get(`/api/sessions/${sessionId}`);
    return { success: true, data: response.data.data };
  } catch (error) {
    return { success: false, message: 'Session not found' };
  }
}

// ── Get analytics summary ──
export async function getAnalyticsSummary() {
  try {
    const response = await axiosInstance.get('/api/sessions/analytics/summary');
    return { success: true, data: response.data.data };
  } catch (error) {
    return { success: false, data: null };
  }
}