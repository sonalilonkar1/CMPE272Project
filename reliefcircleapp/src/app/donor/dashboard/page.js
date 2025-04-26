'use client'
import { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import Link from 'next/link'
import { fetchDonorDonations } from '@/redux/features/donationsSlice'

export default function Dashboard() {
  const dispatch = useDispatch()
  const [activeTab, setActiveTab] = useState('overview')
  const { donations, status, error } = useSelector((state) => state.donations)

  // Mock donor ID - replace with actual ID from your auth system
  const donorId = '7bf83b6a-0fad-41c7-ba59-55449886f6d7'

  useEffect(() => {
    if (activeTab === 'donations' || activeTab === 'overview') {
      dispatch(fetchDonorDonations(donorId))
    }
  }, [dispatch, activeTab, donorId])

  // Calculate total donations
  const totalDonations = donations.reduce((sum, donation) => sum + donation.amount, 0)
  const charitiesSupported = new Set(donations.map(d => d.charity)).size

  return (
    <main className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="gradient-bg">
        <div className="container py-8">
          <p className="text-white/80 text-sm">
            Welcome back, John Doe
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
                  onClick={() => setActiveTab('donations')}
                  className={`w-full text-left px-4 py-2 rounded-xl text-sm font-medium transition-colors
                    ${activeTab === 'donations' 
                      ? 'bg-violet-50 text-violet-600' 
                      : 'text-slate-600 hover:bg-gray-50'}`}
                >
                  My Donations
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
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="card !p-6">
                    <h3 className="!text-base !mb-1">Total Donations</h3>
                    <p className="text-2xl font-bold text-slate-800">${totalDonations.toLocaleString()}</p>
                  </div>
                  <div className="card !p-6">
                    <h3 className="!text-base !mb-1">Charities Supported</h3>
                    <p className="text-2xl font-bold text-slate-800">{charitiesSupported}</p>
                  </div>
                </div>

                {/* Recent Activity */}
                <div className="card">
                  <h2 className="!text-lg !mb-4">Recent Activity</h2>
                  <div className="space-y-4">
                    {status === 'loading' ? (
                      <div className="flex items-center justify-center py-8">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-violet-600"></div>
                      </div>
                    ) : error ? (
                      <div className="text-center text-red-600 py-4">
                        {error}
                      </div>
                    ) : donations.length === 0 ? (
                      <div className="text-center text-slate-600 py-8">
                        No donations yet. Start making a difference!
                      </div>
                    ) : (
                      donations.slice(0, 5).map(donation => (
                        <div key={donation.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
                          <div>
                            <p className="font-medium text-slate-800">{donation.charity}</p>
                            <p className="text-sm text-slate-600">Donated ${donation.amount}</p>
                          </div>
                          <span className="text-sm text-slate-600">{new Date(donation.date).toLocaleDateString()}</span>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              </>
            )}

            {activeTab === 'donations' && (
              <div className="card">
                <h2 className="!text-lg !mb-4">Donation History</h2>
                <div className="overflow-x-auto">
                  {status === 'loading' ? (
                    <div className="flex items-center justify-center py-8">
                      <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-violet-600"></div>
                    </div>
                  ) : error ? (
                    <div className="text-center text-red-600 py-4">
                      {error}
                    </div>
                  ) : donations.length === 0 ? (
                    <div className="text-center text-slate-600 py-8">
                      No donations yet. Start making a difference!
                    </div>
                  ) : (
                    <table className="w-full">
                      <thead>
                        <tr className="border-b border-gray-200">
                          <th className="text-left py-3 px-4 text-sm font-medium text-slate-600">Charity</th>
                          <th className="text-left py-3 px-4 text-sm font-medium text-slate-600">Amount</th>
                          <th className="text-left py-3 px-4 text-sm font-medium text-slate-600">Date</th>
                          <th className="text-left py-3 px-4 text-sm font-medium text-slate-600">Status</th>
                        </tr>
                      </thead>
                      <tbody>
                        {donations.map(donation => (
                          <tr key={donation.id} className="border-b border-gray-100">
                            <td className="py-3 px-4">{donation.charity}</td>
                            <td className="py-3 px-4">${donation.amount}</td>
                            <td className="py-3 px-4">{new Date(donation.date).toLocaleDateString()}</td>
                            <td className="py-3 px-4">
                              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                                {donation.status}
                              </span>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
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
                        <span className="ml-2 text-sm text-slate-600">Donation receipts</span>
                      </label>
                      <label className="flex items-center">
                        <input type="checkbox" className="h-4 w-4 text-violet-600 focus:ring-violet-500 border-gray-300 rounded" />
                        <span className="ml-2 text-sm text-slate-600">Impact updates</span>
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