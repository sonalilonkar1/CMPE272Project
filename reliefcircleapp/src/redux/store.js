import { configureStore } from '@reduxjs/toolkit';
import authReducer from '@/redux/features/auth/authSlice';
import charitiesReducer from '@/redux/features/charities/charitiesSlice';
import donationsReducer from '@/redux/features/donations/donationsSlice';
import verificationsReducer from '@/redux/features/verifications/verificationsSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    charities: charitiesReducer,
    donations: donationsReducer,
    verifications: verificationsReducer,
  },
  middleware: (getDefaultMiddleware) => getDefaultMiddleware(),
  devTools: process.env.NODE_ENV !== 'production',
});
