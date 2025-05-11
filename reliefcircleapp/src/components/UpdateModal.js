'use client'

import { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { sendFundraiserUpdate } from '@/redux/features/updatesSlice'

export default function UpdateModal({ isOpen, onClose, charityId, charityName }) {
  const dispatch = useDispatch()
  const { profile } = useSelector((state) => state.user)
  const [message, setMessage] = useState('')
  const [media, setMedia] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const [fileError, setFileError] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (fileError) {
      return
    }
    setIsLoading(true)

    try {
      await dispatch(sendFundraiserUpdate({
        text: message,
        file: media,
        token: profile.token,
        charityId
      })).unwrap()

      setMessage('')
      setMedia(null)
      setFileError('')
      onClose()
    } catch (error) {
      console.error('Error sending update:', error)
    } finally {
      setIsLoading(false)
    }
  }

  const handleMediaChange = (e) => {
    const file = e.target.files[0]
    setFileError('')
    
    if (file) {
      // Check file type
      const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'application/pdf']
      if (!allowedTypes.includes(file.type)) {
        setFileError('Please upload an image (JPG, PNG, GIF, WEBP) or PDF file')
        return
      }

      // Check file size (exactly 10MB)
      const maxSize = 10 * 1024 * 1024 // 10MB in bytes
      if (file.size > maxSize) {
        const fileSizeMB = (file.size / (1024 * 1024)).toFixed(2)
        setFileError(`File size (${fileSizeMB}MB) exceeds the 10MB limit`)
        return
      }

      setMedia(file)
    }
  }

  const handleRemoveFile = () => {
    setMedia(null)
    setFileError('')
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg w-full max-w-xl mx-4">
        <div className="p-6">
          {/* Header */}
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-xl font-semibold text-gray-900">Write update message</h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-500"
            >
              <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          {/* Charity Name */}
          <div className="mb-4">
            <p className="text-sm text-gray-600">Sending update for: <span className="font-medium text-gray-900">{charityName}</span></p>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit}>
            {/* Message Input */}
            <div className="mb-6">
              <label htmlFor="message" className="sr-only">Update message</label>
              <textarea
                id="message"
                rows={4}
                className="block w-full rounded-lg border border-gray-300 px-4 py-3 text-gray-900 placeholder-gray-500 focus:border-violet-500 focus:ring-violet-500"
                placeholder="Tell donors about any progress or news..."
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                required
              />
            </div>

            {/* Media Upload */}
            <div className="mb-6">
              <div className="flex items-center justify-between mb-2">
                <h3 className="text-sm font-medium text-gray-900">Upload file</h3>
                <span className="text-xs text-violet-600 bg-violet-50 px-2 py-1 rounded">Optional</span>
              </div>
              <p className="text-sm text-gray-500 mb-3">Share images or PDF documents to support your update.</p>
              
              <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center">
                {media ? (
                  <div className="space-y-2">
                    <p className="text-sm text-gray-600">{media.name}</p>
                    <button
                      type="button"
                      onClick={handleRemoveFile}
                      className="text-sm text-red-600 hover:text-red-700"
                    >
                      Remove
                    </button>
                  </div>
                ) : (
                  <div>
                    <label
                      htmlFor="media-upload"
                      className="cursor-pointer flex flex-col items-center space-y-2"
                    >
                      <svg className="h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                      </svg>
                      <span className="text-sm text-gray-500">Click to upload</span>
                    </label>
                    <input
                      id="media-upload"
                      type="file"
                      className="hidden"
                      accept="image/*,.pdf"
                      onChange={handleMediaChange}
                    />
                    <p className="text-xs text-gray-500 mt-2">
                      JPG, PNG, GIF, WEBP, or PDF (max 10MB)
                    </p>
                  </div>
                )}
              </div>
              {fileError && (
                <p className="mt-2 text-sm text-red-600">{fileError}</p>
              )}
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isLoading || !message.trim() || fileError}
              className={`w-full rounded-lg px-4 py-3 text-sm font-medium text-white 
                ${isLoading || !message.trim() || fileError
                  ? 'bg-gray-300 cursor-not-allowed'
                  : 'bg-violet-600 hover:bg-violet-700'
                }`}
            >
              {isLoading ? 'Sending...' : 'Send update'}
            </button>
          </form>
        </div>
      </div>
    </div>
  )
} 