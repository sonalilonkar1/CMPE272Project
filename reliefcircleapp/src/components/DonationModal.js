'use client'

import { useState, useEffect } from 'react'
import { showToast } from './Toast'

export default function DonationModal({ isOpen, onClose, charity, initialAmount }) {
  const [amount, setAmount] = useState(initialAmount || '')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (isOpen) {
      setAmount(initialAmount || '')
    }
  }, [isOpen, initialAmount])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)

    try {
      // Create a test payment intent
      const response = await fetch('/api/create-payment-intent', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          amount: parseFloat(amount) * 100, // Convert to cents
          charityId: charity.id,
          charityName: charity.name,
          stripeAccount: charity.fundraiserStripeId ? charity.fundraiserStripeId :'acct_1RO0YyReABzSigiM',
        }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.message || 'Failed to create payment')
      }
      window.location = data.url
    } catch (error) {
      console.error('Payment error:', error)
      showToast.error(error.message || 'Failed to process payment')
    } finally {
      setLoading(false)
    }
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-lg max-w-md w-full p-6">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold text-gray-900">Make a Donation</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-500"
          >
            <span className="sr-only">Close</span>
            <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <div className="mb-4">
          <p className="text-sm text-gray-600">
            You are donating to: <span className="font-medium text-gray-900">{charity.name}</span>
          </p>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="amount" className="block text-sm font-medium text-gray-700 mb-1">
              Donation Amount (USD)
            </label>
            <div className="relative rounded-md shadow-sm">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <span className="text-gray-500 sm:text-sm">$</span>
              </div>
              <div className="block w-full pl-7 pr-12 sm:text-sm border-gray-300 rounded-md bg-gray-100 h-10 flex items-center">
                <span className="text-gray-900">{amount}</span>
              </div>
            </div>
          </div>

          <div className="mt-6">
            <button
              type="submit"
              disabled={loading}
              className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-violet-600 hover:bg-violet-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-violet-500 disabled:opacity-50"
            >
              {loading ? (
                <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
              ) : (
                'Proceed to Payment'
              )}
            </button>
          </div>

          <div className="mt-4 text-center">
            <p className="text-xs text-gray-500">
              This is a test payment. No real charges will be made.
            </p>
          </div>
        </form>
      </div>
    </div>
  )
} 