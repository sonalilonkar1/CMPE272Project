import { configureStore } from '@reduxjs/toolkit';
import authReducer from './features/authSlice';
import charitiesReducer from './features/charitiesSlice';
import donationsReducer from './features/donationsSlice';
import userReducer from './features/userSlice';
import updatesReducer from './features/updatesSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    charities: charitiesReducer,
    donations: donationsReducer,
    user: userReducer,
    updates: updatesReducer,
  },
});

export default store; 