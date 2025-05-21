'use client'
import { useState, useEffect } from 'react'
import DonationModal from '@/components/DonationModal'
import { loadStripe } from '@stripe/stripe-js'
import { useDispatch, useSelector } from 'react-redux'
import { fetchCharityUpdates } from '@/redux/features/charitiesSlice'

// Skeleton loader component
export const CharityDetailSkeleton = () => (
  <main className="min-h-screen bg-gray-50 mt-28">
    {/* Header Section Skeleton */}
    <section className="gradient-bg">
      <div className="container py-12">
        <div className="flex items-center gap-4 mb-4">
          <div className="h-8 bg-white bg-opacity-20 rounded-lg w-1/3 animate-pulse"></div>
        </div>
        <div className="space-y-3">
          <div className="h-4 bg-white bg-opacity-20 rounded w-3/4 animate-pulse"></div>
          <div className="h-4 bg-white bg-opacity-20 rounded w-2/3 animate-pulse"></div>
        </div>
        <div className="mt-4 flex items-center gap-4">
          <div className="h-4 bg-white bg-opacity-20 rounded w-40 animate-pulse"></div>
          <span className="text-white opacity-80">•</span>
          <div className="h-4 bg-white bg-opacity-20 rounded w-32 animate-pulse"></div>
        </div>
      </div>
    </section>

    {/* Main Content Skeleton */}
    <section className="section">
      <div className="container max-w-5xl">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Left Column Skeleton */}
          <div className="lg:col-span-2">
            <div className="card">
              <div className="h-6 bg-gray-200 rounded w-48 mb-6 animate-pulse"></div>
              <div className="space-y-8">
                {/* Organization Details Skeleton */}
                <div>
                  <div className="h-5 bg-gray-200 rounded w-40 mb-3 animate-pulse"></div>
                  <div className="space-y-2">
                    <div className="h-4 bg-gray-200 rounded w-full animate-pulse"></div>
                    <div className="h-4 bg-gray-200 rounded w-5/6 animate-pulse"></div>
                  </div>
                </div>
                {/* Category Skeleton */}
                <div>
                  <div className="h-5 bg-gray-200 rounded w-24 mb-3 animate-pulse"></div>
                  <div className="h-4 bg-gray-200 rounded w-32 animate-pulse"></div>
                </div>
                {/* Fundraiser Information Skeleton */}
                <div>
                  <div className="h-5 bg-gray-200 rounded w-48 mb-3 animate-pulse"></div>
                  <div className="space-y-2">
                    <div className="h-4 bg-gray-200 rounded w-56 animate-pulse"></div>
                    <div className="h-4 bg-gray-200 rounded w-48 animate-pulse"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Right Column Skeleton */}
          <div className="lg:col-span-1">
            <div className="card sticky top-8">
              <div className="space-y-6">
                {/* Progress Skeleton */}
                <div>
                  <div className="flex justify-between items-center mb-2">
                    <div className="h-7 bg-gray-200 rounded w-24 animate-pulse"></div>
                    <div className="h-4 bg-gray-200 rounded w-20 animate-pulse"></div>
                  </div>
                  <div className="h-2 bg-gray-200 rounded-full w-full animate-pulse"></div>
                </div>

                {/* Donation Form Skeleton */}
                <div className="space-y-4">
                  <div>
                    <div className="h-4 bg-gray-200 rounded w-32 mb-2 animate-pulse"></div>
                    <div className="h-10 bg-gray-200 rounded-xl w-full animate-pulse"></div>
                  </div>
                  <div className="h-10 bg-gray-200 rounded-xl w-full animate-pulse"></div>
                </div>

                {/* Organization Info Skeleton */}
                <div>
                  <div className="h-4 bg-gray-200 rounded w-40 mb-2 animate-pulse"></div>
                  <div className="h-4 bg-gray-200 rounded w-48 animate-pulse"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </main>
);

