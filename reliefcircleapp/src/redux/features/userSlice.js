import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';
import { USER_ENDPOINTS } from '@/utils/api';
import { getSession } from 'next-auth/react';
import { showToast } from '@/components/Toast';

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

// Async thunk for volunteer signup
export const signupAsVolunteer = createAsyncThunk(
  'user/signupAsVolunteer',
  async ({ token }, { rejectWithValue }) => {
    try {
      const response = await axios.put(
        USER_ENDPOINTS.SIGNUP_VOLUNTEER,
        {},
        {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );
      showToast.success('Successfully signed up as a volunteer!');
      return response.data;
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Failed to sign up as volunteer';
      showToast.error(errorMessage);
      return rejectWithValue(errorMessage);
    }
  }
);

// Async thunk for fetching donor dashboard stats
export const fetchDonorStats = createAsyncThunk(
  'user/fetchDonorStats',
  async ({ token }, { rejectWithValue }) => {
    try {
      const response = await axios.get(USER_ENDPOINTS.STATS_DONOR, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      return response.data;
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch donor stats';
      showToast.error(errorMessage);
      return rejectWithValue(errorMessage);
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
  volunteerStatus: 'idle',
  volunteerError: null,
  donorStats: null,
  donorStatsLoading: false,
  donorStatsError: null
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
      })
      .addCase(signupAsVolunteer.pending, (state) => {
        state.volunteerStatus = 'loading';
        state.volunteerError = null;
      })
      .addCase(signupAsVolunteer.fulfilled, (state, action) => {
        state.volunteerStatus = 'succeeded';
        state.profile = {
          ...state.profile,
          isVolunteer: true
        };
        state.volunteerError = null;
      })
      .addCase(signupAsVolunteer.rejected, (state, action) => {
        state.volunteerStatus = 'failed';
        state.volunteerError = action.payload;
      })
      .addCase(fetchDonorStats.pending, (state) => {
        state.donorStatsLoading = true;
        state.donorStatsError = null;
      })
      .addCase(fetchDonorStats.fulfilled, (state, action) => {
        state.donorStatsLoading = false;
        state.donorStats = action.payload;
      })
      .addCase(fetchDonorStats.rejected, (state, action) => {
        state.donorStatsLoading = false;
        state.donorStatsError = action.payload;
      });
  }
});

export const { clearUserError } = userSlice.actions;
export default userSlice.reducer; 