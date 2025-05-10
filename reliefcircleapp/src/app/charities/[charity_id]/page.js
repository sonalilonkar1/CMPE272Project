'use client'
import { Suspense, useEffect } from 'react'
import CharityContent, { CharityDetailSkeleton } from './CharityContent'
import { useDispatch, useSelector } from 'react-redux'
import { fetchCharityById } from '@/redux/features/charitiesSlice'

export default function CharityPage({ params }) {
  const dispatch = useDispatch();
  const { currentCharity, status, error } = useSelector((state) => state.charities);

  useEffect(() => {
    dispatch(fetchCharityById(params.charity_id));
  }, [dispatch, params.charity_id]);

  if (status === 'loading') {
    return <CharityDetailSkeleton />;
  }

  if (status === 'failed') {
    return <div className="text-red-600 p-8">Error: {error || 'Failed to fetch charity data.'}</div>;
  }

  if (!currentCharity) {
    return <div className="p-8">No charity found.</div>;
  }

  return (
    <Suspense fallback={<CharityDetailSkeleton />}>
      <CharityContent initialData={currentCharity} />
    </Suspense>
  );
}
