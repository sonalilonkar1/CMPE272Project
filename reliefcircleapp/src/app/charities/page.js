
'use client'

import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchCharities, selectCharities } from "@/redux/features/charities/charitiesSlice";
import CharityCard from "@/components/charity/CharityCard";

const CharitiesPage = () => {
  const dispatch = useDispatch();
  const charities = useSelector(selectCharities);
  const charityStatus = useSelector((state) => state.charities.status);

  useEffect(() => {
    if (charityStatus === "idle") {
      dispatch(fetchCharities());
    }
  }, [dispatch, charityStatus]);

  return (
    <div className="charities-page p-4 max-w-screen-lg mx-auto">
      <h1 className="text-2xl font-bold mb-6">Charities Seeking Donations</h1>

      {charities.length === 0 ? (
        <p>No charity requests found at the moment.</p>
      ) : (
        <div className="charity-list space-y-6">
          {charities.map((charity) => (
            <CharityCard key={charity.id} charity={charity} />
          ))}
        </div>
      )}
    </div>
  );
};


export default CharitiesPage;
