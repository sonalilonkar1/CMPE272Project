'use client'

import { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import Link from 'next/link'
import { useSearchParams } from 'next/navigation'

export default function DonorDashboard() {
  const dispatch = useDispatch()
  const searchParams = useSearchParams()
  const currentTab = searchParams.get('tab') || 'overview'

  const tabs = [
    { id: 'overview', label: 'Profile', href: '/donor?tab=overview' },
    { id: 'impact', label: 'Impact', href: '/donor?tab=impact' },
    { id: 'volunteer', label: 'Updates', href: '/donor?tab=volunteer' },
  ]

  // Mock data for testing
  const mockDonor = {
    name: 'John Doe',
    email: 'john@example.com',
    totalDonations: 5,
    totalAmount: 2500,
    joinedDate: '2024-01-15',
    impactStats: {
      charities: 3,
      peopleHelped: 150,
      projectsSupported: 4
    },
    isVolunteer: false
  }

  const mockDonations = [
    {
      id: 1,
      charityName: 'Global Education Initiative',
      amount: 1000,
      date: '2024-03-01',
      impact: 'Helped provide education to 20 children',
      status: 'COMPLETED'
    },
    {
      id: 2,
      charityName: 'Clean Water Project',
      amount: 500,
      date: '2024-02-15',
      impact: 'Provided clean water to 5 families',
      status: 'COMPLETED'
    }
  ]

  const mockVolunteerUpdates = [
    {
      id: 1,
      charityName: 'Global Food Bank',
      message: 'Volunteer opportunity: Food distribution this weekend',
      date: '2024-03-10T10:00:00Z',
      type: 'EVENT',
      location: '123 Main St, City',
      date: '2024-03-15'
    },
    {
      id: 2,
      charityName: 'Education for All',
      message: 'Looking for volunteers to help with after-school tutoring',
      date: '2024-03-08T15:30:00Z',
      type: 'OPPORTUNITY',
      location: '456 School Ave, Town',
      date: '2024-03-20'
    }
  ]

  const [isVolunteer, setIsVolunteer] = useState(mockDonor.isVolunteer)
  const [selectedUpdate, setSelectedUpdate] = useState(null)
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false)
  const [rating, setRating] = useState(0)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleVolunteerSignup = () => {
    setIsVolunteer(true)
    // Here you would typically make an API call to update the user's volunteer status
  }

  // Mock updates data
  const mockUpdates = [
    {
      id: 1,
      title: 'Updates (1)',
      date: '1 May 2025',
      organizer: 'Kaley Dixon',
      role: 'Organiser',
      message: 'Thankyou all who have shared the go fund me link today and all who have donated, the family are very grateful for everyone&apos;s support it means the world to them.',
      files: [
        {
          name: 'progress_report.pdf',
          url: '/files/progress_report.pdf',
          type: 'document'
        },
        {
          name: 'update_image.jpg',
          url: '/images/update1.jpg',
          type: 'image'
        }
      ]
    },
    {
      id: 2,
      title: 'Updates (2)',
      date: '30 April 2025',
      organizer: 'Kaley Dixon',
      role: 'Organiser',
      message: 'We have reached our first milestone! Here are some photos of the supplies we have purchased.',
      files: [
        {
          name: 'milestone_photos.jpg',
          url: '/images/milestone.jpg',
          type: 'image'
        }
      ]
    }
  ]

  const handleOpenUpdateModal = (update) => {
    setSelectedUpdate(update)
    setIsUpdateModalOpen(true)
  }

  const handleRatingChange = (newRating) => {
    setRating(newRating)
  }

  const handleReviewSubmit = () => {
    setIsSubmitting(true)
    // Here you would make an API call to submit the review
    console.log('Submitting rating:', { rating, updateId: selectedUpdate?.id })
    setTimeout(() => {
      setIsSubmitting(false)
      handleCloseUpdateModal()
      // Reset form
      setRating(0)
    }, 1000)
  }

  const handleCloseUpdateModal = () => {
    setSelectedUpdate(null)
    setIsUpdateModalOpen(false)
    setRating(0)
  }

  // Thumbs rating component
  const ThumbsRating = ({ rating, onRatingChange }) => {
    return (
      <div className="flex items-center space-x-1 flex-wrap gap-2">
        {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((thumb) => (
          <button
            key={thumb}
            type="button"
            className="focus:outline-none transform transition-transform hover:scale-110"
            onClick={() => onRatingChange(thumb)}
          >
            <svg
              className={`w-6 h-6 ${
                thumb <= rating ? 'text-violet-600' : 'text-gray-300'
              }`}
              fill="currentColor"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={thumb <= rating ? 0 : 1}
                d="M14 10h4.764a2 2 0 011.789 2.894l-3.5 7A2 2 0 0115.263 21h-4.017c-.163 0-.326-.02-.485-.06L7 20m7-10V5a2 2 0 00-2-2h-.095c-.5 0-.905.405-.905.905 0 .714-.211 1.412-.608 2.006L7 11v9m7-10h-2M7 20H5a2 2 0 01-2-2v-6a2 2 0 012-2h2.5"
              />
            </svg>
          </button>
        ))}
        {rating > 0 && (
          <span className="text-sm text-violet-600 font-medium ml-2">
            {rating}/10
          </span>
        )}
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-900">
            {currentTab === 'overview' ? 'Your Profile' : 
             currentTab === 'impact' ? 'Your Impact' : 
             'Charity Updates'}
          </h1>
          <p className="mt-2 text-sm text-gray-600">
            {currentTab === 'overview'
              ? 'Manage your profile and account settings'
              : currentTab === 'impact'
              ? 'Track your donations and see the impact you\'ve made'
              : 'Stay informed about charity progress and help other donors'
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
              {/* Profile Content */}
              <div className="grid grid-cols-1 gap-6">
                <div className="bg-white p-6 rounded-lg border border-gray-200">
                  <div className="flex items-center space-x-4 mb-6">
                    <div className="w-16 h-16 rounded-full bg-violet-600 text-white flex items-center justify-center text-2xl font-medium">
                      {mockDonor.name[0]}
                    </div>
                    <div>
                      <h2 className="text-xl font-semibold text-gray-900">{mockDonor.name}</h2>
                      <p className="text-sm text-gray-500">{mockDonor.email}</p>
                      <p className="text-sm text-gray-500">Member since {new Date(mockDonor.joinedDate).toLocaleDateString()}</p>
                    </div>
                  </div>
                  
                  <div className="border-t border-gray-200 pt-6">
                    <h3 className="text-lg font-medium text-gray-900 mb-4">Account Information</h3>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700">Name</label>
                        <input
                          type="text"
                          value={mockDonor.name}
                          readOnly
                          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-violet-500 focus:ring-violet-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">Email</label>
                        <input
                          type="email"
                          value={mockDonor.email}
                          readOnly
                          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-violet-500 focus:ring-violet-500"
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ) : currentTab === 'impact' ? (
            <div className="space-y-6">
              {/* Impact Stats */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                <div className="bg-violet-50 p-6 rounded-lg">
                  <h3 className="text-lg font-medium text-violet-900">Total Donations</h3>
                  <p className="mt-2 text-3xl font-bold text-violet-600">${mockDonor.totalAmount}</p>
                  <p className="text-sm text-violet-600">{mockDonor.totalDonations} donations made</p>
                </div>
                <div className="bg-green-50 p-6 rounded-lg">
                  <h3 className="text-lg font-medium text-green-900">People Helped</h3>
                  <p className="mt-2 text-3xl font-bold text-green-600">{mockDonor.impactStats.peopleHelped}</p>
                  <p className="text-sm text-green-600">Lives impacted</p>
                </div>
                <div className="bg-blue-50 p-6 rounded-lg">
                  <h3 className="text-lg font-medium text-blue-900">Projects Supported</h3>
                  <p className="mt-2 text-3xl font-bold text-blue-600">{mockDonor.impactStats.projectsSupported}</p>
                  <p className="text-sm text-blue-600">{mockDonor.impactStats.charities} charities</p>
                </div>
              </div>

              {/* Donations List */}
              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-4">Your Donations</h3>
                <div className="space-y-4">
                  {mockDonations.map((donation) => (
                    <div key={donation.id} className="bg-gray-50 rounded-lg p-6">
                      <div className="flex justify-between items-start mb-4">
                        <div>
                          <h4 className="text-lg font-medium text-gray-900">{donation.charityName}</h4>
                          <p className="text-sm text-gray-500">
                            {new Date(donation.date).toLocaleDateString()}
                          </p>
                        </div>
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                          ${donation.amount}
                        </span>
                      </div>
                      <p className="text-sm text-gray-600">{donation.impact}</p>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          ) : (
            <div className="space-y-6">
              {!isVolunteer ? (
                <div className="text-center py-12">
                  <h3 className="text-lg font-medium text-gray-900 mb-4">Become a Volunteer</h3>
                  <p className="text-gray-600 mb-6">
                    Join our volunteer community to review and verify charity updates. Your insights help other donors 
                    understand how their contributions are making a difference.
                  </p>
                  <button
                    onClick={handleVolunteerSignup}
                    className="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-violet-600 hover:bg-violet-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-violet-500"
                  >
                    Sign Up as Volunteer
                  </button>
                </div>
              ) : (
                <div className="space-y-6">
                  <div className="flex justify-between items-center">
                    <h3 className="text-lg font-medium text-gray-900">Recent Updates</h3>
                    <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                      Active Volunteer
                    </span>
                  </div>
                  
                  <div className="space-y-4">
                    {mockUpdates.map((update) => (
                      <div 
                        key={update.id} 
                        className="bg-gray-50 rounded-lg p-6 cursor-pointer hover:bg-gray-100 transition-colors duration-200"
                        onClick={() => handleOpenUpdateModal(update)}
                      >
                        <div className="space-y-4">
                          <div>
                            <h4 className="text-xl font-semibold text-gray-900">{update.title}</h4>
                            <div className="mt-1 flex items-center text-sm text-gray-600">
                              <span className="font-medium">{update.date}</span>
                              <span className="mx-2">•</span>
                              <span>by {update.organizer}, {update.role}</span>
                            </div>
                          </div>
                          <p className="text-gray-600 line-clamp-2">{update.message}</p>
                          {update.files.length > 0 && (
                            <div className="flex items-center space-x-2">
                              <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
                              </svg>
                              <span className="text-sm text-gray-500">{update.files.length} attachment{update.files.length !== 1 ? 's' : ''}</span>
                            </div>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Update Detail Modal */}
      {isUpdateModalOpen && selectedUpdate && (
        <div className="fixed inset-0 z-50 overflow-y-auto">
          <div className="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0">
            <div className="fixed inset-0 transition-opacity" aria-hidden="true">
              <div className="absolute inset-0 bg-gray-500 opacity-75" onClick={handleCloseUpdateModal}></div>
            </div>

            <div className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
              <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                <div className="sm:flex sm:items-start">
                  <div className="mt-3 text-center sm:mt-0 sm:text-left w-full">
                    <h3 className="text-xl font-semibold text-gray-900 mb-2">
                      {selectedUpdate.title}
                    </h3>
                    <div className="mb-4 flex items-center text-sm text-gray-600">
                      <span className="font-medium">{selectedUpdate.date}</span>
                      <span className="mx-2">•</span>
                      <span>by {selectedUpdate.organizer}, {selectedUpdate.role}</span>
                    </div>
                    <p className="text-gray-600 mb-4">{selectedUpdate.message}</p>
                    {selectedUpdate.files.length > 0 && (
                      <div className="border-t border-gray-200 pt-4 mb-6">
                        <h4 className="text-sm font-medium text-gray-900 mb-2">Attachments</h4>
                        <div className="space-y-2">
                          {selectedUpdate.files.map((file, index) => (
                            <a
                              key={index}
                              href={file.url}
                              target="_blank"
                              rel="noopener noreferrer"
                              className="flex items-center p-2 rounded-lg hover:bg-gray-50"
                            >
                              <svg className="h-5 w-5 text-gray-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
                              </svg>
                              <span className="text-sm text-violet-600 hover:text-violet-700">{file.name}</span>
                            </a>
                          ))}
                        </div>
                      </div>
                    )}

                    {/* Volunteer Review Section */}
                    <div className="border-t border-gray-200 pt-4">
                      <h4 className="text-sm font-medium text-gray-900 mb-4">Rate this Update</h4>
                      <div className="space-y-4">
                        <div>
                          <ThumbsRating rating={rating} onRatingChange={handleRatingChange} />
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
                <button
                  type="button"
                  className="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-violet-600 text-base font-medium text-white hover:bg-violet-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-violet-500 sm:ml-3 sm:w-auto sm:text-sm"
                  onClick={handleReviewSubmit}
                  disabled={isSubmitting || rating === 0}
                >
                  {isSubmitting ? 'Submitting...' : 'Submit Rating'}
                </button>
                <button
                  type="button"
                  className="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-violet-500 sm:mt-0 sm:w-auto sm:text-sm"
                  onClick={handleCloseUpdateModal}
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
} 