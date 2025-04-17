'use client'
import { useSession, signIn, signOut } from 'next-auth/react'
import { useRouter } from 'next/navigation'

export function useAuth() {
  const { data: session, status } = useSession()
  const router = useRouter()

  const handleSignIn = async () => {
    await signIn('okta', { callbackUrl: '/' })
  }

  const handleSignOut = async () => {
    await signOut({ callbackUrl: '/' })
  }

  return {
    session,
    status,
    isAuthenticated: status === 'authenticated',
    isLoading: status === 'loading',
    signIn: handleSignIn,
    signOut: handleSignOut,
    user: session?.user
  }
} 