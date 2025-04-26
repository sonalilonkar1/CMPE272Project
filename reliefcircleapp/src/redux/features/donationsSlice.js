import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axiosInstance from '@/lib/axios';

// Async thunk for fetching donor's donations
export const fetchDonorDonations = createAsyncThunk(
  'donations/fetchDonorDonations',
  async (donorId, { rejectWithValue }) => {
    try {
      const response = await axiosInstance.get(`/donations?donorId=${donorId}`);
      return Array.isArray(response.data) ? response.data : [];
    } catch (error) {
      return rejectWithValue(error.response?.data || 'Failed to fetch donations');
    }
  }
);

const initialState = {
  donations: [],
  status: 'idle', // 'idle' | 'loading' | 'succeeded' | 'failed'
  error: null,
};

const donationsSlice = createSlice({
  name: 'donations',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchDonorDonations.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(fetchDonorDonations.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.donations = action.payload;
        state.error = null;
      })
      .addCase(fetchDonorDonations.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
        state.donations = [];
      });
  },
});

export default donationsSlice.reducer; 