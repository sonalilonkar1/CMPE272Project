'use client';

import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchUserProfile } from '@/redux/features/userSlice';
import { useAuth } from '@/app/hooks/useAuth';

export default function UserProfileProvider({ children }) {
  const dispatch = useDispatch();
  const { isAuthenticated } = useAuth();
  const { loading, error } = useSelector((state) => state.user);

  useEffect(() => {
    if (isAuthenticated) {
      dispatch(fetchUserProfile());
    }
  }, [dispatch, isAuthenticated]);

  // You can add loading or error states here if needed
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-violet-500"></div>
      </div>
    );
  }

  if (error) {
    console.error('Error loading user profile:', error);
    // You might want to show an error message or handle it differently
  }

  return children;
} 