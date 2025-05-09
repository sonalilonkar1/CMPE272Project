'use client';

import Link from 'next/link'
import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useDispatch, useSelector } from 'react-redux';
import { login } from '@/redux/features/authSlice';
import { signIn } from 'next-auth/react';
import { Toast } from '@/components/Toast';

export default function Login() {
  const router = useRouter();
  const dispatch = useDispatch();
  const searchParams = useSearchParams();
  const { loading } = useSelector((state) => state.auth);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  // Check for registration success message
  useEffect(() => {
    const registered = searchParams.get('registered');
    const errorMsg = searchParams.get('error');
    
    if (registered) {
      showToast.success('Registration successful! Please sign in with your new account.');
    }
    
    if (errorMsg === 'session-expired') {
      showToast.error('Your session has expired. Please sign in again.');
    }
  }, [searchParams]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      // Dispatch login action
      const result = await dispatch(login({
        email,
        password
      })).unwrap();

      if (result.token) {
        // Sign in with NextAuth
        const signInResult = await signIn('credentials', {
        email,
        password,
          redirect: false
      });

        if (signInResult.error) {
        return;
      }

      router.push('/dashboard');
      }
    } catch (err) {
      console.error('Login error:', err);
    }
  };

  const handleGoogleSignIn = () => {
    signIn('google', { callbackUrl: '/dashboard' });
  };

  return (
    <main className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <Toast />
      <div className="container max-w-md">
        <div className="card !p-8">
          <div className="text-center mb-8">
            <h1 className="!text-2xl !mb-2">Welcome Back</h1>
            <p className="text-slate-600 text-sm">
              Sign in to continue to ReliefCircle
            </p>
          </div>

          <form className="space-y-6" onSubmit={handleSubmit}>
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-slate-700 mb-1">
                Email address
              </label>
              <input
                id="email"
                name="email"
                type="email"
                autoComplete="email"
                required
                className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-xl 
                shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 
                focus:ring-violet-500 focus:border-violet-500 text-sm"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-slate-700 mb-1">
                Password
              </label>
              <input
                id="password"
                name="password"
                type="password"
                autoComplete="current-password"
                required
                className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-xl 
                shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 
                focus:ring-violet-500 focus:border-violet-500 text-sm"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>

            <div>
            <button
              type="submit"
                className="btn-primary w-full"
              disabled={loading}
            >
              {loading ? 'Signing in...' : 'Sign in'}
            </button>
            </div>
          </form>

            <div className="relative my-4">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-gray-300"></div>
              </div>
              <div className="relative flex justify-center text-sm">
                <span className="px-2 bg-white text-gray-500">Or continue with</span>
              </div>
            </div>
            
            <button
              type="button"
              onClick={handleGoogleSignIn}
              className="w-full flex items-center justify-center py-2 px-4 border border-gray-300 
              rounded-xl shadow-sm bg-white text-sm font-medium text-gray-700 hover:bg-gray-50"
            >
              <img 
                src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" 
                alt="Google" 
                className="h-5 w-5 mr-2" 
              />
              Google
            </button>

            <div className="text-center text-sm text-slate-600">
              Don&apos;t have an account?{' '}
              <Link href="/register" className="text-violet-600 hover:text-violet-500 font-medium">
                Sign up
              </Link>
            </div>
        </div>
      </div>
    </main>
  )
} 