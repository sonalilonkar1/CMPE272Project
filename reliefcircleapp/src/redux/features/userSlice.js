import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';
import { USER_ENDPOINTS } from '@/utils/api';
import { getSession } from 'next-auth/react';

// Async thunk for fetching user profile
export const fetchUserProfile = createAsyncThunk(
  'user/fetchProfile',
  async (_, { rejectWithValue }) => {
    try {
      const session = await getSession();
      if (!session?.accessToken) {
        throw new Error('No access token found');
      }

      const response = await axios.get(USER_ENDPOINTS.PROFILE, {
        headers: {
          'Authorization': `Bearer ${session.accessToken}`
        }
      });
      return {...response.data, token: session.accessToken};
    } catch (error) {
      return rejectWithValue(error.response?.data || 'Failed to fetch user profile');
    }
  }
);

// Async thunk for fetching Stripe account info and charges
export const fetchStripeAccountInfo = createAsyncThunk(
  'user/fetchStripeAccountInfo',
  async (stripeId, { rejectWithValue }) => {
    try {
      const response = await axios.get(`/api/stripe-account-info?stripeId=${stripeId}`);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data || 'Failed to fetch Stripe account info');
    }
  }
);

const initialState = {
  profile: null,
  loading: false,
  error: null,
  stripeAccount: null,
  stripeAccountLoading: false,
  stripeAccountError: null,
};

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    clearUserError: (state) => {
      state.error = null;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchUserProfile.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchUserProfile.fulfilled, (state, action) => {
        state.loading = false;
        state.profile = action.payload;
      })
      .addCase(fetchUserProfile.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Stripe account info
      .addCase(fetchStripeAccountInfo.pending, (state) => {
        state.stripeAccountLoading = true;
        state.stripeAccountError = null;
      })
      .addCase(fetchStripeAccountInfo.fulfilled, (state, action) => {
        state.stripeAccountLoading = false;
        state.stripeAccount = action.payload;
      })
      .addCase(fetchStripeAccountInfo.rejected, (state, action) => {
        state.stripeAccountLoading = false;
        state.stripeAccountError = action.payload;
        state.stripeAccount = null;
      });
  }
});

export const { clearUserError } = userSlice.actions;
export default userSlice.reducer; 