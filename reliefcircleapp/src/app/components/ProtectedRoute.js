'use client';

import { useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';

export default function ProtectedRoute({ 
  children,
  requiredRole = null,
  redirectTo = '/login',
}) {
  const { data: session, status } = useSession();
  const router = useRouter();
  
  useEffect(() => {
    // Check if the session is loading
    if (status === 'loading') return;
    
    // If not authenticated, redirect to login
    if (!session) {
      router.push(`${redirectTo}?callbackUrl=${encodeURIComponent(window.location.href)}`);
      return;
    }
    
    // If role is required but user doesn't have it
    if (requiredRole && session.user.role !== requiredRole) {
      router.push('/');
    }
  }, [session, status, requiredRole, redirectTo, router]);
  
  // Show loading or nothing while checking authentication
  if (status === 'loading' || !session) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-violet-500"></div>
      </div>
    );
  }
  
  // If role is required but user doesn't have it, show nothing
  if (requiredRole && session.user.role !== requiredRole) {
    return null;
  }
  
  // If authenticated and has the required role, show children
  return children;
} 