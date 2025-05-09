'use client'

import { useState } from 'react'
import Image from 'next/image'

export default function UpdateModal({ isOpen, onClose, charityId, charityName }) {
  const [message, setMessage] = useState('')
  const [media, setMedia] = useState(null)
  const [isLoading, setIsLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setIsLoading(true)

    try {
      const formData = new FormData()
      formData.append('message', message)
      if (media) {
        formData.append('media', media)
      }
      formData.append('charityId', charityId)

      const response = await fetch('/api/charity-updates', {
        method: 'POST',
        body: formData,
      })

      if (!response.ok) {
        throw new Error('Failed to send update')
      }

      setMessage('')
      setMedia(null)
      onClose()
    } catch (error) {
      console.error('Error sending update:', error)
      // You might want to show an error toast here
    } finally {
      setIsLoading(false)
    }
  }

  const handleMediaChange = (e) => {
    const file = e.target.files[0]
    if (file) {
      setMedia(file)
    }
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
                <h3 className="text-sm font-medium text-gray-900">Upload photos or videos</h3>
                <span className="text-xs text-violet-600 bg-violet-50 px-2 py-1 rounded">Recommended</span>
              </div>
              <p className="text-sm text-gray-500 mb-3">Updates with media help increase donations.</p>
              
              <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center">
                {media ? (
                  <div className="space-y-2">
                    <p className="text-sm text-gray-600">{media.name}</p>
                    <button
                      type="button"
                      onClick={() => setMedia(null)}
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
                      accept="image/*,video/*"
                      onChange={handleMediaChange}
                    />
                  </div>
                )}
              </div>
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isLoading || !message.trim()}
              className={`w-full rounded-lg px-4 py-3 text-sm font-medium text-white 
                ${isLoading || !message.trim()
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