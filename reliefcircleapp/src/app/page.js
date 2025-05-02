import Link from 'next/link'

export default function Home() {
  return (
    <main className="min-h-screen">
      {/* Hero Section */}
      <section className="hero-section">
        <div className="hero-content">
          <div className="container">
            <div className="max-w-3xl">
              <h1 className="text-white mb-6">Welcome to ReliefCircle</h1>
              <p className="text-xl text-white/90 mb-12">
                A transparent and decentralized platform connecting charities, donors, and volunteers to make a real impact in communities.
              </p>
              <div className="flex flex-wrap gap-4">
                <Link href="/charities" className="btn-secondary">
                  Browse Charities
                </Link>
                <Link href="/donate" className="btn-primary">
                  Donate Now
                </Link>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="section bg-gray-50">
        <div className="container">
          <h2 className="text-center">How ReliefCircle Works</h2>
          <div className="grid md:grid-cols-3 gap-8 mt-12">
            <div className="card">
              <h3>For Charities</h3>
              <p className="text-slate-600">
                Submit fund requests, track donations in real-time, and provide transparent proof of fund utilization.
              </p>
            </div>
            <div className="card">
              <h3>For Donors</h3>
              <p className="text-slate-600">
                Contribute to causes you care about and track how your donations are making an impact.
              </p>
            </div>
            <div className="card">
              <h3>For Volunteers</h3>
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
          <h2 className="text-gradient">Ready to Make a Difference?</h2>
          <p className="text-xl text-slate-600 mb-12 max-w-2xl mx-auto">
            Join our community of changemakers and start making an impact today.
          </p>
          <div className="flex justify-center gap-4">
            <Link href="/register" className="btn-primary bg-gradient-to-r from-violet-600 to-teal-600">
              Get Started
            </Link>
            <Link href="/about" className="btn-outline border-violet-600 text-violet-600">
              Learn More
            </Link>
          </div>
        </div>
      </section>
    </main>
  )
}
