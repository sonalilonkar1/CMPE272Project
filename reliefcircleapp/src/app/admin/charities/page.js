'use client'
import { useState } from 'react'

// Mock data - replace with actual API data
const mockPendingCharities = [
  {
    id: 1,
    name: "Global Food Bank",
    category: "Humanitarian Aid",
    submittedDate: "2024-03-15",
    status: "Pending",
    contactName: "John Smith",
    contactEmail: "john@globalfoodbank.org",
    targetAmount: 50000,
    location: "New York, USA"
  },
  {
    id: 2,
    name: "Education For All",
    category: "Education",
    submittedDate: "2024-03-14",
    status: "Pending",
    contactName: "Sarah Johnson",
    contactEmail: "sarah@educationforall.org",
    targetAmount: 100000,
    location: "London, UK"
  }
]

const mockApprovedCharities = [
  {
    id: 3,
    name: "Clean Water Initiative",
    category: "Environment",
    submittedDate: "2024-03-10",
    status: "Approved",
    contactName: "Michael Brown",
    contactEmail: "michael@cleanwater.org",
    targetAmount: 75000,
    location: "Sydney, Australia"
  }
]

const mockRejectedCharities = [
  {
    id: 4,
    name: "Tech for Good",
    category: "Education",
    submittedDate: "2024-03-12",
    status: "Rejected",
    contactName: "David Wilson",
    contactEmail: "david@techforgood.org",
    targetAmount: 200000,
    location: "San Francisco, USA"
  }
]

