// Charity Categories
export const CHARITY_CATEGORIES = [
  { id: 'education', label: 'Education', icon: '📚' },
  { id: 'healthcare', label: 'Healthcare', icon: '🏥' },
  { id: 'environment', label: 'Environment', icon: '🌱' },
  { id: 'animals', label: 'Animals', icon: '🐾' },
  { id: 'disaster-relief', label: 'Disaster Relief', icon: '🚑' },
  { id: 'poverty', label: 'Poverty Alleviation', icon: '🤝' },
  { id: 'children', label: 'Children & Youth', icon: '👶' },
  { id: 'elderly', label: 'Elderly Care', icon: '👴' },
  { id: 'human-rights', label: 'Human Rights', icon: '✊' },
  { id: 'arts-culture', label: 'Arts & Culture', icon: '🎨' },
  { id: 'sports', label: 'Sports & Recreation', icon: '⚽' },
  { id: 'technology', label: 'Technology & Innovation', icon: '💻' },
  { id: 'research', label: 'Research & Science', icon: '🔬' },
  { id: 'community', label: 'Community Development', icon: '🏘️' },
  { id: 'other', label: 'Other', icon: '📦' }
]

// User Roles
export const USER_ROLES = {
  DONOR: 'DONOR',
  FUNDRAISER: 'FUNDRAISER',
  ADMIN: 'ADMIN'
}

// Navigation Links
export const NAV_LINKS = [
  { href: '/charities', label: 'Charities' }
]


// Currency
export const CURRENCY = {
  USD: 'USD',
  EUR: 'EUR',
  GBP: 'GBP'
}

// Default Currency
export const DEFAULT_CURRENCY = CURRENCY.USD

// Pagination
export const DEFAULT_PAGE_SIZE = 10
export const MAX_PAGE_SIZE = 50

// File Upload
export const MAX_FILE_SIZE = 5 * 1024 * 1024 // 5MB
export const ALLOWED_FILE_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'application/pdf']

// Date Formats
export const DATE_FORMAT = 'YYYY-MM-DD'
export const DATETIME_FORMAT = 'YYYY-MM-DD HH:mm:ss'

// Error Messages
export const ERROR_MESSAGES = {
  GENERIC: 'Something went wrong. Please try again.',
  NETWORK: 'Network error. Please check your connection.',
  UNAUTHORIZED: 'Please sign in to continue.',
  FORBIDDEN: 'You do not have permission to perform this action.',
  NOT_FOUND: 'The requested resource was not found.',
  VALIDATION: 'Please check your input and try again.'
}

// Success Messages
export const SUCCESS_MESSAGES = {
  DONATION_CREATED: 'Thank you for your donation!',
  CHARITY_CREATED: 'Charity created successfully!',
  PROFILE_UPDATED: 'Profile updated successfully!',
  SETTINGS_UPDATED: 'Settings updated successfully!'
} 