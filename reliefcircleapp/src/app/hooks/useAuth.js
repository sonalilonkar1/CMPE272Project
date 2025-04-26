'use client'
import { useSession, signOut } from 'next-auth/react'
import { useRouter } from 'next/navigation'

export function useAuth() {
  const { data: session, status } = useSession()
  const router = useRouter()
  
  const isAuthenticated = status === 'authenticated' && !!session
  const isLoading = status === 'loading'
  
  const logout = () => {
    signOut({ callbackUrl: '/login' })
  }
  
  const redirectToLogin = () => {
    router.push('/login')
  }
  
  const hasRole = (requiredRole) => {
    if (!session || !session.user) return false
    return session.user.role === requiredRole
  }
  
  return {
    session,
    status,
    isAuthenticated,
    isLoading,
    logout,
    redirectToLogin,
    hasRole,
    user: session?.user || null,
    accessToken: session?.accessToken || null,
  }
} 