export default function AdminCharities() {
  const [activeTab, setActiveTab] = useState('pending')
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedCharity, setSelectedCharity] = useState(null)
  const [showDetailsModal, setShowDetailsModal] = useState(false)

  const handleApprove = (charityId) => {
    // Handle approval logic
    console.log('Approving charity:', charityId)
  }

  const handleReject = (charityId) => {
    // Handle rejection logic
    console.log('Rejecting charity:', charityId)
  }

  const getCharitiesByStatus = () => {
    switch (activeTab) {
      case 'pending':
        return mockPendingCharities
      case 'approved':
        return mockApprovedCharities
      case 'rejected':
        return mockRejectedCharities
      default:
        return []
    }
  }

  const filteredCharities = getCharitiesByStatus().filter(charity =>
    charity.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    charity.category.toLowerCase().includes(searchQuery.toLowerCase()) ||
    charity.location.toLowerCase().includes(searchQuery.toLowerCase())
  )

  return (
    <main className="min-h-screen bg-gray-50">
      <div className="container py-8">
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Sidebar */}
          <div className="lg:w-64 space-y-4">
            <div className="card !p-4">
              <nav className="space-y-1">
                <button
                  onClick={() => setActiveTab('pending')}
                  className={`w-full text-left px-4 py-2 rounded-xl text-sm font-medium transition-colors
                    ${activeTab === 'pending' 
                      ? 'bg-violet-50 text-violet-600' 
                      : 'text-slate-600 hover:bg-gray-50'}`}
                >
                  Pending Review
                </button>
                <button
                  onClick={() => setActiveTab('approved')}
                  className={`w-full text-left px-4 py-2 rounded-xl text-sm font-medium transition-colors
                    ${activeTab === 'approved' 
                      ? 'bg-violet-50 text-violet-600' 
                      : 'text-slate-600 hover:bg-gray-50'}`}
                >
                  Approved
                </button>
                <button
                  onClick={() => setActiveTab('rejected')}
                  className={`w-full text-left px-4 py-2 rounded-xl text-sm font-medium transition-colors
                    ${activeTab === 'rejected' 
                      ? 'bg-violet-50 text-violet-600' 
                      : 'text-slate-600 hover:bg-gray-50'}`}
                >
                  Rejected
                </button>
              </nav>
            </div>
          </div>

          {/* Main Content */}
          <div className="flex-1 space-y-6">
            {/* Search and Stats */}
            <div className="card">
              <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                <div className="w-full md:w-64">
                  <input
                    type="text"
                    placeholder="Search charities..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="w-full px-4 py-2 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-violet-500"
                  />
                </div>
                <div className="flex items-center space-x-4">
                  <div className="text-center">
                    <p className="text-2xl font-bold text-slate-800">{mockPendingCharities.length}</p>
                    <p className="text-sm text-slate-600">Pending</p>
                  </div>
                  <div className="text-center">
                    <p className="text-2xl font-bold text-slate-800">{mockApprovedCharities.length}</p>
                    <p className="text-sm text-slate-600">Approved</p>
                  </div>
                  <div className="text-center">
                    <p className="text-2xl font-bold text-slate-800">{mockRejectedCharities.length}</p>
                    <p className="text-sm text-slate-600">Rejected</p>
                  </div>
                </div>
              </div>
            </div>

            {/* Charities List */}
            <div className="card">
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                    <tr className="border-b border-gray-200">
                      <th className="text-left py-3 px-4 text-sm font-medium text-slate-600">Charity Name</th>
                      <th className="text-left py-3 px-4 text-sm font-medium text-slate-600">Category</th>
                      <th className="text-left py-3 px-4 text-sm font-medium text-slate-600">Location</th>
                      <th className="text-left py-3 px-4 text-sm font-medium text-slate-600">Target Amount</th>
                      <th className="text-left py-3 px-4 text-sm font-medium text-slate-600">Submitted Date</th>
                      <th className="text-left py-3 px-4 text-sm font-medium text-slate-600">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredCharities.map(charity => (
                      <tr key={charity.id} className="border-b border-gray-100">
                        <td className="py-3 px-4">
                          <div>
                            <p className="font-medium text-slate-800">{charity.name}</p>
                            <p className="text-sm text-slate-600">{charity.contactEmail}</p>
                          </div>
                        </td>
                        <td className="py-3 px-4">{charity.category}</td>
                        <td className="py-3 px-4">{charity.location}</td>
                        <td className="py-3 px-4">${charity.targetAmount.toLocaleString()}</td>
                        <td className="py-3 px-4">{charity.submittedDate}</td>
                        <td className="py-3 px-4">
                          <div className="flex items-center space-x-2">
                            <button
                              onClick={() => {
                                setSelectedCharity(charity)
                                setShowDetailsModal(true)
                              }}
                              className="text-violet-600 hover:text-violet-700 text-sm font-medium"
                            >
                              View Details
                            </button>
                            {activeTab === 'pending' && (
                              <>
                                <button
                                  onClick={() => handleApprove(charity.id)}
                                  className="text-green-600 hover:text-green-700 text-sm font-medium"
                                >
                                  Approve
                                </button>
                                <button
                                  onClick={() => handleReject(charity.id)}
                                  className="text-red-600 hover:text-red-700 text-sm font-medium"
                                >
                                  Reject
                                </button>
                              </>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Details Modal */}
      {showDetailsModal && selectedCharity && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex justify-between items-start mb-6">
                <h2 className="!text-xl !mb-0">{selectedCharity.name}</h2>
                <button
                  onClick={() => setShowDetailsModal(false)}
                  className="text-gray-400 hover:text-gray-500"
                >
                  <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              <div className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-slate-600">Category</p>
                    <p className="font-medium">{selectedCharity.category}</p>
                  </div>
                  <div>
                    <p className="text-sm text-slate-600">Location</p>
                    <p className="font-medium">{selectedCharity.location}</p>
                  </div>
                  <div>
                    <p className="text-sm text-slate-600">Target Amount</p>
                    <p className="font-medium">${selectedCharity.targetAmount.toLocaleString()}</p>
                  </div>
                  <div>
                    <p className="text-sm text-slate-600">Submitted Date</p>
                    <p className="font-medium">{selectedCharity.submittedDate}</p>
                  </div>
                </div>

                <div>
                  <p className="text-sm text-slate-600">Contact Information</p>
                  <div className="mt-2 space-y-1">
                    <p className="font-medium">{selectedCharity.contactName}</p>
                    <p className="text-slate-600">{selectedCharity.contactEmail}</p>
                  </div>
                </div>

                <div>
                  <p className="text-sm text-slate-600">Documents</p>
                  <div className="mt-2 space-y-2">
                    <div className="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
                      <div>
                        <p className="font-medium">Registration Certificate</p>
                        <p className="text-sm text-slate-600">PDF, 2.4 MB</p>
                      </div>
                      <button className="text-violet-600 hover:text-violet-700 text-sm font-medium">
                        Download
                      </button>
                    </div>
                    <div className="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
                      <div>
                        <p className="font-medium">Tax Exemption Proof</p>
                        <p className="text-sm text-slate-600">PDF, 1.8 MB</p>
                      </div>
                      <button className="text-violet-600 hover:text-violet-700 text-sm font-medium">
                        Download
                      </button>
                    </div>
                  </div>
                </div>

                {activeTab === 'pending' && (
                  <div className="flex justify-end space-x-4 pt-6">
                    <button
                      onClick={() => handleApprove(selectedCharity.id)}
                      className="btn-primary"
                    >
                      Approve
                    </button>
                    <button
                      onClick={() => handleReject(selectedCharity.id)}
                      className="btn-outline border-red-600 text-red-600 hover:bg-red-50"
                    >
                      Reject
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </main>
  )
} 