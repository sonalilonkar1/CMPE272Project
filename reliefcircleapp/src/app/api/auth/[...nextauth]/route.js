import NextAuth from 'next-auth'
import OktaProvider from 'next-auth/providers/okta'

const handler = NextAuth({
  providers: [
    OktaProvider({
      clientId: process.env.OKTA_CLIENT_ID,
      issuer: process.env.OKTA_ISSUER,
      authorization: {
        params: {
          scope: 'openid email profile',
          response_type: 'code',
          prompt: 'login'
        }
      },
      userinfo: {
        params: {
          scope: 'openid email profile'
        }
      }
    })
  ],
  callbacks: {
    async jwt({ token, account, profile }) {
      // Persist the Okta access token to the token right after signin
      if (account) {
        token.accessToken = account.access_token
        token.userRole = profile?.role || 'user'
      }
      return token
    },
    async session({ session, token }) {
      // Send properties to the client
      session.accessToken = token.accessToken
      session.userRole = token.userRole
      return session
    }
  },
  pages: {
    signIn: '/auth/signin',
  }
})

export { handler as GET, handler as POST } 