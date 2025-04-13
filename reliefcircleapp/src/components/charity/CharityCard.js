const CharityCard = ({ charity }) => {
    return (
      <div className="charity-card p-4 bg-white shadow rounded-md">
        <h3 className="text-xl font-semibold">{charity.name}</h3>
        <p className="mt-2">{charity.description}</p>
        <p className="mt-2 font-bold">Target Amount: ${charity.targetAmount}</p>
        <p className="mt-2 text-sm">
          Status: {charity.isVerified ? "Verified" : "Pending Verification"}
        </p>
        <button className="mt-4 px-6 py-2 bg-blue-500 text-white rounded-md">
          Donate Now
        </button>
      </div>
    );
  };
  
  export default CharityCard;