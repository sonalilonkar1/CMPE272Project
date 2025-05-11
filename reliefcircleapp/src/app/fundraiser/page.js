'use client'
import { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import Link from 'next/link'
import { fetchFundraiserCharities } from '@/redux/features/charitiesSlice'
import { fetchFundraiserUpdates } from '@/redux/features/updatesSlice'
import { useSearchParams } from 'next/navigation'
import UpdateModal from '@/components/UpdateModal'
import TransactionsTab from '@/components/TransactionsTab'
import { showToast } from '@/components/Toast'

export default function FundraiserDashboard() {
  const dispatch = useDispatch()
  const [page, setPage] = useState(1)
  const [selectedCharity, setSelectedCharity] = useState(null)
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false)
  
  const { 
    status: charitiesStatus, 
    error: charitiesError,
    fundraiserCharities
  } = useSelector((state) => state.charities)

  const {
    status: updatesStatus,
    error: updatesError,
    updates
  } = useSelector((state) => state.updates)

  const { profile } = useSelector((state) => state.user)
  const fundraiserId = profile?.id
  const token = profile?.token

  const searchParams = useSearchParams()
  const currentTab = searchParams.get('tab') || 'overview'
  const stripeStatus = searchParams.get('stripe')

  useEffect(() => {
    if (currentTab === 'charities' && fundraiserId && token) {
      dispatch(fetchFundraiserCharities({ fundraiserId, page, token }))
    }
  }, [dispatch, currentTab, fundraiserId, page, token])

  useEffect(() => {
    if (currentTab === 'updates' && token) {
      dispatch(fetchFundraiserUpdates({ token }))
    }
  }, [dispatch, currentTab, token])

  useEffect(() => {
    if (currentTab === 'transactions' && stripeStatus === 'success') {
      showToast.success('Stripe connected successfully')
    }
  }, [currentTab, stripeStatus])

  const handlePageChange = (newPage) => {
    setPage(newPage)
  }

  const handleOpenUpdateModal = (charity) => {
    setSelectedCharity(charity)
    setIsUpdateModalOpen(true)
  }

  const tabs = [
    { id: 'overview', label: 'Overview', href: '/fundraiser?tab=overview' },
    { id: 'charities', label: 'My Charities', href: '/fundraiser?tab=charities' },
    { id: 'updates', label: 'Updates', href: '/fundraiser?tab=updates' },
    { id: 'transactions', label: 'Transactions', href: '/fundraiser?tab=transactions' },
  ]

  // Mock updates data - replace with actual data from your API
  const mockUpdates = [
    {
      id: 1,
      charityName: 'Global Food Bank',
      message: 'We have successfully distributed food packages to 100 families this week!',
      date: '2024-03-10T10:00:00Z',
      media: '/images/update1.jpg'
    },
    {
      id: 2,
      charityName: 'Education for All',
      message: 'New classroom construction is 75% complete. Thank you for your support!',
      date: '2024-03-08T15:30:00Z',
      media: null
    }
  ]

  // Mock charity for testing
  const mockCharity = {
    id: 'mock-123',
    name: 'Global Education Initiative',
    createdAt: '2024-03-01T10:00:00Z',
    targetAmount: 50000,
    raisedAmount: 32500,
    description: 'Providing quality education to underprivileged children',
    isVerified: true,
    status: 'ACTIVE'
  }

  // Combine mock charity with API data
  // const displayCharities = [...(fundraiserCharities || []), mockCharity]
  const displayCharities = fundraiserCharities || [];


  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-900 mt-10">
            {currentTab === 'overview' 
              ? 'Fundraiser Overview' 
              : currentTab === 'updates'
              ? 'Charity Updates'
              : currentTab === 'transactions'
              ? 'Transactions'
              : 'My Charities'}
          </h1>
          <p className="mt-2 text-sm text-gray-600">
            {currentTab === 'overview' 
              ? 'View your fundraising statistics and overall impact'
              : currentTab === 'updates'
              ? 'Keep your donors informed about charity progress and milestones'
              : currentTab === 'transactions'
              ? 'Manage your payment processing and view transaction history'
              : 'Manage your charitable organizations and campaigns'
            }
          </p>
        </div>

        {/* Tabs */}
        <div className="border-b border-gray-200 mb-8">
          <nav className="-mb-px flex space-x-8">
            {tabs.map((tab) => (
              <Link
                key={tab.id}
                href={tab.href}
                className={`
                  whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm
                  ${currentTab === tab.id
                    ? 'border-violet-500 text-violet-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }
                `}
              >
                {tab.label}
              </Link>
            ))}
          </nav>
        </div>

        {/* Tab Content */}
        <div className="bg-white shadow rounded-lg p-6">
          {currentTab === 'overview' ? (
            <div className="space-y-6">
              {/* Overview Content */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="bg-violet-50 p-6 rounded-lg">
                  <h3 className="text-lg font-medium text-violet-900">Total Charities</h3>
                  <p className="mt-2 text-3xl font-bold text-violet-600">12</p>
                </div>
                <div className="bg-green-50 p-6 rounded-lg">
                  <h3 className="text-lg font-medium text-green-900">Total Donations</h3>
                  <p className="mt-2 text-3xl font-bold text-green-600">$24,500</p>
                </div>
                <div className="bg-blue-50 p-6 rounded-lg">
                  <h3 className="text-lg font-medium text-blue-900">Total Donors</h3>
                  <p className="mt-2 text-3xl font-bold text-blue-600">156</p>
                </div>
              </div>

              {/* Recent Activity */}
              <div className="mt-8">
                <h3 className="text-lg font-medium text-gray-900 mb-4">Recent Activity</h3>
                <div className="space-y-4">
                  <p className="text-sm text-gray-500">No recent activity to display.</p>
                </div>
              </div>
            </div>
          ) : currentTab === 'updates' ? (
            <div className="space-y-6">
              {updatesStatus === 'loading' ? (
                <div className="text-center py-4">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-violet-600 mx-auto"></div>
                </div>
              ) : updatesError ? (
                <div className="text-center text-red-600 py-4">{updatesError}</div>
              ) : updates.length === 0 ? (
                <div className="text-center py-8">
                  <p className="text-gray-500">No updates found. Create your first update to get started!</p>
                </div>
              ) : (
                <div className="space-y-6">
                  {updates.map((update) => (
                    <div key={update.id} className="bg-gray-50 rounded-lg p-6">
                      <div className="flex justify-between items-start mb-4">
                        <div>
                          <h3 className="text-lg font-medium text-gray-900">{update.charityName}</h3>
                          <div className="flex items-center gap-2 mt-1">
                            <p className="text-sm text-gray-500">
                              {new Date(update.createdAt).toLocaleDateString('en-US', {
                                year: 'numeric',
                                month: 'long',
                                day: 'numeric'
                              })}
                            </p>
                            <span className="text-sm text-gray-500">•</span>
                            <p className="text-sm text-gray-500">by {update.fundraiserName}</p>
                          </div>
                        </div>
                        <div className="flex items-center gap-2">
                          <div className="flex items-center">
                            <svg className="w-5 h-5 text-yellow-400" fill="currentColor" viewBox="0 0 20 20">
                              <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                            </svg>
                            <span className="ml-1 text-sm text-gray-600">{update.averageRating.toFixed(1)}</span>
                            <span className="ml-1 text-sm text-gray-500">({update.ratingCount})</span>
                          </div>
                        </div>
                      </div>
                      <p className="text-gray-600 mb-4">{update.text}</p>
                      {update.fileUrl && (
                        <div className="mt-4">
                          <img
                            src={update.fileUrl}
                            alt="Update media"
                            className="rounded-lg max-h-64 w-auto"
                          />
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          ) : currentTab === 'transactions' ? (
            <TransactionsTab fundraiserId={fundraiserId} />
          ) : (
            <div className="space-y-6">
              {/* Charities Content */}
              <div className="flex justify-between items-center mb-6">
                <h3 className="text-lg font-medium text-gray-900">Your Charities</h3>
                <Link
                  href="/charities/new"
                  className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-violet-600 hover:bg-violet-700"
                >
                  Create New Charity
                </Link>
              </div>

              {/* Charities List */}
              <div className="space-y-4">
                {charitiesStatus === 'loading' ? (
                  <div className="text-center py-4">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-violet-600 mx-auto"></div>
                  </div>
                ) : charitiesError && false ? (
                  <div className="text-center text-red-600 py-4">{charitiesError}</div>
                ) : displayCharities.length === 0 ? (
                  <div className="text-center py-8">
                    <p className="text-gray-500">No charities found. Create your first charity to get started!</p>
                  </div>
                ) : (
                  displayCharities.map((charity) => (
                    <div key={charity.id} className="bg-gray-50 rounded-lg p-6">
                      <div className="flex justify-between items-start">
                        <div>
                          <div className="flex items-center gap-2">
                            <h4 className="text-lg font-medium text-gray-900">{charity.name}</h4>
                            {charity.isVerified && (
                              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                                Verified
                              </span>
                            )}
                            {charity.id === 'mock-123' && (
                              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                                Demo
                              </span>
                            )}
                          </div>
                          <p className="text-sm text-gray-500 mt-1">
                            Created on {new Date(charity.createdAt).toLocaleDateString()}
                          </p>
                          {charity.description && (
                            <p className="text-sm text-gray-600 mt-2">{charity.description}</p>
                          )}
                        </div>
                        <button
                          onClick={() => handleOpenUpdateModal(charity)}
                          className="inline-flex items-center px-3 py-2 border border-transparent text-sm font-medium rounded-md text-violet-600 bg-violet-100 hover:bg-violet-200"
                        >
                          Send Update
                        </button>
                      </div>
                      <div className="mt-4">
                        <div className="flex space-x-4 text-sm text-gray-600">
                          <span>Target: ${charity.targetAmount.toLocaleString()}</span>
                          <span>Raised: ${charity.raisedAmount.toLocaleString()}</span>
                          <span className="text-green-600">
                            ({Math.round((charity.raisedAmount / charity.targetAmount) * 100)}% funded)
                          </span>
                        </div>
                        {/* Progress Bar */}
                        <div className="mt-2 w-full bg-gray-200 rounded-full h-2.5">
                          <div
                            className="bg-violet-600 h-2.5 rounded-full"
                            style={{ width: `${Math.min((charity.raisedAmount / charity.targetAmount) * 100, 100)}%` }}
                          ></div>
                        </div>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Update Modal */}
      <UpdateModal
        isOpen={isUpdateModalOpen}
        onClose={() => {
          setIsUpdateModalOpen(false)
          setSelectedCharity(null)
        }}
        charityId={selectedCharity?.id}
        charityName={selectedCharity?.name}
      />
    </div>
  )
} 