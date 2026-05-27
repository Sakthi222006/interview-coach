// frontend/src/services/userService.js
// This will grow in later phases (interview history, profile, etc.)

import axiosInstance from '../api/axiosInstance';

// ── Get current user's profile ──
// This is a PROTECTED route — axiosInstance auto-adds the JWT token
export async function getUserProfile() {
  try {
    const response = await axiosInstance.get('/api/user/profile');
    return { success: true, data: response.data };
  } catch (error) {
    return { success: false, message: 'Failed to load profile' };
  }
}

// More functions will be added here in Phase 6
// - getInterviewHistory()
// - getAnalytics()
// - updateProfile()