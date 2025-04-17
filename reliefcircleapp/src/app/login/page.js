import Link from 'next/link'

export default function Login() {
  return (
    <main className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="container max-w-md">
        <div className="card !p-8">
          <div className="text-center mb-8">
            <h1 className="!text-2xl !mb-2">Welcome Back</h1>
            <p className="text-slate-600 text-sm">
              Sign in to continue to ReliefCircle
            </p>
          </div>

          <form className="space-y-6">
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
              />
            </div>

            <div className="flex items-center justify-between">
              <div className="flex items-center">
                <input
                  id="remember-me"
                  name="remember-me"
                  type="checkbox"
                  className="h-4 w-4 text-violet-600 focus:ring-violet-500 border-gray-300 rounded"
                />
                <label htmlFor="remember-me" className="ml-2 block text-sm text-slate-600">
                  Remember me
                </label>
              </div>

              <div className="text-sm">
                <Link href="/users/auth/forgot-password" className="text-violet-600 hover:text-violet-500">
                  Forgot password?
                </Link>
              </div>
            </div>

            <button
              type="submit"
              className="w-full btn-primary"
            >
              Sign in
            </button>

            <div className="text-center text-sm text-slate-600">
              Don&apos;t have an account?{' '}
              <Link href="/register" className="text-violet-600 hover:text-violet-500 font-medium">
                Sign up
              </Link>
            </div>
          </form>
        </div>
      </div>
    </main>
  )
} 