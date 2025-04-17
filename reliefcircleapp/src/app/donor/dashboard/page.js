'use client'
import Link from 'next/link'
import { useState } from 'react'

// Temporary mock data - replace with actual API data
const mockDonations = [
  { id: 1, charity: "Save The Children", amount: 100, date: "2024-03-15", status: "Completed" },
  { id: 2, charity: "Ocean Conservation", amount: 50, date: "2024-03-10", status: "Completed" },
]

const mockVerificationRequests = [
  {
    id: 1,
    charity: "Global Food Bank",
    type: "Fund Utilization",
    submittedDate: "2024-03-14",
    status: "Pending Review",
    documents: 2
  },
  {
    id: 2,
    charity: "Education For All",
    type: "Project Completion",
    submittedDate: "2024-03-13",
    status: "Pending Review",
    documents: 3
  }
]

export default function Dashboard() {
  // Temporary - replace with actual auth state
  const userRole = 'volunteer' // or 'donor'
  const [activeTab, setActiveTab] = useState('overview')

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
                {userRole === 'volunteer' && (
                  <button
                    onClick={() => setActiveTab('verifications')}
                    className={`w-full text-left px-4 py-2 rounded-xl text-sm font-medium transition-colors
                      ${activeTab === 'verifications' 
                        ? 'bg-violet-50 text-violet-600' 
                        : 'text-slate-600 hover:bg-gray-50'}`}
                  >
                    Verification Requests
                  </button>
                )}
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
                    <h3 className="!text-base !mb-1">Total Donations</h3>
                    <p className="text-2xl font-bold text-slate-800">$150.00</p>
                  </div>
                  <div className="card !p-6">
                    <h3 className="!text-base !mb-1">Charities Supported</h3>
                    <p className="text-2xl font-bold text-slate-800">2</p>
                  </div>
                  {userRole === 'volunteer' && (
                    <div className="card !p-6">
                      <h3 className="!text-base !mb-1">Verifications Completed</h3>
                      <p className="text-2xl font-bold text-slate-800">5</p>
                    </div>
                  )}
                </div>

                {/* Recent Activity */}
                <div className="card">
                  <h2 className="!text-lg !mb-4">Recent Activity</h2>
                  <div className="space-y-4">
                    {mockDonations.map(donation => (
                      <div key={donation.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
                        <div>
                          <p className="font-medium text-slate-800">{donation.charity}</p>
                          <p className="text-sm text-slate-600">Donated ${donation.amount}</p>
                        </div>
                        <span className="text-sm text-slate-600">{donation.date}</span>
                      </div>
                    ))}
                  </div>
                </div>
              </>
            )}

            {activeTab === 'donations' && (
              <div className="card">
                <h2 className="!text-lg !mb-4">Donation History</h2>
                <div className="overflow-x-auto">
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
                      {mockDonations.map(donation => (
                        <tr key={donation.id} className="border-b border-gray-100">
                          <td className="py-3 px-4">{donation.charity}</td>
                          <td className="py-3 px-4">${donation.amount}</td>
                          <td className="py-3 px-4">{donation.date}</td>
                          <td className="py-3 px-4">
                            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                              {donation.status}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {activeTab === 'verifications' && userRole === 'volunteer' && (
              <div className="card">
                <div className="flex justify-between items-center !mb-4">
                  <h2 className="!text-lg !mb-0">Verification Requests</h2>
                  <select className="text-sm border border-gray-300 rounded-xl px-3 py-2 focus:outline-none focus:ring-2 focus:ring-violet-500">
                    <option>All Requests</option>
                    <option>Pending Review</option>
                    <option>Completed</option>
                  </select>
                </div>
                <div className="space-y-4">
                  {mockVerificationRequests.map(request => (
                    <div key={request.id} className="p-4 bg-gray-50 rounded-xl">
                      <div className="flex justify-between items-start mb-3">
                        <div>
                          <h3 className="!text-base !mb-1">{request.charity}</h3>
                          <p className="text-sm text-slate-600">{request.type}</p>
                        </div>
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                          {request.status}
                        </span>
                      </div>
                      <div className="flex justify-between items-center">
                        <div className="flex items-center space-x-4 text-sm text-slate-600">
                          <span>Submitted: {request.submittedDate}</span>
                          <span>{request.documents} documents</span>
                        </div>
                        <Link href={`/verifications/${request.id}`} className="btn-primary !py-2">
                          Review
                        </Link>
                      </div>
                    </div>
                  ))}
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
                      {userRole === 'volunteer' && (
                        <label className="flex items-center">
                          <input type="checkbox" className="h-4 w-4 text-violet-600 focus:ring-violet-500 border-gray-300 rounded" />
                          <span className="ml-2 text-sm text-slate-600">New verification requests</span>
                        </label>
                      )}
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