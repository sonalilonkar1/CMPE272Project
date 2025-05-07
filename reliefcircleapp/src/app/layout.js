import './globals.css'
import { Inter } from 'next/font/google'
import { Providers } from './providers'
import { setupAxiosInterceptors } from '@/lib/auth'

const inter = Inter({ subsets: ['latin'] })

// Set up axios interceptors for auth token handling
if (typeof window !== 'undefined') {
  setupAxiosInterceptors()
}

export const metadata = {
  title: 'ReliefCircle',
  description: 'Your trusted platform for charity and fundraising',
}

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <Providers>
          {children}
        </Providers>
      </body>
    </html>
  )
}
