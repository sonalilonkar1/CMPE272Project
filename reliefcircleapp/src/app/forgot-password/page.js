'use client'
import Link from 'next/link'

export default function ForgotPassword() {
  return (
    <main className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <h1 className="text-3xl font-bold text-slate-800">Forgot Password</h1>
          <p className="mt-2 text-sm text-slate-600">
            Enter your email address and we&apos;ll send you a link to reset your password.
          </p>
        </div>

        <div className="card">
          <form className="space-y-6">
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-slate-700">
                Email address
              </label>
              <div className="mt-1">
                <input
                  id="email"
                  name="email"
                  type="email"
                  autoComplete="email"
                  required
                  className="input-field"
                  placeholder="Enter your email"
                />
              </div>
            </div>

            <div>
              <button
                type="submit"
                className="btn-primary w-full"
              >
                Send Reset Link
              </button>
            </div>
          </form>

          <div className="mt-6 text-center">
            <Link 
              href="/users/auth/login"
              className="text-sm font-medium text-violet-600 hover:text-violet-500"
            >
              Back to Sign In
            </Link>
          </div>
        </div>

        <div className="text-center">
          <p className="text-sm text-slate-600">
            Don&apos;t have an account?{' '}
            <Link 
              href="/users/auth/register"
              className="font-medium text-violet-600 hover:text-violet-500"
            >
              Sign up
            </Link>
          </p>
        </div>
      </div>
    </main>
  )
} 