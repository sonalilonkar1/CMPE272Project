'use client'
import { useState } from 'react'
import Link from 'next/link'
import { useRouter } from 'next/navigation'
import axios from 'axios'
import { signIn } from 'next-auth/react'

// API URL
const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api'

export default function Register() {
  const router = useRouter()
  const [selectedRole, setSelectedRole] = useState('donor')
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
    organizationName: '',
    agreeToTerms: false
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    // Basic validation
    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match')
      setLoading(false)
      return
    }

    try {
      // Send registration data to backend
      const response = await axios.post(`${API_URL}/auth/register`, {
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        password: formData.password,
        role: selectedRole,
        organizationName: selectedRole === 'fundraiser' ? formData.organizationName : null
      })

      if (response.data && response.data.success) {
        // Auto login after successful registration
        const result = await signIn('credentials', {
          email: formData.email,
          password: formData.password,
          redirect: false
        })

        if (result.error) {
          // If auto-login fails, redirect to login page
          router.push('/login?registered=true')
        } else {
          // Registration and login successful, redirect to dashboard
          router.push('/dashboard')
        }
      } else {
        setError(response.data?.message || 'Registration failed')
        setLoading(false)
      }
    } catch (err) {
      console.error('Registration error:', err)
      setError(err.response?.data?.message || 'An error occurred during registration')
      setLoading(false)
    }
  }

  return (
    <main className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <h1 className="text-3xl font-bold text-slate-800">Create an Account</h1>
          <p className="mt-2 text-sm text-slate-600">
            Join our community and make a difference
          </p>
        </div>

        <div className="card">
          {error && (
            <div className="mb-4 p-3 bg-red-100 text-red-700 rounded-lg text-sm">
              {error}
            </div>
          )}

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
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label htmlFor="firstName" className="block text-sm font-medium text-slate-700">
                  First Name
                </label>
                <div className="mt-1">
                  <input
                    id="firstName"
                    name="firstName"
                    type="text"
                    autoComplete="given-name"
                    required
                    value={formData.firstName}
                    onChange={handleChange}
                    className="input-field"
                    placeholder="Enter your first name"
                  />
                </div>
              </div>
              <div>
                <label htmlFor="lastName" className="block text-sm font-medium text-slate-700">
                  Last Name
                </label>
                <div className="mt-1">
                  <input
                    id="lastName"
                    name="lastName"
                    type="text"
                    autoComplete="family-name"
                    required
                    value={formData.lastName}
                    onChange={handleChange}
                    className="input-field"
                    placeholder="Enter your last name"
                  />
                </div>
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