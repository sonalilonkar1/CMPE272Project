import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from '@/lib/axios';

// Async thunk for fetching donor's donations
export const fetchDonorDonations = createAsyncThunk(
  'donations/fetchDonorDonations',
  async ({ donorId, token }, { rejectWithValue }) => {
    try {
      const response = await axios.get(`/donations?donorId=${donorId}&sortBy=createdAt&sortDirection=desc`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      return Array.isArray(response.data?.content) ? response?.data?.content : [];
    } catch (error) {
      let errorMsg = 'Failed to fetch donations';
      if (error.response?.data) {
        if (typeof error.response.data === 'string') {
          errorMsg = error.response.data;
        } else if (typeof error.response.data.message === 'string') {
          errorMsg = error.response.data.message;
        } else {
          errorMsg = JSON.stringify(error.response.data);
        }
      }
      return rejectWithValue(errorMsg);
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