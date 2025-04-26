import { configureStore } from '@reduxjs/toolkit';
import charitiesReducer from './features/charitiesSlice';
import donationsReducer from './features/donationsSlice';

export const store = configureStore({
  reducer: {
    charities: charitiesReducer,
    donations: donationsReducer,
  },
}); 