import { configureStore } from '@reduxjs/toolkit';
import authReducer from './features/authSlice';
import charitiesReducer from './features/charitiesSlice';
import donationsReducer from './features/donationsSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    charities: charitiesReducer,
    donations: donationsReducer,
  },
}); 