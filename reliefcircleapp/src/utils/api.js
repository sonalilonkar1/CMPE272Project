// Base API URL
export const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

// Auth endpoints
export const AUTH_ENDPOINTS = {
  LOGIN: `${API_URL}/auth/login`,
  REGISTER: `${API_URL}/auth/register`,
  REFRESH_TOKEN: `${API_URL}/auth/refresh-token`,
  LOGOUT: `${API_URL}/auth/logout`,
};

// Charity endpoints
export const CHARITY_ENDPOINTS = {
  LIST: `${API_URL}/charities`,
  DETAIL: (id) => `${API_URL}/charities/${id}`,
  CREATE: `${API_URL}/charities`,
  UPDATE: (id) => `${API_URL}/charities/${id}`,
  DELETE: (id) => `${API_URL}/charities/${id}`,
  FUNDRAISER_CHARITIES: (fundraiserId) => `${API_URL}/charities?fundraiserId=${fundraiserId}`,
  VERIFY: (id) => `${API_URL}/charities/${id}/verify`,
  REJECT: (id) => `${API_URL}/charities/${id}/reject`,
};

// Donation endpoints
export const DONATION_ENDPOINTS = {
  LIST: `${API_URL}/donations`,
  CREATE: `${API_URL}/donations`,
  DETAIL: (id) => `${API_URL}/donations/${id}`,
  USER_DONATIONS: (userId) => `${API_URL}/donations?userId=${userId}`,
  CHARITY_DONATIONS: (charityId) => `${API_URL}/donations?charityId=${charityId}`,
};

// User endpoints
export const USER_ENDPOINTS = {
  PROFILE: `${API_URL}/users/me`,
  UPDATE_PROFILE: `${API_URL}/users/profile`,
  CHANGE_PASSWORD: `${API_URL}/users/change-password`,
  UPLOAD_AVATAR: `${API_URL}/users/avatar`,
};

