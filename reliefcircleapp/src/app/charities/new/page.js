'use client'
import { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useRouter } from 'next/navigation'
import { createCharity } from '@/redux/features/charitiesSlice'
import { CHARITY_CATEGORIES } from '@/utils/constants'

export default function NewCharityForm() {
  const dispatch = useDispatch()
  const router = useRouter()
  const { status, error } = useSelector((state) => state.charities)
  const { profile } = useSelector((state) => state.user)
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    organizationName: '',
    targetAmount: '',
    category: '',
    location: '',
    website: '',
    documents: [],
  })
  const [selectedFile, setSelectedFile] = useState(null)
  const [fileError, setFileError] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (fileError) {
      return
    }
    try {
      const result = await dispatch(createCharity({
        charityData: formData,
        token: profile?.token
      })).unwrap()
      if (result) {
        router.push('/fundraiser')
      }
    } catch (error) {
      console.error('Failed to create charity:', error)
    }
  }

  const handleChange = (e) => {
    const { name, value } = e.target
    if (name.includes('.')) {
      const [parent, child] = name.split('.')
      setFormData(prev => ({
        ...prev,
        [parent]: {
          ...prev[parent],
          [child]: value
        }
      }))
    } else {
      setFormData(prev => ({
        ...prev,
        [name]: value
      }))
    }
  }

  const handleFileChange = (e) => {
    const file = e.target.files[0]
    setFileError('')
    
    if (file) {
      // Check file type
      const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
      if (!allowedTypes.includes(file.type)) {
        setFileError('Please upload an image file (JPG, PNG, GIF, or WEBP)')
        return
      }

      // Check file size (5MB)
      const maxSize = 5 * 1024 * 1024 // 5MB in bytes
      if (file.size > maxSize) {
        setFileError('Image size should be less than 5MB')
        return
      }

      setSelectedFile(file)
      setFormData(prev => ({
        ...prev,
        documents: [file]
      }))
    }
  }

  const handleRemoveFile = () => {
    setSelectedFile(null)
    setFormData(prev => ({
      ...prev,
      documents: []
    }))
    setFileError('')
  }

  return (
    <main className="min-h-screen bg-gray-50 mt-16">
      <div className="container py-8">
        <div className="max-w-3xl mx-auto">
          <div className="card">
            <h1 className="!text-2xl !mb-6">Submit New Charity Request</h1>
            
            <form onSubmit={handleSubmit} className="space-y-6">
              {/* Basic Information */}
              <div className="space-y-4">
                <h2 className="!text-lg !mb-4">Basic Information</h2>
                
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Charity Name *
                  </label>
                  <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-violet-500"
                    placeholder="Enter charity name"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Description *
                  </label>
                  <textarea
                    name="description"
                    value={formData.description}
                    onChange={handleChange}
                    required
                    rows={4}
                    className="w-full px-4 py-2 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-violet-500"
                    placeholder="Describe your charity's mission and goals"
                  />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">
                      Target Amount ($) *
                    </label>
                    <input
                      type="number"
                      name="targetAmount"
                      value={formData.targetAmount}
                      onChange={handleChange}
                      required
                      min="0"
                      className="w-full px-4 py-2 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-violet-500"
                      placeholder="Enter target amount"
                    />
                  </div>

                  <div>
                    <label htmlFor="category" className="block text-sm font-medium text-gray-700 mb-1">Category</label>
                    <select
                      id="category"
                      name="category"
                      value={formData.category}
                      onChange={handleChange}
                      required
                      className="w-full px-4 py-2 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-violet-500 h-12"
                    >
                      <option value="">Select a category</option>
                      {CHARITY_CATEGORIES.map((cat) => (
                        <option key={cat.id} value={cat.id}>{cat.label}</option>
                      ))}
                    </select>
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">
                      Organization Name
                    </label>
                    <input
                      type="text"
                      name="organizationName"
                      value={formData.organizationName}
                      onChange={handleChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-violet-500"
                      placeholder="Enter organization name"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">
                      Website
                    </label>
                    <input
                      type="url"
                      name="website"
                      value={formData.website}
                      onChange={handleChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-violet-500"
                      placeholder="Enter website URL"
                    />
                  </div>
                </div>
              </div>

              {/* Documents */}
              <div className="space-y-4">
                <h2 className="!text-lg !mb-4">Required Image</h2>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Upload Image *
                  </label>
                  <div className="mt-1 flex justify-center px-6 pt-5 pb-6 border-2 border-gray-300 border-dashed rounded-xl">
                    <div className="space-y-1 text-center">
                      {selectedFile ? (
                        <div className="space-y-2">
                          <p className="text-sm text-gray-600">{selectedFile.name}</p>
                          <button
                            type="button"
                            onClick={handleRemoveFile}
                            className="text-sm text-red-600 hover:text-red-700"
                          >
                            Remove
                          </button>
                        </div>
                      ) : (
                        <>
                          <svg
                            className="mx-auto h-12 w-12 text-gray-400"
                            stroke="currentColor"
                            fill="none"
                            viewBox="0 0 48 48"
                            aria-hidden="true"
                          >
                            <path
                              d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02"
                              strokeWidth={2}
                              strokeLinecap="round"
                              strokeLinejoin="round"
                            />
                          </svg>
                          <div className="flex text-sm text-gray-600">
                            <label
                              htmlFor="file-upload"
                              className="relative cursor-pointer bg-white rounded-md font-medium text-violet-600 hover:text-violet-500 focus-within:outline-none focus-within:ring-2 focus-within:ring-offset-2 focus-within:ring-violet-500"
                            >
                              <span>Upload image</span>
                              <input
                                id="file-upload"
                                name="document"
                                type="file"
                                onChange={handleFileChange}
                                accept="image/*"
                                className="sr-only"
                              />
                            </label>
                            <p className="pl-1">or drag and drop</p>
                          </div>
                          <p className="text-xs text-gray-500">
                            JPG, PNG, GIF, or WEBP up to 5MB
                          </p>
                        </>
                      )}
                    </div>
                  </div>
                  {fileError && (
                    <p className="mt-2 text-sm text-red-600">{fileError}</p>
                  )}
                </div>
              </div>

              {/* Submit Button */}
              <div className="pt-6">
                <button
                  type="submit"
                  className="w-full btn-primary"
                  disabled={status === 'loading'}
                >
                  {status === 'loading' ? 'Creating Charity...' : 'Submit Charity Request'}
                </button>
                {error && (
                  <div className="mt-4 p-4 bg-red-50 text-red-600 rounded-xl">
                    {error}
                  </div>
                )}
              </div>
            </form>
          </div>
        </div>
      </div>
    </main>
  )
} 