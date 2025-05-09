'use client'
import { useState } from 'react'
import Link from 'next/link'
import { useRouter } from 'next/navigation'
import { useDispatch, useSelector } from 'react-redux'
import { register } from '@/redux/features/authSlice'
import { signIn } from 'next-auth/react'
import { Toast } from '@/components/Toast'

export default function Register() {
  const router = useRouter()
  const dispatch = useDispatch()
  const { loading } = useSelector((state) => state.auth)
  const [selectedRole, setSelectedRole] = useState('donor')
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
    organizationName: '',
    agreeToTerms: false
  })

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    // Basic validation
    if (formData.password !== formData.confirmPassword) {
      return
    }

    try {
      // Dispatch registration action
      const result = await dispatch(register({
        fullName: formData.fullName,
        email: formData.email,
        password: formData.password,
        role: selectedRole.toUpperCase(),
        organizationName: selectedRole === 'fundraiser' ? formData.organizationName : null
      })).unwrap()

      if (result.token) {
        // Auto login after successful registration
        const signInResult = await signIn('credentials', {
          email: formData.email,
          password: formData.password,
          redirect: false
        })

        if (signInResult.error) {
          // If auto-login fails, redirect to login page
          router.push('/login?registered=true')
        } else {
          // Registration and login successful, redirect to dashboard
          router.push('/dashboard')
        }
      }
    } catch (err) {
      console.error('Registration error:', err)
    }
  }

  return (
    <main className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <Toast />
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <h1 className="text-3xl font-bold text-slate-800">Create an Account</h1>
          <p className="mt-2 text-sm text-slate-600">
            Join our community and make a difference
          </p>
        </div>

        <div className="card">
          {/* Role Selector */}
          <div className="mb-6">
            <label className="block text-sm font-medium text-slate-700 mb-2">
              I want to join as a
            </label>
            <div className="role-selector">
              <button
                type="button"
                onClick={() => setSelectedRole('donor')}
                className={`role-option ${selectedRole === 'donor' ? 'active' : ''}`}
              >
                Donor
              </button>
              <button
                type="button"
                onClick={() => setSelectedRole('fundraiser')}
                className={`role-option ${selectedRole === 'fundraiser' ? 'active' : ''}`}
              >
                Fundraiser
              </button>
            </div>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
              <div>
              <label htmlFor="fullName" className="block text-sm font-medium text-slate-700">
                Full Name
                </label>
                <div className="mt-1">
                  <input
                  id="fullName"
                  name="fullName"
                    type="text"
                  autoComplete="name"
                    required
                  value={formData.fullName}
                    onChange={handleChange}
                    className="input-field"
                  placeholder="Enter your full name"
                  />
              </div>
            </div>

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
                  value={formData.email}
                  onChange={handleChange}
                  className="input-field"
                  placeholder="Enter your email"
                />
              </div>
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-slate-700">
                Password
              </label>
              <div className="mt-1">
                <input
                  id="password"
                  name="password"
                  type="password"
                  autoComplete="new-password"
                  required
                  value={formData.password}
                  onChange={handleChange}
                  className="input-field"
                  placeholder="Create a password"
                />
              </div>
            </div>

            <div>
              <label htmlFor="confirmPassword" className="block text-sm font-medium text-slate-700">
                Confirm Password
              </label>
              <div className="mt-1">
                <input
                  id="confirmPassword"
                  name="confirmPassword"
                  type="password"
                  autoComplete="new-password"
                  required
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  className="input-field"
                  placeholder="Confirm your password"
                />
              </div>
            </div>

            {selectedRole === 'fundraiser' && (
              <div>
                <label htmlFor="organizationName" className="block text-sm font-medium text-slate-700">
                  Organization Name
                </label>
                <div className="mt-1">
                  <input
                    id="organizationName"
                    name="organizationName"
                    type="text"
                    required
                    value={formData.organizationName}
                    onChange={handleChange}
                    className="input-field"
                    placeholder="Enter your organization name"
                  />
                </div>
              </div>
            )}

            <div className="flex items-center">
              <input
                id="agreeToTerms"
                name="agreeToTerms"
                type="checkbox"
                required
                checked={formData.agreeToTerms}
                onChange={handleChange}
                className="h-4 w-4 text-violet-600 focus:ring-violet-500 border-gray-300 rounded"
              />
              <label htmlFor="agreeToTerms" className="ml-2 block text-sm text-slate-600">
                I agree to the{' '}
                <Link href="/terms" className="font-medium text-violet-600 hover:text-violet-500">
                  Terms of Service
                </Link>{' '}
                and{' '}
                <Link href="/privacy" className="font-medium text-violet-600 hover:text-violet-500">
                  Privacy Policy
                </Link>
              </label>
            </div>

            <div>
              <button
                type="submit"
                className="btn-primary w-full"
                disabled={loading}
              >
                {loading ? 'Creating Account...' : 'Create Account'}
              </button>
            </div>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-slate-600">
              Already have an account?{' '}
              <Link 
                href="/login"
                className="font-medium text-violet-600 hover:text-violet-500"
              >
                Sign in
              </Link>
            </p>
          </div>
        </div>
      </div>
    </main>
  )
} 