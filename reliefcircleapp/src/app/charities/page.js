'use client'
import { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import Link from 'next/link'
import Image from 'next/image'
import { fetchCharities } from '@/redux/features/charitiesSlice'
import { Toast } from '@/components/Toast'

// Loading skeleton component
const CharityCardSkeleton = () => (
  <div className="bg-white rounded-lg shadow-md flex flex-col min-h-[280px] animate-pulse">
    <div className="p-6 pb-3 flex-grow">
      <div className="h-6 bg-gray-200 rounded w-3/4 mb-4"></div>
      <div className="space-y-2 mb-4">
        <div className="h-4 bg-gray-200 rounded w-full"></div>
        <div className="h-4 bg-gray-200 rounded w-5/6"></div>
        <div className="h-4 bg-gray-200 rounded w-4/6"></div>
      </div>
      <div className="space-y-1">
        <div className="flex justify-between items-center mb-2">
          <div className="h-4 bg-gray-200 rounded w-24"></div>
          <div className="h-4 bg-gray-200 rounded w-24"></div>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-2.5"></div>
        <div className="flex justify-end">
          <div className="h-4 bg-gray-200 rounded w-12"></div>
        </div>
      </div>
    </div>
    <div className="px-6 py-4 border-t border-gray-100">
      <div className="flex justify-between items-center">
        <div className="h-4 bg-gray-200 rounded w-32"></div>
        <div className="h-8 bg-gray-200 rounded w-24"></div>
      </div>
    </div>
  </div>
);

export default function Charities() {
  const dispatch = useDispatch();
  const { charities, currentPage, totalPages, totalElements, pageSize, loading } = useSelector((state) => state.charities);
  const [page, setPage] = useState(0);

  useEffect(() => {
    dispatch(fetchCharities({ page, size: pageSize }));
  }, [dispatch, page, pageSize]);

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  // Calculate percentage for progress bar
  const calculateProgress = (raised, target) => {
    if (!raised || !target) return 0;
    const percentage = (raised / target) * 100;
    return Math.min(Math.round(percentage), 100);
  };

  // Truncate text helper
  const truncateText = (text, maxLength) => {
    if (!text) return '';
    if (text.length <= maxLength) return text;
    return text.substr(0, maxLength) + '...';
  };

  if (loading) {
    return (
      <main className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
        <div className="container mx-auto">
          <h1 className="text-3xl font-bold text-slate-800 mb-8">Charities</h1>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {[...Array(6)].map((_, index) => (
              <CharityCardSkeleton key={index} />
            ))}
          </div>

          <div className="mt-8 flex justify-center items-center space-x-4">
            <div className="h-10 bg-gray-200 rounded w-24"></div>
            <div className="h-6 bg-gray-200 rounded w-32"></div>
            <div className="h-10 bg-gray-200 rounded w-24"></div>
          </div>

          <div className="mt-4 text-center">
            <div className="h-4 bg-gray-200 rounded w-48 mx-auto"></div>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <Toast />
      <div className="container mx-auto">
        <h1 className="text-3xl font-bold text-slate-800 mb-8">Charities</h1>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {charities.map((charity) => {
            const progress = calculateProgress(charity.raisedAmount, charity.targetAmount);
            return (
              <div key={charity.id} className="bg-white rounded-lg shadow-md flex flex-col min-h-[280px] group">
                <Link href={`/charities/${charity.id}`} className="flex-grow p-6 pb-3 hover:bg-gray-50 transition-colors duration-200">
                  <div>
                    <h2 className="text-xl font-semibold text-slate-800 mb-2 line-clamp-1 group-hover:text-violet-600 transition-colors duration-200">
                      {charity.name}
                    </h2>
                    <div className="mb-4">
                      <p className="text-slate-600 line-clamp-3 min-h-[4.5rem]">
                        {truncateText(charity.description, 120)}
                      </p>
                    </div>
                    
                    <div className="space-y-1">
                      <div className="flex justify-between items-center">
                        <span className="text-sm text-slate-500">Target: ${charity.targetAmount.toLocaleString()}</span>
                        <span className="text-sm text-slate-500">Raised: ${charity.raisedAmount.toLocaleString()}</span>
                      </div>
                      
                      <div className="w-full bg-gray-200 rounded-full h-2.5">
                        <div
                          className="bg-violet-600 h-2.5 rounded-full transition-all duration-300"
                          style={{ width: `${progress}%` }}
                        ></div>
                      </div>
                      
                      <div className="text-right">
                        <span className="text-sm font-medium text-violet-600">{progress}%</span>
                      </div>
                    </div>
                  </div>
                </Link>

                <div className="px-6 py-4 border-t border-gray-100">
                  <div className="flex justify-between items-center">
                    <span className="text-sm text-slate-500">By: {charity.fundraiserName}</span>
                    <Link
                      href={`/charities/${charity.id}/donate`}
                      className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-violet-600 hover:bg-violet-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-violet-500 transition-colors duration-200"
                    >
                      Donate Now
                    </Link>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        {/* Pagination Controls */}
        <div className="mt-8 flex justify-center items-center space-x-4">
          <button
            onClick={() => handlePageChange(page - 1)}
            disabled={page === 0}
            className={`px-4 py-2 rounded-lg ${
              page === 0
                ? 'bg-gray-200 text-gray-500 cursor-not-allowed'
                : 'bg-violet-600 text-white hover:bg-violet-700'
            }`}
          >
            Previous
          </button>
          <span className="text-slate-600">
            Page {page + 1} of {totalPages}
          </span>
          <button
            onClick={() => handlePageChange(page + 1)}
            disabled={page === totalPages - 1}
            className={`px-4 py-2 rounded-lg ${
              page === totalPages - 1
                ? 'bg-gray-200 text-gray-500 cursor-not-allowed'
                : 'bg-violet-600 text-white hover:bg-violet-700'
            }`}
          >
            Next
          </button>
        </div>

        <div className="mt-4 text-center text-sm text-slate-500">
          Showing {charities.length} of {totalElements} charities
        </div>
      </div>
    </main>
  );
} 