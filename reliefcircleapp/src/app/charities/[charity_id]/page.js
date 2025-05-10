import { Suspense } from 'react'
import CharityContent, { CharityDetailSkeleton } from './CharityContent'
import { CHARITY_ENDPOINTS } from '@/utils/api'

async function getCharity(id) {
  try {
    const response = await fetch(CHARITY_ENDPOINTS.DETAIL(id), {
      headers: {
        'Authorization': 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoiRE9OT1IiLCJzdWIiOiJ0ZXN0dXNlcjJAZXhhbXBsZS5jb20iLCJpYXQiOjE3NDU4NjM4MTEsImV4cCI6MTc0NTk1MDIxMX0.EhEe-STkhRAaE-H8QibTIIV_RDQ4FqSnMlvp2txv0Kc13t_7eNgsAMAKwG6i937vz1TjGzu1g5xUS-pjT8q-3g'
      },
      cache: 'no-store' // This ensures fresh data on each request
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch charity data');
    }
    
    return response.json();
  } catch (error) {
    console.error('Error fetching charity:', error);
    throw error;
  }
}

export default async function CharityPage({ params }) {
  const charity = await getCharity(params.charity_id);

  return (
    <Suspense fallback={<CharityDetailSkeleton />}>
      <CharityContent initialData={charity} />
    </Suspense>
  );
}
