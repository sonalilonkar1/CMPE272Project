import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';
import { showToast } from '@/components/Toast';
import { CHARITY_ENDPOINTS } from '@/utils/api';

// Async thunk for fetching charities
export const fetchCharities = createAsyncThunk(
  'charities/fetchCharities',
  async ({ page = 0, pageSize = 6 }, { rejectWithValue }) => {
    try {
      const response = await axios.get(`${CHARITY_ENDPOINTS.LIST}?page=${page}&pageSize=${pageSize}`);
      return response.data;
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch charities';
      showToast.error(errorMessage);
      return rejectWithValue(errorMessage);
    }
  }
);

// Async thunk for fetching a single charity
export const fetchCharityById = createAsyncThunk(
  'charities/fetchCharityById',
  async (charityId, { rejectWithValue }) => {
    try {
      const response = await axios.get(CHARITY_ENDPOINTS.DETAIL(charityId), {
        headers: {
          'Authorization': 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoiRE9OT1IiLCJzdWIiOiJ0ZXN0dXNlcjJAZXhhbXBsZS5jb20iLCJpYXQiOjE3NDU4NjM4MTEsImV4cCI6MTc0NTk1MDIxMX0.EhEe-STkhRAaE-H8QibTIIV_RDQ4FqSnMlvp2txv0Kc13t_7eNgsAMAKwG6i937vz1TjGzu1g5xUS-pjT8q-3g'
        }
      });
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
      const response = await axios.post(CHARITY_ENDPOINTS.CREATE, charityData);
      showToast.success('Charity created successfully!');
      return response.data;
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Failed to create charity';
      showToast.error(errorMessage);
      return rejectWithValue(errorMessage);
    }
  }
);

// Async thunk for fetching fundraiser's charities
export const fetchFundraiserCharities = createAsyncThunk(
  'charities/fetchFundraiserCharities',
  async ({ fundraiserId, page = 0, pageSize = 10, token }, { rejectWithValue }) => {
    try {
      const response = await axios.get(`${CHARITY_ENDPOINTS.FUNDRAISER_CHARITIES(fundraiserId)}&page=${page}&pageSize=${pageSize}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data || 'Failed to fetch fundraiser charities');
    }
  }
);

const initialState = {
  charities: [],
  currentCharity: null,
  fundraiserCharities: [],
  currentPage: 0,
  totalPages: 0,
  totalElements: 0,
  pageSize: 6,
  loading: false,
  error: null,
  fundraiserCharitiesPage: 0,
  fundraiserCharitiesTotalPages: 0,
  fundraiserCharitiesTotalElements: 0,
  fundraiserCharitiesPageSize: 10
};

const charitiesSlice = createSlice({
  name: 'charities',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    }
  },
  extraReducers: (builder) => {
    builder
      // Fetch Charities
      .addCase(fetchCharities.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCharities.fulfilled, (state, action) => {
        state.loading = false;
        state.charities = action.payload.content;
        state.currentPage = action.payload.pageNumber;
        state.totalPages = action.payload.totalPages;
        state.totalElements = action.payload.totalElements;
        state.pageSize = action.payload.pageSize;
      })
      .addCase(fetchCharities.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Fetch Charity by ID
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
      // Create Charity
      .addCase(createCharity.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createCharity.fulfilled, (state, action) => {
        state.loading = false;
        state.charities.unshift(action.payload);
      })
      .addCase(createCharity.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Fetch Fundraiser Charities
      .addCase(fetchFundraiserCharities.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(fetchFundraiserCharities.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.fundraiserCharities = action.payload.content;
        state.fundraiserCharitiesPage = action.payload.pageNumber;
        state.fundraiserCharitiesTotalPages = action.payload.totalPages;
        state.fundraiserCharitiesTotalElements = action.payload.totalElements;
        state.fundraiserCharitiesPageSize = action.payload.pageSize;
        state.error = null;
      })
      .addCase(fetchFundraiserCharities.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
        state.fundraiserCharities = [];
      });
  }
});

export const { clearError } = charitiesSlice.actions;
export default charitiesSlice.reducer; 