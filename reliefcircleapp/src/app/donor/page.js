'use client'

import { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import Link from 'next/link'
import { useSearchParams } from 'next/navigation'
import { fetchDonorDonations } from '@/redux/features/donationsSlice'
import { fetchVolunteerUpdates, rateUpdate } from '@/redux/features/updatesSlice'
import { signupAsVolunteer, fetchDonorStats } from '@/redux/features/userSlice'
import DonationModal from '@/components/DonationModal'
import { showToast } from '@/components/Toast'

export default function DonorDashboard() {
  const dispatch = useDispatch()
  const searchParams = useSearchParams()
  const currentTab = searchParams.get('tab') || 'overview'
  const { donations, status, error } = useSelector((state) => state.donations)
  const { profile, volunteerStatus, donorStats } = useSelector((state) => state.user)
  const { volunteerUpdates, status: updatesStatus, error: updatesError } = useSelector((state) => state.updates)
  const [selectedCharity, setSelectedCharity] = useState(null)
  const [isDonationModalOpen, setIsDonationModalOpen] = useState(false)
  const [isVolunteer, setIsVolunteer] = useState(profile?.isVolunteer)
  const [selectedUpdate, setSelectedUpdate] = useState(null)
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false)
  const [rating, setRating] = useState(0)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const paymentStatus = searchParams.get('payment')

  const tabs = [
    { id: 'overview', label: 'Profile', href: '/donor?tab=overview' },
    { id: 'impact', label: 'Impact', href: '/donor?tab=impact' },
    { id: 'volunteer', label: 'Updates', href: '/donor?tab=volunteer' },
  ]

  const donor = profile || {
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

  useEffect(() => {
    if (currentTab === 'impact' && profile?.id && profile?.token) {
      dispatch(fetchDonorDonations({donorId: profile?.id, token: profile?.token}))
    }
  }, [dispatch, currentTab, profile])

  useEffect(() => {
    if (currentTab === 'volunteer' && profile?.token) {
      dispatch(fetchVolunteerUpdates({ token: profile.token }))
    }
  }, [dispatch, currentTab, profile?.token])

  useEffect(() => {
    if (paymentStatus === 'success') {
      showToast.success('Thank you for your donation!')
    } else if (paymentStatus === 'cancelled') {
      showToast.info('Donation was cancelled')
    }
  }, [paymentStatus])

  useEffect(() => {
    if (profile?.isVolunteer) {
      setIsVolunteer(true)
    }
  }, [profile?.isVolunteer])

  useEffect(() => {
    if (profile?.token) {
      dispatch(fetchDonorStats({ token: profile.token }))
    }
  }, [dispatch, profile?.token])

  // Calculate impact stats
  const totalDonations = donations.length > 0 
    ? donations.reduce((sum, donation) => sum + donation.amount, 0)
    : donor.totalAmount

  const charitiesSupported = donations.length > 0
    ? new Set(donations.map(d => d.charity)).size
    : donor.impactStats?.charities

  const peopleHelped = donations.length > 0
    ? Math.round(totalDonations / 100) // Simple calculation: assume $100 helps one person
    : donor.impactStats?.peopleHelped

  const projectsSupported = donations.length > 0
    ? Math.round(totalDonations / 500) // Simple calculation: assume $500 supports one project
    : donor.impactStats?.projectsSupported

  const handleVolunteerSignup = async () => {
    if (!profile?.token) {
      showToast.error('Please sign in to become a volunteer')
      return
    }

    try {
      await dispatch(signupAsVolunteer({ token: profile.token })).unwrap()
      setIsVolunteer(true)
    } catch (error) {
      console.error('Failed to sign up as volunteer:', error)
    }
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

  const handleReviewSubmit = async () => {
    setIsSubmitting(true)
    try {
      await dispatch(rateUpdate({
        updateId: selectedUpdate.id,
        rating,
        token: profile.token
      })).unwrap()
      setIsSubmitting(false)
      handleCloseUpdateModal()
      setRating(0)
      dispatch(fetchVolunteerUpdates({ token: profile.token }))
    } catch (error) {
      setIsSubmitting(false)
      // Optionally show error toast here, but the thunk already does
    }
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

  const handleOpenDonationModal = (charity) => {
    setSelectedCharity(charity)
    setIsDonationModalOpen(true)
  }

  const handleCloseDonationModal = () => {
    setSelectedCharity(null)
    setIsDonationModalOpen(false)
  }

  // Mock charities data - replace with actual data from your API
  const mockCharities = [
    {
      id: '1',
      name: 'Global Food Bank',
      description: 'Providing food security to communities in need',
      targetAmount: 50000,
      raisedAmount: 25000,
      image: '/images/charity1.jpg'
    },
    {
      id: '2',
      name: 'Education for All',
      description: 'Making quality education accessible to all children',
      targetAmount: 75000,
      raisedAmount: 45000,
      image: '/images/charity2.jpg'
    }
  ]

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-900 mb-2 mt-10">
            {currentTab === 'overview' ? 'Your Profile' : 
             currentTab === 'impact' ? 'Your Impact' : 
             'Charity Updates'}
          </h1>
          <p className="text-base text-gray-600">
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
                      {profile?.fullName[0]}
                    </div>
                    <div>
                      <h2 className="text-xl font-semibold text-gray-900">{profile?.fullName}</h2>
                      <p className="text-sm text-gray-500">{profile?.email}</p>
                      <p className="text-sm text-gray-500">Member since {profile?.joinedDate && new Date(profile.joinedDate).toLocaleDateString()}</p>
                    </div>
                  </div>
                  
                  <div className="border-t border-gray-200 pt-6">
                    <h3 className="text-lg font-medium text-gray-900 mb-4">Account Information</h3>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700">Name</label>
                        <input
                          type="text"
                          value={profile?.fullName}
                          readOnly
                          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-violet-500 focus:ring-violet-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">Email</label>
                        <input
                          type="email"
                          value={profile?.email}
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
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
                <div className="bg-violet-50 p-6 rounded-lg">
                  <h3 className="text-lg font-medium text-violet-900">Total Donated</h3>
                  <p className="mt-2 text-3xl font-bold text-violet-600">${donorStats?.totalDonated?.toLocaleString()}</p>
                </div>
                <div className="bg-blue-50 p-6 rounded-lg">
                  <h3 className="text-lg font-medium text-blue-900">Charities Supported</h3>
                  <p className="mt-2 text-3xl font-bold text-blue-600">{donorStats?.totalCharitiesSupported}</p>
                </div>
              </div>

              {/* Donations List */}
              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-4">Your Donations</h3>
                <div className="space-y-4">
                  {status === 'loading' ? (
                    <div className="flex items-center justify-center py-8">
                      <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-violet-600"></div>
                    </div>
                  ) : error ? (
                    <div className="text-center text-red-600 py-4">
                      {error}
                    </div>
                  ) : (
                    donations.map((donation) => (
                      <div key={donation.id} className="bg-gray-50 rounded-lg p-6">
                        <div className="flex justify-between items-start mb-4">
                          <div>
                            <h4 className="text-lg font-medium text-gray-900">{donation.charityName}</h4>
                            <p className="text-sm text-gray-500">
                              {donation.createdAt ? new Date(donation.createdAt).toLocaleDateString() : ''}
                            </p>
                          </div>
                          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                            ${donation.amount}
                          </span>
                        </div>
                        <p className="text-sm text-gray-600">
                          Status: {"Confirmed" || 'N/A'}
                        </p>
                      </div>
                    ))
                  )}
                </div>
              </div>
            </div>
          ) : (
            <div className="space-y-6">
              {!profile?.isVolunteer ? (
                <div className="text-center py-12">
                  <h3 className="text-lg font-medium text-gray-900 mb-4">Become a Volunteer</h3>
                  <p className="text-gray-600 mb-6">
                    Join our volunteer community to review and verify charity updates. Your insights help other donors 
                    understand how their contributions are making a difference.
                  </p>
                  <button
                    onClick={handleVolunteerSignup}
                    disabled={volunteerStatus === 'loading'}
                    className={`inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-md shadow-sm text-white 
                      ${volunteerStatus === 'loading' 
                        ? 'bg-violet-400 cursor-not-allowed' 
                        : 'bg-violet-600 hover:bg-violet-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-violet-500'
                      }`}
                  >
                    {volunteerStatus === 'loading' ? 'Signing up...' : 'Sign Up as Volunteer'}
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
                    {updatesStatus === 'loading' ? (
                      <div className="text-center py-4">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-violet-600 mx-auto"></div>
                      </div>
                    ) : updatesError ? (
                      <div className="text-center text-red-600 py-4">{updatesError}</div>
                    ) : Array.isArray(volunteerUpdates) && volunteerUpdates.length === 0 ? (
                      <div className="text-center py-8">
                        <p className="text-gray-500">No updates found.</p>
                      </div>
                    ) : (
                      Array.isArray(volunteerUpdates) && volunteerUpdates.map((update) => (
                        <div 
                          key={update.id} 
                          className="bg-gray-50 rounded-lg p-6 cursor-pointer hover:bg-gray-100 transition-colors duration-200"
                          onClick={() => handleOpenUpdateModal(update)}
                        >
                          <div className="flex justify-between items-center w-full">
                            {/* Left side: date, rating, attachment */}
                            <div className="flex-1 min-w-[320px] flex flex-col justify-between">
                              <div>
                                <div className="font-regular text-lg text-gray-900">
                                  {update.charityName ? update.charityName : 'No Charity Name'}
                                </div>
                                <div className="text-gray-600 mt-2">
                                  {update.updateText ? update.updateText : 'No update message'}
                                </div>
                                <div className="flex items-center gap-6 mt-1">
                                  <span className="font-regular text-sm text-gray-900">
                                    {update.createdAt
                                      ? new Date(update.createdAt).toLocaleDateString('en-US', {
                                          year: 'numeric',
                                          month: 'long',
                                          day: 'numeric'
                                        })
                                      : 'Unknown date'}
                                  </span>
                                  {update.fileUrl && (
                                    <a
                                      href={update.fileUrl}
                                      target="_blank"
                                      rel="noopener noreferrer"
                                      className="text-violet-600 hover:underline whitespace-nowrap"
                                    >
                                      View Attachment
                                    </a>
                                  )}
                                </div>
                              </div>
                            </div>
                            
                            {/* Right side: button */}
                            {update.rating === 0 ? (
                              <button
                                className="px-6 py-3 bg-violet-600 text-white rounded hover:bg-violet-700 transition"
                                onClick={(e) => {
                                  e.stopPropagation()
                                  handleOpenUpdateModal(update)
                                }}
                              >
                                Show Your Support
                              </button>
                            ): <div className="w-full flex justify-end mt-1">
                                {typeof update.rating === 'number' && update.rating > 0 ? (
                                  <div className="flex items-center">
                                    {[...Array(Math.round(update.rating))].map((_, i) => (
                                      <svg
                                        key={i}
                                        className="w-5 h-5 text-violet-600"
                                        fill="currentColor"
                                        viewBox="0 0 24 24"
                                      >
                                        <path d="M14 10h4.764a2 2 0 011.789 2.894l-3.5 7A2 2 0 0115.263 21h-4.017c-.163 0-.326-.02-.485-.06L7 20m7-10V5a2 2 0 00-2-2h-.095c-.5 0-.905.405-.905.905 0 .714-.211 1.412-.608 2.006L7 11v9m7-10h-2M7 20H5a2 2 0 01-2-2v-6a2 2 0 012-2h2.5" />
                                      </svg>
                                    ))}
                                    <span className="ml-2 text-sm text-gray-600">{update.rating}</span>
                                  </div>
                                ) : (
                                  <span className="text-sm text-gray-600">N/A</span>
                                )}
                              </div>}
                          </div>
                        </div>
                      ))
                    )}
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
                      {selectedUpdate.charityName || 'No Charity Name'}
                    </h3>
                    <div className="mb-4 flex items-center text-sm text-gray-600">
                      <span className="font-medium">
                        {selectedUpdate.createdAt
                          ? new Date(selectedUpdate.createdAt).toLocaleDateString('en-US', {
                              year: 'numeric',
                              month: 'long',
                              day: 'numeric'
                            })
                          : 'Unknown date'}
                      </span>
                    </div>
                    {selectedUpdate.updateText && (
                      <p className="text-gray-600 mb-4">{selectedUpdate.updateText}</p>
                    )}
                    {selectedUpdate.fileUrl && (
                      <div className="border-t border-gray-200 pt-4 mb-6">
                        <h4 className="text-sm font-medium text-gray-900 mb-2">Attachment</h4>
                        <a
                          href={selectedUpdate.fileUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="text-violet-600 hover:underline"
                        >
                          View Attachment
                        </a>
                      </div>
                    )}
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

      {/* Donation Modal */}
      <DonationModal
        isOpen={isDonationModalOpen}
        onClose={handleCloseDonationModal}
        charity={selectedCharity}
      />
    </div>
  )
} 