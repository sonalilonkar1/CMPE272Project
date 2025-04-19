import { configureStore } from '@reduxjs/toolkit';
import charitiesReducer from './features/charitiesSlice';

export const store = configureStore({
  reducer: {
    charities: charitiesReducer,
  },
}); 