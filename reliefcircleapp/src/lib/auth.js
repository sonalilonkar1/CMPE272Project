import { getSession } from "next-auth/react";
import axios from "axios";

// Set up axios interceptor to include auth token in requests
export const setupAxiosInterceptors = () => {
  axios.interceptors.request.use(
    async (config) => {
      try {
        const session = await getSession();
        if (session?.accessToken) {
          config.headers.Authorization = `Bearer ${session.accessToken}`;
        }
        return config;
      } catch (error) {
        return Promise.reject(error);
      }
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  // Handle 401 responses and redirect to login
  axios.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401) {
        if (typeof window !== 'undefined') {
          // Redirect to login page
          window.location.href = '/login?error=session-expired';
        }
      }
      return Promise.reject(error);
    }
  );
};

// Custom hook for authorized fetch with token (use in API calls)
export const fetchWithAuth = async (url, options = {}) => {
  const session = await getSession();
  if (!session?.accessToken) {
    throw new Error('Not authenticated');
  }

  const headers = {
    Authorization: `Bearer ${session.accessToken}`,
    ...options.headers,
  };

  return fetch(url, {
    ...options,
    headers,
  });
};

// Check if user is authenticated and has required role
export const checkUserRole = async (requiredRole) => {
  const session = await getSession();
  if (!session) return false;
  
  // If no specific role required, just check authentication
  if (!requiredRole) return true;
  
  // Check if user has the required role
  return session.user.role === requiredRole;
}; 