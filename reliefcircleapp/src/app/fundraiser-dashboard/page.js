'use client'
import { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import Link from 'next/link'
import { fetchFundraiserCharities } from '@/redux/features/charitiesSlice'



export default function FundraiserDashboard() {
  const dispatch = useDispatch()
  const [activeTab, setActiveTab] = useState('overview')
  const [page, setPage] = useState(0)
  const { 
    fundraiserCharities, 
    fundraiserCharitiesPage,
    fundraiserCharitiesTotalPages,
    fundraiserCharitiesTotalElements,
    status, 
    error 
  } = useSelector((state) => state.charities)

  // Mock fundraiser ID - replace with actual ID from your auth system
  const fundraiserId = '2770e3eb-c16c-4662-b24e-a5c689929848'

  useEffect(() => {
    if (activeTab === 'charities') {
      dispatch(fetchFundraiserCharities({ fundraiserId, page }))
    }
  }, [dispatch, activeTab, fundraiserId, page])

  const handlePageChange = (newPage) => {
    setPage(newPage)
  }

  return (
    <main className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="gradient-bg">
        <div className="container py-8">
          <p className="text-white/80 text-sm">
            Welcome back, Global Food Bank
          </p>
        </div>
      </div>

      {/* Main Content */}
      <div className="container py-8">
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Sidebar */}
          <div className="lg:w-64 space-y-4">
            <div className="card !p-4">
              <nav className="space-y-1">
                <button
                  onClick={() => setActiveTab('overview')}
                  className={`w-full text-left px-4 py-2 rounded-xl text-sm font-medium transition-colors
                    ${activeTab === 'overview' 
                      ? 'bg-violet-50 text-violet-600' 
                      : 'text-slate-600 hover:bg-gray-50'}`}
                >
                  Overview
                </button>
                <button
                  onClick={() => setActiveTab('charities')}
                  className={`w-full text-left px-4 py-2 rounded-xl text-sm font-medium transition-colors
                    ${activeTab === 'charities' 
                      ? 'bg-violet-50 text-violet-600' 
                      : 'text-slate-600 hover:bg-gray-50'}`}
                >
                  My Charities
                </button>
                <button
                  onClick={() => setActiveTab('settings')}
                  className={`w-full text-left px-4 py-2 rounded-xl text-sm font-medium transition-colors
                    ${activeTab === 'settings' 
                      ? 'bg-violet-50 text-violet-600' 
                      : 'text-slate-600 hover:bg-gray-50'}`}
                >
                  Settings
                </button>
              </nav>
            </div>
          </div>

          {/* Main Content Area */}
          <div className="flex-1 space-y-6">
            {activeTab === 'overview' && (
              <>
                {/* Stats */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                  <div className="card !p-6">
                    <h3 className="!text-base !mb-1">Total Raised</h3>
                    <p className="text-2xl font-bold text-slate-800">$100,000</p>
                  </div>
                  <div className="card !p-6">
                    <h3 className="!text-base !mb-1">Active Campaigns</h3>
                    <p className="text-2xl font-bold text-slate-800">2</p>
                  </div>
                  <div className="card !p-6">
                    <h3 className="!text-base !mb-1">Total Donors</h3>
                    <p className="text-2xl font-bold text-slate-800">450</p>
                  </div>
                </div>

                {/* Recent Activity */}
                <div className="card">
                  <h2 className="!text-lg !mb-4">Recent Activity</h2>
                  <div className="space-y-4">
                    {fundraiserCharities.map(charity => (
                      <div key={charity.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
                        <div>
                          <p className="font-medium text-slate-800">{charity.name}</p>
                          <p className="text-sm text-slate-600">
                            Raised: ${charity.raisedAmount.toLocaleString()}
                          </p>
                        </div>
                        <span className="text-sm text-slate-600">
                          {new Date(charity.createdAt).toLocaleDateString()}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              </>
            )}

            {activeTab === 'charities' && (
              <div className="card">
                <div className="flex justify-between items-center !mb-4">
                  <h2 className="!text-lg !mb-0">My Charities</h2>
                  <Link href="/charities/new" className="btn-primary">
                    Add New Charity
                  </Link>
                </div>
                <div className="space-y-4">
                  {status === 'loading' ? (
                    <div className="flex items-center justify-center py-8">
                      <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-violet-600"></div>
                    </div>
                  ) : error ? (
                    <div className="text-center text-red-600 py-4">
                      {error}
                    </div>
                  ) : fundraiserCharities.length === 0 ? (
                    <div className="text-center text-slate-600 py-8">
                      No charities found. Create your first charity!
                    </div>
                  ) : (
                    <>
                      {fundraiserCharities.map(charity => (
                        <div key={charity.id} className="p-4 bg-gray-50 rounded-xl">
                          <div className="flex justify-between items-start mb-3">
                            <div>
                              <h3 className="!text-base !mb-1">{charity.name}</h3>
                              <div className="flex items-center space-x-4 text-sm text-slate-600">
                                <span>Target: ${charity.targetAmount.toLocaleString()}</span>
                                <span>Raised: ${charity.raisedAmount.toLocaleString()}</span>
                              </div>
                            </div>
                            <div className="flex flex-col items-end space-y-2">
                              {charity.isVerified && (
                                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                                  Verified
                                </span>
                              )}
                            </div>
                          </div>
                          <div className="flex justify-between items-center">
                            <span className="text-sm text-slate-600">
                              Created: {new Date(charity.createdAt).toLocaleDateString()}
                            </span>
                            <Link 
                              href={`/charities/${charity.id}`}
                              className="text-violet-600 hover:text-violet-700 text-sm font-medium"
                            >
                              View Details
                            </Link>
                          </div>
                        </div>
                      ))}

                      {/* Pagination Controls */}
                      {fundraiserCharitiesTotalPages > 1 && (
                        <div className="mt-6 flex justify-center items-center space-x-4">
                          <button
                            onClick={() => handlePageChange(page - 1)}
                            disabled={page === 0}
                            className={`px-4 py-2 rounded-lg ${
                              page === 0
                                ? 'bg-gray-200 text-gray-500 cursor-not-allowed'
                                : 'bg-violet-600 text-white hover:bg-violet-700'
                            }`}
                          >
                            Previous
                          </button>
                          <span className="text-slate-600">
                            Page {page + 1} of {fundraiserCharitiesTotalPages}
                          </span>
                          <button
                            onClick={() => handlePageChange(page + 1)}
                            disabled={page === fundraiserCharitiesTotalPages - 1}
                            className={`px-4 py-2 rounded-lg ${
                              page === fundraiserCharitiesTotalPages - 1
                                ? 'bg-gray-200 text-gray-500 cursor-not-allowed'
                                : 'bg-violet-600 text-white hover:bg-violet-700'
                            }`}
                          >
                            Next
                          </button>
                        </div>
                      )}

                      <div className="mt-4 text-center text-sm text-slate-500">
                        Showing {fundraiserCharities.length} of {fundraiserCharitiesTotalElements} charities
                      </div>
                    </>
                  )}
                </div>
              </div>
            )}

            {activeTab === 'settings' && (
              <div className="card">
                <h2 className="!text-lg !mb-4">Account Settings</h2>
                <div className="space-y-6">
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">
                      Email Notifications
                    </label>
                    <div className="space-y-2">
                      <label className="flex items-center">
                        <input type="checkbox" className="h-4 w-4 text-violet-600 focus:ring-violet-500 border-gray-300 rounded" />
                        <span className="ml-2 text-sm text-slate-600">New donations</span>
                      </label>
                      <label className="flex items-center">
                        <input type="checkbox" className="h-4 w-4 text-violet-600 focus:ring-violet-500 border-gray-300 rounded" />
                        <span className="ml-2 text-sm text-slate-600">Verification updates</span>
                      </label>
                      <label className="flex items-center">
                        <input type="checkbox" className="h-4 w-4 text-violet-600 focus:ring-violet-500 border-gray-300 rounded" />
                        <span className="ml-2 text-sm text-slate-600">Campaign milestones</span>
                      </label>
                    </div>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </main>
  )
} 