export default function CharityContent({ initialData }) {
  const [donationAmount, setDonationAmount] = useState(50)
  const [isProcessing, setIsProcessing] = useState(false)
  const [isDonationModalOpen, setIsDonationModalOpen] = useState(false)
  const [selectedCharity, setSelectedCharity] = useState(null)
  const dispatch = useDispatch()
  const { profile } = useSelector(state => state.user)

  useEffect(() => {
    if (initialData.id) {
      dispatch(fetchCharityUpdates({ charityId: initialData.id, token: profile?.token }))
    }
  }, [dispatch, initialData.id, profile?.token])

  const handleDonation = async (e) => {
    e.preventDefault()
    setIsProcessing(true)
    try {
      const stripe = await loadStripe(process.env.NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY)
      const { error } = await stripe.redirectToCheckout({
        sessionId: data.sessionId, // This must be a Checkout Session ID (starts with cs_)
      })
      if (error) {
        console.error('Stripe checkout error:', error)
      }
    } catch (error) {
      console.error('Donation failed:', error)
    } finally {
      setIsProcessing(false)
    }
  }

  const progressPercentage = (initialData.raisedAmount / initialData.targetAmount) * 100

  const handleOpenDonationModal = (charity) => {
    setSelectedCharity(charity)
    setIsDonationModalOpen(true)
  }

  const handleCloseDonationModal = () => {
    setIsDonationModalOpen(false)
    setSelectedCharity(null)
  }

  return (
    <main className="min-h-screen bg-gray-50">
      {/* Header Section */}
      <section className="gradient-bg mt-16">
        <div className="container py-12">
          <div className="flex items-center gap-4 mb-4">
            <h1 className="text-white !mb-0">{initialData.name}</h1>
            {initialData.isVerified && (
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                Verified
              </span>
            )}
          </div>
          <p className="hero-text text-white opacity-90">
            {initialData.description}
          </p>
          <div className="mt-4 flex items-center gap-4">
            <span className="text-white opacity-80">By {initialData.organizationName}</span>
            <span className="text-white opacity-80">•</span>
            <span className="text-white opacity-80">Created {new Date(initialData.createdAt).toLocaleDateString()}</span>
          </div>
        </div>
      </section>

      {/* Main Content */}
      <section className="section">
        <div className="container max-w-5xl">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Left Column - Charity Details */}
            <div className="lg:col-span-2">
              <div className="card">
                <h2 className="text-xl font-semibold mb-4">About the Charity</h2>
                <div className="space-y-4">
                  <div>
                    <h3 className="text-lg font-medium mb-2">Organization Details</h3>
                    <p className="text-slate-600">{initialData.description}</p>
                  </div>
                  <div>
                    <h3 className="text-lg font-medium mb-2">Category</h3>
                    <p className="text-slate-600">{initialData.category}</p>
                  </div>
                  <div>
                    <h3 className="text-lg font-medium mb-2">Fundraiser Information</h3>
                    <p className="text-slate-600">
                      Organized by {initialData.fundraiserName}<br />
                      Contact: {initialData.fundraiserEmail}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* Right Column - Donation Card */}
            <div className="lg:col-span-1">
              <div className="card sticky top-8">
                <div className="space-y-6">
                  {/* Progress */}
                  <div>
                    <div className="flex justify-between items-center mb-2">
                      <span className="text-2xl font-bold text-slate-800">
                        ${initialData.raisedAmount?.toLocaleString()}
                      </span>
                      <span className="text-sm text-slate-500">
                        of ${initialData.targetAmount?.toLocaleString()}
                      </span>
                    </div>
                    <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                      <div
                        className="h-full bg-gradient-to-r from-violet-600 to-teal-500"
                        style={{ width: `${progressPercentage}%` }}
                      />
                    </div>
                  </div>

                  {/* Donation Form */}
                  <form onSubmit={handleDonation} className="space-y-4">
                    <div>
                      <label htmlFor="amount" className="block text-sm font-medium text-slate-700 mb-2">
                        Donation Amount
                      </label>
                      <div className="relative">
                        <span className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500">$</span>
                        <input
                          type="number"
                          id="amount"
                          value={donationAmount}
                          onChange={(e) => setDonationAmount(Number(e.target.value))}
                          min="1"
                          className="w-full pl-8 pr-4 py-2 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-violet-500"
                          required
                        />
                      </div>
                    </div>

                    <button
                      type="button"
                      className="w-full btn-primary bg-gradient-to-r from-violet-600 to-teal-500"
                      onClick={() => handleOpenDonationModal(initialData)}
                      disabled={isProcessing}
                    >
                      Donate Now
                    </button>
                  </form>

                  {/* Organization Info */}
                  <div className="text-sm text-slate-600">
                    <p className="font-medium mb-2">About the Organization</p>
                    <p>{initialData.organizationName}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Donation Modal */}
      {isDonationModalOpen && (
        <DonationModal
          isOpen={isDonationModalOpen}
          onClose={handleCloseDonationModal}
          charity={selectedCharity}
          initialAmount={donationAmount}
        />
      )}
    </main>
  )
} 