'use client'
import Link from 'next/link'

export default function Footer() {
  return (
    <footer className="bg-white border-t border-gray-200 mt-12">
      <div className="container mx-auto px-4 py-10 flex flex-col md:flex-row justify-between items-center gap-6">
        {/* Logo and tagline */}
        <div className="flex flex-col items-center md:items-start">
          <Link href="/" className="text-2xl font-bold text-violet-600 mb-2">
            ReliefCircle
          </Link>
          <span className="text-sm text-gray-500">Empowering Change, Together</span>
        </div>
        {/* Navigation */}
        <nav className="flex flex-col md:flex-row gap-4 md:gap-8 items-center">
          <Link href="/about" className="text-gray-600 hover:text-violet-600 transition">About</Link>
          <Link href="/charities" className="text-gray-600 hover:text-violet-600 transition">Charities</Link>
          <Link href="/donor?tab=impact" className="text-gray-600 hover:text-violet-600 transition">Impact</Link>
          <Link href="/contact" className="text-gray-600 hover:text-violet-600 transition">Contact</Link>
        </nav>
        {/* Socials */}
        <div className="flex gap-4">
          <a href="https://twitter.com/" target="_blank" rel="noopener noreferrer" className="text-gray-400 hover:text-violet-600 transition">
            <span className="sr-only">Twitter</span>
            <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 24 24"><path d="M22.46 6c-.77.35-1.6.58-2.47.69a4.3 4.3 0 0 0 1.88-2.37 8.59 8.59 0 0 1-2.72 1.04A4.28 4.28 0 0 0 16.11 4c-2.37 0-4.29 1.92-4.29 4.29 0 .34.04.67.11.99C7.69 9.13 4.07 7.38 1.64 4.7c-.37.64-.58 1.38-.58 2.17 0 1.5.76 2.82 1.92 3.6-.71-.02-1.38-.22-1.97-.54v.05c0 2.1 1.5 3.85 3.5 4.25-.36.1-.74.16-1.13.16-.28 0-.54-.03-.8-.08.54 1.7 2.1 2.94 3.95 2.97A8.6 8.6 0 0 1 2 19.54c-.29 0-.57-.02-.85-.05A12.13 12.13 0 0 0 8.29 21.5c7.55 0 11.68-6.26 11.68-11.68 0-.18-.01-.36-.02-.54A8.18 8.18 0 0 0 24 4.59a8.36 8.36 0 0 1-2.54.7z"/></svg>
          </a>
          <a href="https://facebook.com/" target="_blank" rel="noopener noreferrer" className="text-gray-400 hover:text-violet-600 transition">
            <span className="sr-only">Facebook</span>
            <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 24 24"><path d="M22.68 0H1.32A1.32 1.32 0 0 0 0 1.32v21.36A1.32 1.32 0 0 0 1.32 24h11.5v-9.29H9.69v-3.62h3.13V8.41c0-3.1 1.89-4.79 4.65-4.79 1.32 0 2.45.1 2.78.14v3.22h-1.91c-1.5 0-1.79.71-1.79 1.75v2.3h3.58l-.47 3.62h-3.11V24h6.09A1.32 1.32 0 0 0 24 22.68V1.32A1.32 1.32 0 0 0 22.68 0"/></svg>
          </a>
          <a href="mailto:info@reliefcircle.com" className="text-gray-400 hover:text-violet-600 transition">
            <span className="sr-only">Email</span>
            <svg className="w-6 h-6" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path d="M16 12l-4-4-4 4m8 0l-4 4-4-4"/></svg>
          </a>
        </div>
      </div>
      <div className="text-center text-xs text-gray-400 py-4 border-t border-gray-100">
        &copy; {new Date().getFullYear()} ReliefCircle. All rights reserved.
      </div>
    </footer>
  )
}