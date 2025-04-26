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

// Async thunk for fetching a single charity
export const fetchCharityById = createAsyncThunk(
  'charities/fetchCharityById',
  async (charityId, { rejectWithValue }) => {
    try {
      const response = await axiosInstance.get(`/charities/${charityId}`);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data || 'Failed to fetch charity details');
    }
  }
);

// Async thunk for creating a new charity
export const createCharity = createAsyncThunk(
  'charities/createCharity',
  async (charityData, { rejectWithValue }) => {
    try {
      const response = await axiosInstance.post('/charities', charityData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data || 'Failed to create charity');
    }
  }
);

// Async thunk for fetching fundraiser's charities
export const fetchFundraiserCharities = createAsyncThunk(
  'charities/fetchFundraiserCharities',
  async (fundraiserId, { rejectWithValue }) => {
    try {
      const response = await axiosInstance.get(`/charities?fundraiserId=${fundraiserId}`);
      return Array.isArray(response.data) ? response.data : [];
    } catch (error) {
      return rejectWithValue(error.response?.data || 'Failed to fetch fundraiser charities');
    }
  }
);

const initialState = {
  charities: [],
  currentCharity: null,
  fundraiserCharities: [],
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
      })
      .addCase(fetchCharityById.pending, (state) => {
        state.status = 'loading';
        state.error = null;
        state.currentCharity = null;
      })
      .addCase(fetchCharityById.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.currentCharity = action.payload;
        state.error = null;
      })
      .addCase(fetchCharityById.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
        state.currentCharity = null;
      })
      .addCase(createCharity.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(createCharity.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.charities.push(action.payload);
        state.error = null;
      })
      .addCase(createCharity.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
      })
      .addCase(fetchFundraiserCharities.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(fetchFundraiserCharities.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.fundraiserCharities = action.payload;
        state.error = null;
      })
      .addCase(fetchFundraiserCharities.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
        state.fundraiserCharities = [];
      });
  },
});

export default charitiesSlice.reducer; 