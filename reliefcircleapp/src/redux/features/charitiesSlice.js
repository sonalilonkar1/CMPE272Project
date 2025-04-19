import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axiosInstance from '@/lib/axios';

// Async thunk for fetching charities
export const fetchCharities = createAsyncThunk(
  'charities/fetchCharities',
  async (_, { rejectWithValue }) => {
    try {
      const response = await axiosInstance.get('/charities');
      // Ensure we're returning an array
      return Array.isArray(response.data) ? response.data : [];
    } catch (error) {
      return rejectWithValue(error.response?.data || 'Failed to fetch charities');
    }
  }
);

const initialState = {
  charities: [],
  status: 'idle', // 'idle' | 'loading' | 'succeeded' | 'failed'
  error: null,
};

const charitiesSlice = createSlice({
  name: 'charities',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchCharities.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(fetchCharities.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.charities = action.payload;
        state.error = null;
      })
      .addCase(fetchCharities.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
        state.charities = [];
      });
  },
});

export default charitiesSlice.reducer; 