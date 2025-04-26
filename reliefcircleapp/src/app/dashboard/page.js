'use client'

import { useSession } from 'next-auth/react';
import ProtectedRoute from '../components/ProtectedRoute';
import { useState, useEffect } from 'react';
import axios from 'axios';

// API URL
const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export default function Dashboard() {
  const { data: session } = useSession();
  const [userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Fetch user data when component mounts
  useEffect(() => {
    const fetchUserData = async () => {
      try {
        // This request will automatically include the bearer token
        const response = await axios.get(`${API_URL}/users/me`);
        setUserData(response.data);
        setLoading(false);
      } catch (err) {
        console.error('Error fetching user data:', err);
        setError('Failed to load user data');
        setLoading(false);
      }
    };

    if (session) {
      fetchUserData();
    }
  }, [session]);

  return (
    <ProtectedRoute>
      <main className="container mx-auto py-10 px-4">
        <h1 className="text-3xl font-bold mb-6">Dashboard</h1>
        
        {loading ? (
          <div className="flex justify-center">
            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-violet-500"></div>
          </div>
        ) : error ? (
          <div className="bg-red-100 text-red-700 p-4 rounded-lg">
            {error}
          </div>
        ) : (
          <div className="bg-white shadow rounded-lg p-6">
            <div className="mb-6">
              <h2 className="text-2xl font-semibold mb-4">Welcome, {session?.user?.name || 'User'}!</h2>
              <p className="text-gray-600">Email: {session?.user?.email}</p>
              {session?.user?.role && (
                <p className="text-gray-600">Role: {session.user.role}</p>
              )}
            </div>
            
            {userData && (
              <div className="border-t pt-4">
                <h3 className="text-xl font-semibold mb-3">Your Profile</h3>
                <pre className="bg-gray-100 p-4 rounded overflow-auto">
                  {JSON.stringify(userData, null, 2)}
                </pre>
              </div>
            )}
          </div>
        )}
      </main>
    </ProtectedRoute>
  );
} 