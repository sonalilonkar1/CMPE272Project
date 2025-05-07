'use client'
import { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useParams } from 'next/navigation'
import Image from 'next/image'
import { fetchCharityById } from '@/redux/features/charitiesSlice'

export default function CharityDetail() {
  const { charity_id } = useParams()
  const dispatch = useDispatch()
  const { currentCharity, status, error } = useSelector((state) => state.charities)
  const [donationAmount, setDonationAmount] = useState(50)
  const [isProcessing, setIsProcessing] = useState(false)

  useEffect(() => {
    if (charity_id) {
      dispatch(fetchCharityById(charity_id))
    }
  }, [charity_id, dispatch])

  const handleDonation = async (e) => {
    e.preventDefault()
    setIsProcessing(true)
    try {
      // Here you would integrate with your payment processor (e.g., Stripe)
      // For now, we'll just simulate a successful donation
      await new Promise(resolve => setTimeout(resolve, 2000))
      // You would typically show a success message here
    } catch (error) {
      console.error('Donation failed:', error)
    } finally {
      setIsProcessing(false)
    }
  }

  if (status === 'loading') {
    return (
      <main className="min-h-screen bg-gray-50">
        <div className="container py-12">
          <div className="flex items-center justify-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-violet-600"></div>
          </div>
        </div>
      </main>
    )
  }

  if (status === 'failed' || !currentCharity) {
    return (
      <main className="min-h-screen bg-gray-50">
        <div className="container py-12">
          <div className="text-center text-red-600">
            {error?.message || 'Failed to load charity details'}
          </div>
        </div>
      </main>
    )
  }

  const progressPercentage = (currentCharity.raisedAmount / currentCharity.targetAmount) * 100

  return (
    <main className="min-h-screen bg-gray-50">
      {/* Header Section */}
      <section className="gradient-bg">
        <div className="container py-12">
          <div className="flex items-center gap-4 mb-4">
            <h1 className="text-white !mb-0">{currentCharity.name}</h1>
            {currentCharity.isVerified && (
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                Verified
              </span>
            )}
          </div>
          <p className="hero-text">
            {currentCharity.description}
          </p>
        </div>
      </section>

      {/* Main Content */}
      <section className="section">
        <div className="container max-w-5xl">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Left Column - Charity Details */}
            <div className="lg:col-span-2 space-y-8">
              {/* Image */}
              <div className="relative w-full h-64 rounded-xl overflow-hidden">
                {currentCharity.imageUrl ? (
                  <Image
                    src={currentCharity.imageUrl}
                    alt={currentCharity.name}
                    fill
                    className="object-cover"
                  />
                ) : (
                  <div className="absolute inset-0 bg-gray-200 flex items-center justify-center">
                    <span className="text-gray-400">No image available</span>
                  </div>
                )}
              </div>

              {/* Story */}
              <div className="card">
                <h2 className="!text-xl !mb-4">Our Story</h2>
                <p className="text-slate-600 whitespace-pre-line">
                  {currentCharity.story || 'No story available'}
                </p>
              </div>

              {/* Updates */}
              <div className="card">
                <h2 className="!text-xl !mb-4">Updates</h2>
                <div className="space-y-4">
                  {currentCharity.updates?.length > 0 ? (
                    currentCharity.updates.map((update, index) => (
                      <div key={index} className="border-b border-gray-100 pb-4 last:border-0">
                        <div className="flex items-center justify-between mb-2">
                          <h3 className="!text-base !mb-0">{update.title}</h3>
                          <span className="text-sm text-slate-500">
                            {new Date(update.createdAt).toLocaleDateString()}
                          </span>
                        </div>
                        <p className="text-slate-600">{update.content}</p>
                      </div>
                    ))
                  ) : (
                    <p className="text-slate-600">No updates available</p>
                  )}
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
                        ${currentCharity.raisedAmount?.toLocaleString()}
                      </span>
                      <span className="text-sm text-slate-500">
                        of ${currentCharity.targetAmount?.toLocaleString()}
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
                      type="submit"
                      className="w-full btn-primary bg-gradient-to-r from-violet-600 to-teal-500"
                      disabled={isProcessing}
                    >
                      {isProcessing ? 'Processing...' : 'Donate Now'}
                    </button>
                  </form>

                  {/* Donation Info */}
                  <div className="text-sm text-slate-600 space-y-2">
                    <p>Your donation will help:</p>
                    <ul className="list-disc list-inside space-y-1">
                      {currentCharity.benefits?.map((benefit, index) => (
                        <li key={index}>{benefit}</li>
                      ))}
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  )
}
