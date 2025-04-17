'use client'
import { useState } from 'react'
import Link from 'next/link'

// Mock data - replace with actual API data
const mockCharities = [
  {
    id: 1,
    name: "Global Food Bank",
    status: "Active",
    totalRaised: 25000,
    targetAmount: 50000,
    donors: 150,
    lastUpdated: "2024-03-15",
    verificationStatus: "Verified"
  },
  {
    id: 2,
    name: "Education For All",
    status: "Pending",
    totalRaised: 0,
    targetAmount: 100000,
    donors: 0,
    lastUpdated: "2024-03-14",
    verificationStatus: "Under Review"
  },
  {
    id: 3,
    name: "Clean Water Initiative",
    status: "Completed",
    totalRaised: 75000,
    targetAmount: 75000,
    donors: 300,
    lastUpdated: "2024-03-10",
    verificationStatus: "Verified"
  }
]

export default function FundraiserDashboard() {
  const [activeTab, setActiveTab] = useState('overview')

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
                    {mockCharities.map(charity => (
                      <div key={charity.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
                        <div>
                          <p className="font-medium text-slate-800">{charity.name}</p>
                          <p className="text-sm text-slate-600">
                            {charity.status === 'Active' ? 'Raised $' + charity.totalRaised : 'Status: ' + charity.status}
                          </p>
                        </div>
                        <span className="text-sm text-slate-600">{charity.lastUpdated}</span>
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
                  {mockCharities.map(charity => (
                    <div key={charity.id} className="p-4 bg-gray-50 rounded-xl">
                      <div className="flex justify-between items-start mb-3">
                        <div>
                          <h3 className="!text-base !mb-1">{charity.name}</h3>
                          <div className="flex items-center space-x-4 text-sm text-slate-600">
                            <span>Target: ${charity.targetAmount}</span>
                            <span>Donors: {charity.donors}</span>
                          </div>
                        </div>
                        <div className="flex flex-col items-end space-y-2">
                          <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium
                            ${charity.status === 'Active' ? 'bg-green-100 text-green-800' :
                              charity.status === 'Pending' ? 'bg-yellow-100 text-yellow-800' :
                              'bg-gray-100 text-gray-800'}`}>
                            {charity.status}
                          </span>
                          <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium
                            ${charity.verificationStatus === 'Verified' ? 'bg-green-100 text-green-800' :
                              'bg-yellow-100 text-yellow-800'}`}>
                            {charity.verificationStatus}
                          </span>
                        </div>
                      </div>
                      <div className="flex justify-between items-center">
                        <div className="w-full bg-gray-200 rounded-full h-2.5">
                          <div 
                            className="bg-violet-600 h-2.5 rounded-full" 
                            style={{ width: `${(charity.totalRaised / charity.targetAmount) * 100}%` }}
                          ></div>
                        </div>
                        <span className="ml-4 text-sm text-slate-600">
                          ${charity.totalRaised} raised
                        </span>
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