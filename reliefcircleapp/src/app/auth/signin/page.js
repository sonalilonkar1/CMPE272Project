'use client'
import { signIn } from 'next-auth/react'
import Image from 'next/image'

export default function SignIn() {
  const handleOktaSignIn = () => {
    signIn('okta', { 
      callbackUrl: '/',
      // Add prompt=login to force the login screen
      authorizationParams: {
        prompt: 'login'
      }
    })
  }

  const handleOktaSignUp = () => {
    // Use the same signIn method but with registration parameters
    signIn('okta', {
      callbackUrl: '/',
      authorizationParams: {
        // This tells Okta to show the registration page
        prompt: 'login',
        screen_hint: 'signup'
      }
    })
  }

  return (
    <main className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <h1 className="text-3xl font-bold text-slate-800">Welcome to ReliefCircle</h1>
          <p className="mt-2 text-sm text-slate-600">
            Sign in or create an account to get started
          </p>
        </div>

        <div className="card">
          <div className="space-y-4">
            <button
              onClick={handleOktaSignIn}
              className="w-full flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-xl text-white bg-violet-600 hover:bg-violet-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-violet-500"
            >
              <svg className="w-5 h-5 mr-2" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2C6.48 2 2 6.48 2 12C2 17.52 6.48 22 12 22C17.52 22 22 17.52 22 12C22 6.48 17.52 2 12 2ZM12 5C13.66 5 15 6.34 15 8C15 9.66 13.66 11 12 11C10.34 11 9 9.66 9 8C9 6.34 10.34 5 12 5ZM12 19.2C9.5 19.2 7.29 17.92 6 15.98C6.03 13.99 10 12.9 12 12.9C13.99 12.9 17.97 13.99 18 15.98C16.71 17.92 14.5 19.2 12 19.2Z" fill="currentColor"/>
              </svg>
              Sign in with Okta
            </button>

            <button
              onClick={handleOktaSignUp}
              className="w-full flex items-center justify-center px-4 py-2 border-2 border-violet-600 text-sm font-medium rounded-xl text-violet-600 bg-white hover:bg-violet-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-violet-500"
            >
              Create an Account
            </button>
          </div>

          <div className="mt-6">
            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-gray-300"></div>
              </div>
              <div className="relative flex justify-center text-sm">
                <span className="px-2 bg-white text-slate-600">
                  Secure Authentication
                </span>
              </div>
            </div>
          </div>

          <div className="mt-6 text-center text-sm">
            <p className="text-slate-600">
              By signing in or creating an account, you agree to our{' '}
              <a href="/terms" className="font-medium text-violet-600 hover:text-violet-500">
                Terms of Service
              </a>{' '}
              and{' '}
              <a href="/privacy" className="font-medium text-violet-600 hover:text-violet-500">
                Privacy Policy
              </a>
            </p>
          </div>
        </div>

        <div className="text-center text-sm text-slate-600">
          <p>
            Need help?{' '}
            <a href="/contact" className="font-medium text-violet-600 hover:text-violet-500">
              Contact Support
            </a>
          </p>
        </div>
      </div>
    </main>
  )
} 