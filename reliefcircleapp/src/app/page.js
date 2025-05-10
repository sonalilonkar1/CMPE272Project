'use client'
import Link from 'next/link'
import { useSelector } from 'react-redux'
import { useRouter } from 'next/navigation'

const categories = [
  { label: 'Your cause', img: '/images/your-cause.jpg' },
  { label: 'Medical', img: '/images/medical.jpg' },
  { label: 'Education', img: '/images/education.jpg' },
  { label: 'Animal', img: '/images/animal.jpg' },
  { label: 'Emergency', img: '/images/emergency.jpg' },
  { label: 'Business', img: '/images/business.jpg' },
];

export default function Home() {
  const { profile } = useSelector((state) => state.user)
  const router = useRouter()

  const handleFundraiserClick = (e) => {
    e.preventDefault()
    if (profile?.role === 'FUNDRAISER') {
      router.push('/charities/new')
    } else {
      router.push('/register')
    }
  }

  const handleDonationClick = (e) => {
    e.preventDefault()
    if (profile?.role !== 'FUNDRAISER') {
      router.push('/charities')
    } else {
      router.push('/register')
    }
  }

  return (
    <main className="min-h-screen bg-gradient-to-br from-green-50 to-white relative overflow-hidden">
      {/* Hero Section */}
      <section className="relative flex flex-col items-center justify-center min-h-[600px] py-24 bg-gradient-to-br from-violet-50 to-green-100 overflow-hidden">
        {/* Subtle Gradient Overlay */}
        <div className="absolute inset-0 bg-gradient-to-br from-violet-200/60 via-white/60 to-green-200/60 pointer-events-none" style={{ zIndex: 1 }}></div>
        {/* Content */}
        <div className="relative z-10 flex flex-col items-center text-center px-4">
          <h2 className="text-xl md:text-2xl font-semibold text-violet-700 mb-3 tracking-tight">
            Join a Community of Givers & Changemakers
          </h2>
          <h1 className="text-4xl md:text-6xl font-extrabold text-gray-900 mb-6 leading-tight drop-shadow-sm">
            Empower. Support. <span className="text-violet-600">Make an Impact</span>
          </h1>
          <p className="text-base md:text-xl text-gray-700 mb-8 max-w-2xl">
            Whether you want to raise funds for a cause, support others with your donation, or volunteer your time—ReliefCircle is your platform to create real change.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 mb-8">
            <button
              onClick={handleFundraiserClick}
              className="px-8 py-3 bg-gradient-to-r from-violet-600 to-green-500 text-white rounded-full text-lg font-bold shadow-lg hover:from-violet-700 hover:to-green-600 transition"
            >
              Start a Fundraiser
            </button>
            <button
              onClick={handleDonationClick}
              className="px-8 py-3 bg-white border-2 border-violet-600 text-violet-700 rounded-full text-lg font-bold shadow hover:bg-violet-50 transition"
            >
              Make a Donation
            </button>
          </div>
          {/* Collage of Placeholder Images */}
          <div className="flex flex-wrap justify-center gap-6 mt-4">
            <img src="/images/placeholder1.jpg" alt="Fundraiser" className="w-32 h-32 object-cover rounded-2xl shadow-md border-4 border-white" />
            <img src="/images/placeholder2.jpg" alt="Donor" className="w-32 h-32 object-cover rounded-2xl shadow-md border-4 border-white" />
            <img src="/images/placeholder3.jpg" alt="Volunteer" className="w-32 h-32 object-cover rounded-2xl shadow-md border-4 border-white" />
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="section bg-gray-50">
        <div className="container">
          <h2 className="text-center text-2xl font-bold text-violet-700 mb-8">How ReliefCircle Works</h2>
          <div className="grid md:grid-cols-3 gap-8 mt-12">
            <div className="card bg-white rounded-xl shadow p-6">
              <h3 className="text-lg font-semibold text-violet-800 mb-2">For Charities</h3>
              <p className="text-slate-600">
                Submit fund requests, track donations in real-time, and provide transparent proof of fund utilization.
              </p>
            </div>
            <div className="card bg-white rounded-xl shadow p-6">
              <h3 className="text-lg font-semibold text-violet-800 mb-2">For Donors</h3>
              <p className="text-slate-600">
                Contribute to causes you care about and track how your donations are making an impact.
              </p>
            </div>
            <div className="card bg-white rounded-xl shadow p-6">
              <h3 className="text-lg font-semibold text-violet-800 mb-2">For Volunteers</h3>
              <p className="text-slate-600">
                Help verify fund utilization and ensure transparency in the donation process.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Call to Action */}
      <section className="section bg-white">
        <div className="container text-center">
          <h2 className="text-2xl md:text-3xl font-bold text-violet-700 mb-4">Ready to Make a Difference?</h2>
          <p className="text-xl text-slate-600 mb-12 max-w-2xl mx-auto">
            Join our community of changemakers and start making an impact today.
          </p>
          <div className="flex justify-center gap-4">
            <Link href="/register" className="btn-primary bg-gradient-to-r from-violet-600 to-teal-600 text-white font-semibold">
              Get Started
            </Link>
            <Link href="/about" className="btn-outline border-violet-600 text-violet-600 font-semibold">
              Learn More
            </Link>
          </div>
        </div>
      </section>
    </main>
  )
}
