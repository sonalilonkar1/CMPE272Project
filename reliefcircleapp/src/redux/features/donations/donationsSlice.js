import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from '@/lib/axios';

const initialState = {
  donations: [],
  userDonations: [],
  loading: false,
  error: null,
};

export const fetchDonations = createAsyncThunk(
  'donations/fetchDonations',
  async (_, { rejectWithValue }) => {
    try {
      const response = await axios.get('/api/donations');
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

export const fetchUserDonations = createAsyncThunk(
  'donations/fetchUserDonations',
  async (_, { rejectWithValue }) => {
    try {
      const response = await axios.get('/api/donations/user');
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

export const fetchCharityDonations = createAsyncThunk(
  'donations/fetchCharityDonations',
  async (charityId, { rejectWithValue }) => {
    try {
      const response = await axios.get(`/api/donations/charity/${charityId}`);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

export const createDonation = createAsyncThunk(
  'donations/createDonation',
  async (donationData, { rejectWithValue }) => {
    try {
      const response = await axios.post('/api/donations', donationData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

const donationsSlice = createSlice({
  name: 'donations',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchDonations.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchDonations.fulfilled, (state, action) => {
        state.loading = false;
        state.donations = action.payload;
      })
      .addCase(fetchDonations.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(fetchUserDonations.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchUserDonations.fulfilled, (state, action) => {
        state.loading = false;
        state.userDonations = action.payload;
      })
      .addCase(fetchUserDonations.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(createDonation.fulfilled, (state, action) => {
        state.donations.push(action.payload);
        state.userDonations.push(action.payload);
      });
  },
});

export const { clearError } = donationsSlice.actions;
export default donationsSlice.reducer;