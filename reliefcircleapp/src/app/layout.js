import './globals.css'
import { Inter } from 'next/font/google'
import { Providers } from './providers'
import Header from '@/components/Header'
import UserProfileProvider from '@/components/UserProfileProvider'
import { setupAxiosInterceptors } from '@/lib/auth'

const inter = Inter({ subsets: ['latin'] })

// Set up axios interceptors for auth token handling
if (typeof window !== 'undefined') {
  setupAxiosInterceptors()
}

export const metadata = {
  title: 'ReliefCircle - Connecting Charities, Donors, and Volunteers',
  description: 'A transparent and decentralized platform connecting charities, donors, and volunteers to make a real impact in communities.',
}

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <Providers>
          <UserProfileProvider>
            <div className="min-h-screen flex flex-col">
              <Header />
              <main className="flex-grow">
                {children}
              </main>
            </div>
          </UserProfileProvider>
        </Providers>
      </body>
    </html>
  )
}
