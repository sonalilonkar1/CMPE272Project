// src/redux/features/verifications/verificationsSlice.js
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from '@/lib/axios';

const initialState = {
  verifications: [],
  loading: false,
  error: null,
};

export const fetchVerifications = createAsyncThunk(
  'verifications/fetchVerifications',
  async (_, { rejectWithValue }) => {
    try {
      const response = await axios.get('/api/verifications');
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

export const fetchCharityVerifications = createAsyncThunk(
  'verifications/fetchCharityVerifications',
  async (charityId, { rejectWithValue }) => {
    try {
      const response = await axios.get(`/api/verifications/charity/${charityId}`);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

export const submitVerification = createAsyncThunk(
  'verifications/submitVerification',
  async (verificationData, { rejectWithValue }) => {
    try {
      const response = await axios.post('/api/verifications', verificationData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

export const reviewVerification = createAsyncThunk(
  'verifications/reviewVerification',
  async ({ id, reviewData }, { rejectWithValue }) => {
    try {
      const response = await axios.put(`/api/verifications/${id}/review`, reviewData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

const verificationsSlice = createSlice({
  name: 'verifications',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchVerifications.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchVerifications.fulfilled, (state, action) => {
        state.loading = false;
        state.verifications = action.payload;
      })
      .addCase(fetchVerifications.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(fetchCharityVerifications.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCharityVerifications.fulfilled, (state, action) => {
        state.loading = false;
        state.verifications = action.payload;
      })
      .addCase(fetchCharityVerifications.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(submitVerification.fulfilled, (state, action) => {
        state.verifications.push(action.payload);
      })
      .addCase(reviewVerification.fulfilled, (state, action) => {
        const index = state.verifications.findIndex(verification => verification.id === action.payload.id);
        if (index !== -1) {
          state.verifications[index] = action.payload;
        }
      });
  },
});

export const { clearError } = verificationsSlice.actions;
export default verificationsSlice.reducer;