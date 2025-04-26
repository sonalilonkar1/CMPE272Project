import { getToken } from "next-auth/jwt";
import { NextResponse } from "next/server";

// Paths that are accessible without authentication
const publicPaths = [
  '/',
  '/login',
  '/register',
  '/forgot-password',
  '/reset-password',
];

// The middleware function
export async function middleware(request) {
  const path = request.nextUrl.pathname;
  
  // Check if the path is public
  const isPublicPath = publicPaths.some(publicPath => 
    path === publicPath || path.startsWith('/api/') || path.startsWith('/_next/') || path.startsWith('/static/')
  );

  // If it's a public path, allow access
  if (isPublicPath) {
    return NextResponse.next();
  }

  // Get the token from the request
  const token = await getToken({ 
    req: request, 
    secret: process.env.NEXTAUTH_SECRET 
  });
  
  // If no token, redirect to login
  if (!token) {
    const url = new URL('/login', request.url);
    url.searchParams.set("callbackUrl", encodeURI(request.url));
    return NextResponse.redirect(url);
  }

  // Role-based access control
  if (path.startsWith('/admin') && token.role !== 'admin') {
    // Redirect non-admin users
    return NextResponse.redirect(new URL('/dashboard', request.url));
  }

  // Allow access with token
  return NextResponse.next();
}

// Configure which paths the middleware applies to
export const config = {
  matcher: [
    /*
     * Match all paths except for:
     * 1. /api routes that don't require auth (handled in the middleware function)
     * 2. /_next (Next.js internals)
     * 3. /static (static files)
     * 4. All files in the public directory
     */
    '/((?!api/auth|_next/static|_next/image|favicon.ico|robots.txt).*)',
  ],
}; 