'use client'

import { useState, useRef, useEffect } from 'react'
import Link from 'next/link'
import { useAuth } from '@/app/hooks/useAuth'
import { usePathname, useSearchParams } from 'next/navigation'
import { NAV_LINKS, USER_ROLES, CHARITY_CATEGORIES, DONATION_STATUS } from '@/utils/constants'

export default function Header() {
  const { isAuthenticated, user, logout } = useAuth()
  const pathname = usePathname()
  const searchParams = useSearchParams()
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false)
  const [isDropdownOpen, setIsDropdownOpen] = useState(false)
  const dropdownRef = useRef(null)

  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsDropdownOpen(false)
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  const isActive = (path, tab = null) => {
    const pathMatch = pathname === path
    if (!tab) return pathMatch
    const currentTab = searchParams.get('tab')
    return pathMatch && currentTab === tab
  }

  const navLinks = [
    { href: '/charities', label: 'Charities' },
  ]

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
    <header className="bg-white shadow-sm sticky top-0 z-50">
      <nav className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link href="/" className="flex items-center space-x-2">
            <span className="text-xl font-bold text-violet-600">ReliefCircle</span>
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center space-x-8">
            {/* Main Navigation */}
            {/* <div className="flex items-center space-x-6">
              {navLinks.map((link) => (
                <Link
                  key={link.href}
                  href={link.href}
                  className={`text-sm font-medium transition-colors ${
                    isActive(link.href)
                      ? 'text-violet-600'
                      : 'text-slate-600 hover:text-violet-600'
                  }`}
                >
                  {link.label}
                </Link>
              ))}
            </div> */}

            {/* Auth Buttons / User Menu */}
            <div className="flex items-center space-x-4">
              {isAuthenticated ? (
                <div className="relative" ref={dropdownRef}>
                  <button 
                    className="flex items-center space-x-3"
                    onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                  >
                    {/* Profile Picture */}
                    <div className="w-8 h-8 rounded-full bg-violet-600 text-white flex items-center justify-center text-sm font-medium">
                      {user?.name?.[0]?.toUpperCase() || 'U'}
                    </div>
                    {/* Name and Chevron */}
                    <div className="flex items-center">
                      {/* <span className="text-sm font-medium text-slate-700">{user?.name || 'User'}</span> */}
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
                  <div 
                    className={`absolute right-0 mt-2 w-64 bg-white rounded-lg shadow-lg py-2 transform transition-all duration-200 ease-in-out ${
                      isDropdownOpen 
                        ? 'opacity-100 visible translate-y-0' 
                        : 'opacity-0 invisible -translate-y-2'
                    }`}
                  >
                    {userNavLinks[USER_ROLES.FUNDRAISER]?.map((item, index) => {
                      if (item.type === 'divider') {
                        return <div key={index} className="border-t border-gray-100 my-2" />;
                      }
                      if (item.type === 'button') {
                        return (
                          <button
                            key={index}
                            onClick={() => {
                              item.onClick();
                              setIsDropdownOpen(false);
                            }}
                            className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                          >
                            {item.label}
                          </button>
                        );
                      }
                      return (
                        <Link
                          key={index}
                          href={item.href}
                          className={`block px-4 py-2 text-sm ${
                            isActive(item.href.split('?')[0], item.activeTab)
                              ? 'text-violet-600 bg-violet-50'
                              : 'text-slate-700 hover:bg-violet-50 hover:text-violet-600'
                          }`}
                          onClick={() => setIsDropdownOpen(false)}
                        >
                          {item.label}
                        </Link>
                      );
                    })}
                  </div>
                </div>
              ) : (
                <>
                  <Link
                    href="/login"
                    className="text-sm font-medium text-slate-600 hover:text-violet-600"
                  >
                    Sign In
                  </Link>
                  <Link
                    href="/register"
                    className="btn-primary"
                  >
                    Get Started
                  </Link>
                </>
              )}
            </div>
          </div>

          {/* Mobile Menu Button */}
          <button
            className="md:hidden p-2 rounded-lg text-slate-600 hover:bg-gray-100"
            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d={isMobileMenuOpen ? "M6 18L18 6M6 6l12 12" : "M4 6h16M4 12h16M4 18h16"}
              />
            </svg>
          </button>
        </div>

        {/* Mobile Menu */}
        {isMobileMenuOpen && (
          <div className="md:hidden py-4">
            {navLinks.map((link) => (
              <Link
                key={link.href}
                href={link.href}
                className={`block px-4 py-2 text-sm font-medium ${
                  isActive(link.href)
                    ? 'text-violet-600 bg-violet-50'
                    : 'text-slate-600 hover:bg-gray-50'
                }`}
                onClick={() => setIsMobileMenuOpen(false)}
              >
                {link.label}
              </Link>
            ))}
            
            {isAuthenticated ? (
              <>
                {userNavLinks[USER_ROLES.FUNDRAISER]?.map((item, index) => {
                  if (item.type === 'divider') {
                    return <div key={index} className="border-t border-gray-100 my-2" />;
                  }
                  if (item.type === 'button') {
                    return (
                      <button
                        key={index}
                        onClick={() => {
                          item.onClick();
                          setIsMobileMenuOpen(false);
                        }}
                        className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                      >
                        {item.label}
                      </button>
                    );
                  }
                  return (
                    <Link
                      key={index}
                      href={item.href}
                      className={`block px-4 py-2 text-sm ${
                        isActive(item.href.split('?')[0], item.activeTab)
                          ? 'text-violet-600 bg-violet-50'
                          : 'text-slate-600 hover:bg-gray-50'
                      }`}
                      onClick={() => setIsMobileMenuOpen(false)}
                    >
                      {item.label}
                    </Link>
                  );
                })}
              </>
            ) : (
              <div className="px-4 space-y-2">
                <Link
                  href="/login"
                  className="block w-full text-center px-4 py-2 text-sm font-medium text-slate-600 hover:bg-gray-50"
                  onClick={() => setIsMobileMenuOpen(false)}
                >
                  Sign In
                </Link>
                <Link
                  href="/register"
                  className="block w-full text-center btn-primary"
                  onClick={() => setIsMobileMenuOpen(false)}
                >
                  Get Started
                </Link>
              </div>
            )}
          </div>
        )}
      </nav>
    </header>
  )
} 