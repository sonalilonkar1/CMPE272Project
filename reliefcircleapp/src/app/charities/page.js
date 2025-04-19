'use client'
import { useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import Link from 'next/link'
import Image from 'next/image'
import { fetchCharities } from '@/redux/features/charitiesSlice'

export default function Charities() {
  const dispatch = useDispatch();
  const { charities, status, error } = useSelector((state) => state.charities);

  useEffect(() => {
    dispatch(fetchCharities());
  }, [dispatch]);

  // Calculate percentage for progress bar
  const calculateProgress = (raised, target) => {
    return Math.min((raised / target) * 100, 100);
  };

  if (status === 'loading' || !charities) {
    return (
      <main className="min-h-screen bg-gray-50">
        <section className="gradient-bg">
          <div className="container py-12">
            <h1 className="text-white mb-3">Featured Charities</h1>
          </div>
        </section>
        <section className="section">
          <div className="container max-w-5xl">
            <div className="flex items-center justify-center">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-violet-600"></div>
            </div>
          </div>
        </section>
      </main>
    );
  }

  if (status === 'failed') {
    return (
      <main className="min-h-screen bg-gray-50">
        <section className="gradient-bg">
          <div className="container py-12">
            <h1 className="text-white mb-3">Featured Charities</h1>
          </div>
        </section>
        <section className="section">
          <div className="container max-w-5xl">
            <div className="text-center text-red-600">
              Error: {error?.message || 'Failed to load charities'}
            </div>
          </div>
        </section>
      </main>
    );
  }

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
            {Array.isArray(charities) && charities.map((charity) => (
              <div key={charity.id} className="card flex flex-col md:flex-row gap-6 !p-6">
                {/* Image */}
                <div className="relative w-full md:w-64 h-48 md:h-auto rounded-xl overflow-hidden shrink-0">
                  <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-black/0 z-10" />
                  {charity.fileUrl ? (
                    <Image
                      src={charity.fileUrl}
                      alt={charity.name}
                      fill
                      className="object-cover"
                    />
                  ) : (
                    <div className="absolute inset-0 bg-gray-200 flex items-center justify-center">
                      <span className="text-gray-400">No image available</span>
                    </div>
                  )}
                  {charity.category && (
                    <div className="absolute bottom-3 left-3 z-20">
                      <span className="px-3 py-1 bg-white/20 backdrop-blur-sm rounded-full text-white text-xs">
                        {charity.category}
                      </span>
                    </div>
                  )}
                </div>

                {/* Content */}
                <div className="flex-grow space-y-4">
                  <div>
                    <div className="flex items-center gap-2 mb-2">
                      <h3 className="!mb-0">{charity.name}</h3>
                      {charity.isVerified && (
                        <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                          Verified
                        </span>
                      )}
                    </div>
                    <p className="text-slate-600 text-sm">{charity.description}</p>
                    <p className="text-sm text-slate-500 mt-2">
                      By {charity.fundraiserName} - {charity.organizationName || 'Independent Fundraiser'}
                    </p>
                  </div>

                  {/* Progress Bar */}
                  <div className="space-y-3">
                    <div className="flex justify-between items-center">
                      <div>
                        <span className="text-2xl font-bold text-slate-800">
                          ${charity.raisedAmount?.toLocaleString()}
                        </span>
                        <span className="text-sm text-slate-500 ml-1">
                          raised of ${charity.targetAmount?.toLocaleString()} goal
                        </span>
                      </div>
                      <span className="text-sm font-medium text-slate-600">
                        {calculateProgress(charity.raisedAmount, charity.targetAmount)}%
                      </span>
                    </div>
                    <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                      <div 
                        className="h-full bg-gradient-to-r from-violet-600 to-teal-500 transition-all duration-500"
                        style={{ 
                          width: `${calculateProgress(charity.raisedAmount, charity.targetAmount)}%` 
                        }}
                      />
                    </div>
                  </div>

                  <div className="flex items-center justify-between pt-2">
                    <span className="text-xs text-slate-600">
                      Started funding since {new Date(charity.createdAt).toLocaleDateString('en-US', {
                        month: 'long',
                        day: 'numeric',
                        year: 'numeric'
                      })}
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
  );
} 