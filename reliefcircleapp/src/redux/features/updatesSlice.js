import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';
import { showToast } from '@/components/Toast';
import { FUNDRAISER_UPDATES_ENDPOINT, UPDATES_ENDPOINT, VOLUNTEER_UPDATES_ENDPOINT } from '@/utils/api';

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

// Async thunk for fetching volunteer updates
export const fetchVolunteerUpdates = createAsyncThunk(
  'updates/fetchVolunteerUpdates',
  async ({ token }, { rejectWithValue }) => {
    try {
      const response = await axios.get(
        VOLUNTEER_UPDATES_ENDPOINT,
        {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );
      return response.data;
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch volunteer updates';
      showToast.error(errorMessage);
      return rejectWithValue(errorMessage);
    }
  }
);

const initialState = {
  updates: [],
  volunteerUpdates: [],
  currentPage: 0,
  totalPages: 0,
  totalElements: 0,
  pageSize: 10,
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
        state.updates = action.payload.content || action.payload;
        state.currentPage = action.payload.pageNumber ?? 0;
        state.totalPages = action.payload.totalPages ?? 0;
        state.totalElements = action.payload.totalElements ?? 0;
        state.pageSize = action.payload.pageSize ?? 10;
        state.error = null;
      })
      .addCase(fetchFundraiserUpdates.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
        state.updates = [];
      })
      .addCase(fetchVolunteerUpdates.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(fetchVolunteerUpdates.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.volunteerUpdates = action.payload;
        state.error = null;
      })
      .addCase(fetchVolunteerUpdates.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
        state.volunteerUpdates = [];
      });
  }
});

export const { clearError } = updatesSlice.actions;
export default updatesSlice.reducer; 