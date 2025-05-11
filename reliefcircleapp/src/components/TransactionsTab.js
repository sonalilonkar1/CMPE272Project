import { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { fetchStripeAccountInfo } from '@/redux/features/userSlice';

export default function TransactionsTab({ fundraiserId }) {
  const { profile, stripeAccount, stripeAccountLoading, stripeAccountError } = useSelector((state) => state.user);
  const dispatch = useDispatch();

  useEffect(() => {
    if (profile?.stripeId) {
      dispatch(fetchStripeAccountInfo(profile.stripeId));
    }
  }, [dispatch, profile?.stripeId]);

  if (stripeAccountLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-violet-600"></div>
      </div>
    );
  }

  if (stripeAccountError) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-red-600">{typeof stripeAccountError === 'string' ? stripeAccountError : 'Failed to load Stripe account info.'}</div>
      </div>
    );
  }

  const transactions = stripeAccount?.charges || [];

  return (
    <div className="space-y-6">
      {/* Stripe Connection Section */}
      <div className="bg-green-50 border border-green-200 rounded-lg p-6 mb-4">
        <div className="flex items-center mb-4">
          <span className="inline-flex items-center justify-center h-8 w-8 rounded-full bg-green-100 text-green-600 mr-3">
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7" />
            </svg>
          </span>
          <span className="text-lg font-semibold text-green-700">Stripe Account Connected</span>
        </div>
        <dl className="grid grid-cols-1 sm:grid-cols-2 gap-x-8 gap-y-2">
          <div>
            <dt className="text-sm font-medium text-gray-600">Business Type</dt>
            <dd className="text-base text-gray-900">{stripeAccount?.business_type || 'N/A'}</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-600">Account Type</dt>
            <dd className="text-base text-gray-900 capitalize">{stripeAccount?.type || 'N/A'}</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-600">Country</dt>
            <dd className="text-base text-gray-900">{stripeAccount?.country || 'N/A'}</dd>
          </div>
          {stripeAccount?.email && (
            <div>
              <dt className="text-sm font-medium text-gray-600">Stripe Email</dt>
              <dd className="text-base text-gray-900">{stripeAccount?.email || 'N/A'}</dd>
            </div>
          )}
          {stripeAccount?.business_profile && (
            <>
              {stripeAccount.business_profile.name && (
                <div>
                  <dt className="text-sm font-medium text-gray-600">Business Name</dt>
                  <dd className="text-base text-gray-900">{stripeAccount?.business_profile?.name || 'N/A'}</dd>
                </div>
              )}
              {stripeAccount?.business_profile?.support_email && (
                <div>
                  <dt className="text-sm font-medium text-gray-600">Support Email</dt>
                  <dd className="text-base text-gray-900">{stripeAccount?.business_profile?.support_email || 'N/A'}</dd>
                </div>
              )}
              {stripeAccount.business_profile.support_phone && (
                <div>
                  <dt className="text-sm font-medium text-gray-600">Support Phone</dt>
                  <dd className="text-base text-gray-900">{stripeAccount?.business_profile?.support_phone || 'N/A'}</dd>
                </div>
              )}
              {stripeAccount?.business_profile?.url && (
                <div>
                  <dt className="text-sm font-medium text-gray-600">Business URL</dt>
                  <dd className="text-base">
                    <a
                      href={stripeAccount?.business_profile?.url || 'N/A'}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-violet-600 hover:underline"
                    >
                      {stripeAccount?.business_profile?.url || 'N/A'}
                    </a>
                  </dd>
                </div>
              )}
            </>
          )}
        </dl>
        {stripeAccount?.balance && (
          <div className="mb-4">
            <span className="text-sm font-medium text-gray-600">Total Balance:</span>
            <span className="text-base text-gray-900 ml-2">
              {stripeAccount.balance.available && stripeAccount.balance.available.length > 0
                ? `$${(stripeAccount.balance.available[0].amount / 100).toFixed(2)} ${stripeAccount.balance.available[0].currency.toUpperCase()}`
                : 'N/A'}
            </span>
          </div>
        )}
      </div>

      {/* Transactions List */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-200">
          <h3 className="text-lg font-medium text-gray-900">Transaction History</h3>
        </div>
        {transactions.length === 0 ? (
          <div className="px-6 py-8 text-center text-gray-500">
            No transactions found
          </div>
        ) : (
          <div className="divide-y divide-gray-200">
            {transactions.map((transaction) => (
              <div key={transaction.id} className="px-6 py-4">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-medium text-gray-900">
                      {transaction.description || 'Stripe Charge'}
                    </p>
                    <p className="text-sm text-gray-500">
                      {transaction.created ? new Date(transaction.created * 1000).toLocaleDateString() : ''}
                    </p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-medium text-gray-900">
                      ${transaction.amount ? (transaction.amount / 100).toFixed(2) : '0.00'}
                    </p>
                    <p className={`text-sm ${
                      transaction.status === 'succeeded' ? 'text-green-600' : 
                      transaction.status === 'pending' ? 'text-yellow-600' : 'text-red-600'
                    }`}>
                      {transaction.status ? transaction.status.charAt(0).toUpperCase() + transaction.status.slice(1) : ''}
                    </p>
                  </div>
                </div>
                {transaction.receipt_url && (
                  <div className="mt-2 text-xs">
                    <a href={transaction.receipt_url} target="_blank" rel="noopener noreferrer" className="text-violet-600 hover:underline">View Receipt</a>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
} 