import { useState, useEffect } from 'react';
import ConnectStripeButton from './ConnectStripeButton';
import { showToast } from './Toast';

export default function TransactionsTab({ fundraiserId }) {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [stripeConnected, setStripeConnected] = useState(false);

  useEffect(() => {
    fetchTransactions();
    checkStripeStatus();
  }, [fundraiserId]);

  const fetchTransactions = async () => {
    try {
      const res = await fetch(`/api/transactions?fundraiserId=${fundraiserId}`);
      const data = await res.json();
      
      if (!res.ok) {
        throw new Error(data.message || 'Failed to fetch transactions');
      }
      
      setTransactions(data.transactions || []);
    } catch (error) {
      console.error('Error fetching transactions:', error);
      showToast.error('Failed to load transactions');
    } finally {
      setLoading(false);
    }
  };

  const checkStripeStatus = async () => {
    try {
      const res = await fetch(`/api/stripe-status?fundraiserId=${fundraiserId}`);
      const data = await res.json();
      
      if (!res.ok) {
        throw new Error(data.message || 'Failed to check Stripe status');
      }
      
      setStripeConnected(data.connected || false);
    } catch (error) {
      console.error('Error checking Stripe status:', error);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-violet-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Stripe Connection Section */}
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Payment Processing</h3>
        {stripeConnected ? (
          <div className="flex items-center text-green-600">
            <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7" />
            </svg>
            <span>Connected with Stripe</span>
          </div>
        ) : (
          <div className="space-y-4">
            <p className="text-gray-600">
              Connect your Stripe account to start receiving donations and managing your transactions.
            </p>
            <ConnectStripeButton fundraiserId={fundraiserId} />
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
                    <p className="text-sm font-medium text-gray-900">{transaction.description}</p>
                    <p className="text-sm text-gray-500">{new Date(transaction.date).toLocaleDateString()}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-medium text-gray-900">${transaction.amount.toFixed(2)}</p>
                    <p className={`text-sm ${
                      transaction.status === 'completed' ? 'text-green-600' : 
                      transaction.status === 'pending' ? 'text-yellow-600' : 'text-red-600'
                    }`}>
                      {transaction.status.charAt(0).toUpperCase() + transaction.status.slice(1)}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
} 