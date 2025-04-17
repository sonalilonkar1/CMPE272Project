import Link from 'next/link'
import Image from 'next/image'

// Temporary mock data - this would come from your API/database
const charities = [
  {
    id: 1,
    name: "Save The Children",
    category: "Children's Welfare",
    description: "Supporting children's rights and providing emergency aid in natural disasters.",
    amountRaised: 125000,
    goal: 200000,
    image: "/charity1.jpg",
    verifiedDonors: 234
  },
  {
    id: 2,
    name: "Ocean Conservation Alliance",
    category: "Environmental",
    description: "Working to protect marine ecosystems and reduce ocean pollution worldwide.",
    amountRaised: 89000,
    goal: 150000,
    image: "/charity2.jpg",
    verifiedDonors: 156
  },
  {
    id: 3,
    name: "Global Food Bank",
    category: "Hunger Relief",
    description: "Providing meals and sustainable food solutions to communities in need.",
    amountRaised: 75000,
    goal: 100000,
    image: "/charity3.jpg",
    verifiedDonors: 189
  },
  {
    id: 4,
    name: "Education For All",
    category: "Education",
    description: "Making quality education accessible to underprivileged children globally.",
    amountRaised: 95000,
    goal: 120000,
    image: "/charity4.jpg",
    verifiedDonors: 167
  }
];

export default function Charities() {
  return (
    <main className="min-h-screen bg-gray-50">
      {/* Header Section */}
      <section className="gradient-bg">
        <div className="container py-12">
          <h1 className="text-white mb-3">Featured Charities</h1>
          <p className="hero-text">
            Support verified organizations making real impact in communities around the world.
          </p>
        </div>
      </section>

      {/* Charities List */}
      <section className="section">
        <div className="container max-w-5xl">
          <div className="space-y-6">
            {charities.map((charity) => (
              <div key={charity.id} className="card flex flex-col md:flex-row gap-6 !p-6">
                {/* Image */}
                <div className="relative w-full md:w-64 h-48 md:h-auto rounded-xl overflow-hidden shrink-0">
                  <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-black/0 z-10" />
                  <Image
                    src={charity.image}
                    alt={charity.name}
                    fill
                    className="object-cover"
                  />
                  <div className="absolute bottom-3 left-3 z-20">
                    <span className="px-3 py-1 bg-white/20 backdrop-blur-sm rounded-full text-white text-xs">
                      {charity.category}
                    </span>
                  </div>
                </div>

                {/* Content */}
                <div className="flex-grow space-y-4">
                  <div>
                    <h3 className="!mb-2">{charity.name}</h3>
                    <p className="text-slate-600 text-sm">{charity.description}</p>
                  </div>

                  {/* Progress Bar */}
                  <div>
                    <div className="flex justify-between text-xs mb-2">
                      <span className="text-slate-600">
                        ${charity.amountRaised.toLocaleString()} raised
                      </span>
                      <span className="text-slate-600">
                        ${charity.goal.toLocaleString()} goal
                      </span>
                    </div>
                    <div className="h-1.5 bg-gray-100 rounded-full overflow-hidden">
                      <div 
                        className="h-full bg-gradient-to-r from-violet-600 to-teal-500 transition-all duration-500"
                        style={{ width: `${(charity.amountRaised / charity.goal) * 100}%` }}
                      />
                    </div>
                  </div>

                  <div className="flex items-center justify-between pt-2">
                    <span className="text-xs text-slate-600">
                      {charity.verifiedDonors} verified donors
                    </span>
                    <Link 
                      href={`/charities/${charity.id}`}
                      className="btn-primary bg-gradient-to-r from-violet-600 to-teal-500 !py-2 !px-6"
                    >
                      Donate Now
                    </Link>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>
    </main>
  )
} 