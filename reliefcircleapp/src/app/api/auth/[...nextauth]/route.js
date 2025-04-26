import NextAuth from "next-auth";
import GoogleProvider from "next-auth/providers/google";
import CredentialsProvider from "next-auth/providers/credentials";
import axios from "axios";

// Your backend API URL
const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api";

export const authOptions = {
  providers: [
    GoogleProvider({
      clientId: process.env.GOOGLE_CLIENT_ID,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET,
    }),
    CredentialsProvider({
      name: "Credentials",
      credentials: {
        email: { label: "Email", type: "email" },
        password: { label: "Password", type: "password" }
      },
      async authorize(credentials) {
        if (!credentials?.email || !credentials?.password) {
          return null;
        }
        
        try {
          // Send credentials to your Java backend
          const response = await axios.post(`${API_URL}/auth/login`, {
            email: credentials.email,
            password: credentials.password,
          });
          
          if (response.data && response.data.token) {
            // Return user object with token
            return {
              id: response.data.id,
              email: credentials.email,
              name: response.data.name || credentials.email,
              token: response.data.token,
              role: response.data.role || "user",
            };
          }
          return null;
        } catch (error) {
          console.error("Auth error:", error);
          return null;
        }
      },
    }),
  ],
  callbacks: {
    async jwt({ token, user, account }) {
      // Initial sign in
      if (account && user) {
        if (account.provider === "google") {
          try {
            // Exchange Google token for your backend JWT
            const response = await axios.post(`${API_URL}/auth/google`, {
              token: account.id_token,
            });
            
            if (response.data && response.data.token) {
              token.accessToken = response.data.token;
              token.id = response.data.id;
              token.role = response.data.role || "user";
            }
          } catch (error) {
            console.error("Google auth error:", error);
          }
        } else if (account.provider === "credentials") {
          // For credentials login, use the token from the backend
          token.accessToken = user.token;
          token.id = user.id;
          token.role = user.role;
        }
      }
      return token;
    },
    async session({ session, token }) {
      // Send properties to the client
      session.accessToken = token.accessToken;
      session.user.id = token.id;
      session.user.role = token.role;
      
      return session;
    },
  },
  pages: {
    signIn: '/login',
    error: '/login',
  },
  session: {
    strategy: "jwt",
    maxAge: 30 * 24 * 60 * 60, // 30 days
  },
  secret: process.env.NEXTAUTH_SECRET,
};

const handler = NextAuth(authOptions);
export { handler as GET, handler as POST }; 