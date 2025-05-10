import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';
import { showToast } from '@/components/Toast';
import { FUNDRAISER_UPDATES_ENDPOINT } from '@/utils/api';

// Async thunk for fetching fundraiser updates
export const fetchFundraiserUpdates = createAsyncThunk(
  'updates/fetchFundraiserUpdates',
  async ({ token }, { rejectWithValue }) => {
    try {
      const response = await axios.get(FUNDRAISER_UPDATES_ENDPOINT, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      return response.data;
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch fundraiser updates';
      showToast.error(errorMessage);
      return rejectWithValue(errorMessage);
    }
  }
);

const initialState = {
  updates: [],
  status: 'idle',
  error: null
};

const updatesSlice = createSlice({
  name: 'updates',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchFundraiserUpdates.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(fetchFundraiserUpdates.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.updates = action.payload;
        state.error = null;
      })
      .addCase(fetchFundraiserUpdates.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
        state.updates = [];
      });
  }
});

export const { clearError } = updatesSlice.actions;
export default updatesSlice.reducer; 