'use client'

import { useState, useRef, useEffect } from 'react'
import Link from 'next/link'
import { useAuth } from '@/app/hooks/useAuth'
import { useSelector } from 'react-redux'
import { USER_ROLES } from '@/utils/constants'
import { usePathname } from 'next/navigation'

export default function Header() {
  const { isAuthenticated, logout } = useAuth()
  const { profile } = useSelector((state) => state.user)
  const [isDropdownOpen, setIsDropdownOpen] = useState(false)
  const dropdownRef = useRef(null)
  const pathname = usePathname()

  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsDropdownOpen(false)
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  const userNavLinks = {
    [USER_ROLES.DONOR]: [
      { type: 'link', href: '/donor?tab=overview', label: 'Profile', activeTab: 'overview' },
      { type: 'divider' },
      { type: 'link', href: '/donor?tab=impact', label: 'Your Impact', activeTab: 'impact' },
      { type: 'divider' },
      { type: 'button', label: 'Sign out', onClick: logout },
    ],
    [USER_ROLES.FUNDRAISER]: [
      { type: 'link', href: '/fundraiser?tab=overview', label: 'Profile', activeTab: 'overview' },
      { type: 'divider' },
      { type: 'link', href: '/fundraiser?tab=charities', label: 'Your fundraisers', activeTab: 'charities' },
      { type: 'link', href: '/charities/new', label: 'Create Charity' },
      { type: 'divider' },
      { type: 'button', label: 'Sign out', onClick: logout },
    ]
  }

  return (
    <header className="bg-white shadow-sm fixed w-full top-0 z-50">
      <nav className="container mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          {/* Logo */}
          <div className="flex items-center">
            <Link href="/" className="text-xl font-bold text-violet-600">
              ReliefCircle
            </Link>
          </div>

          {/* Auth Buttons */}
          <div className="flex items-center space-x-4">
            {isAuthenticated && profile ? (
              <div className="relative" ref={dropdownRef}>
                <button 
                  className="flex items-center space-x-3"
                  onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                >
                  {/* Profile Picture */}
                  <div className="w-8 h-8 rounded-full bg-violet-600 text-white flex items-center justify-center text-sm font-medium">
                    {profile.fullName?.[0]?.toUpperCase() || 'U'}
                  </div>
                  {/* Name and Chevron */}
                  <div className="flex items-center">
                    <span className="text-sm font-medium text-slate-700">{profile.fullName || 'User'}</span>
                    <svg 
                      className={`w-4 h-4 ml-2 text-slate-400 transform transition-transform duration-200 ${isDropdownOpen ? 'rotate-180' : ''}`} 
                      fill="none" 
                      stroke="currentColor" 
                      viewBox="0 0 24 24"
                    >
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                    </svg>
                  </div>
                </button>

                {/* Dropdown Menu */}
                {isDropdownOpen && (
                  <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 ring-1 ring-black ring-opacity-5">
                    {profile.role && userNavLinks[profile.role]?.map((item, index) => (
                      item.type === 'divider' ? (
                        <hr key={index} className="my-1 border-gray-200" />
                      ) : item.type === 'button' ? (
                        <button
                          key={index}
                          onClick={item.onClick}
                          className="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-100"
                        >
                          {item.label}
                        </button>
                      ) : (
                        <Link
                          key={index}
                          href={item.href}
                          className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                        >
                          {item.label}
                        </Link>
                      )
                    ))}
                  </div>
                )}
              </div>
            ) : (
              <>
                <Link
                  href="/login"
                  className="text-gray-700 hover:text-violet-600 font-medium"
                >
                  Sign in
                </Link>
                <Link
                  href="/register"
                  className="bg-violet-600 text-white px-4 py-2 rounded-lg hover:bg-violet-700"
                >
                  Sign up
                </Link>
              </>
            )}
          </div>
        </div>
      </nav>
    </header>
  )
} 