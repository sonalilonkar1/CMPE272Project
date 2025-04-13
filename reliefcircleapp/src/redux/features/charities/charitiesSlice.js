import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from '@/lib/axios';

const initialState = {
  charities: [],
  currentCharity: null,
  loading: false,
  error: null,
};

export const fetchCharities = createAsyncThunk(
  'charities/fetchCharities',
  async (_, { rejectWithValue }) => {
    try {
      const response = await axios.get('/api/charities');
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

export const fetchCharityById = createAsyncThunk(
  'charities/fetchCharityById',
  async (id, { rejectWithValue }) => {
    try {
      const response = await axios.get(`/api/charities/${id}`);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

export const createCharity = createAsyncThunk(
  'charities/createCharity',
  async (charityData, { rejectWithValue }) => {
    try {
      const response = await axios.post('/api/charities', charityData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

export const updateCharity = createAsyncThunk(
  'charities/updateCharity',
  async ({ id, data }, { rejectWithValue }) => {
    try {
      const response = await axios.put(`/api/charities/${id}`, data);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

const charitiesSlice = createSlice({
  name: 'charities',
  initialState,
  reducers: {
    clearCurrentCharity: (state) => {
      state.currentCharity = null;
    },
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCharities.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCharities.fulfilled, (state, action) => {
        state.loading = false;
        state.charities = action.payload;
      })
      .addCase(fetchCharities.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(fetchCharityById.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCharityById.fulfilled, (state, action) => {
        state.loading = false;
        state.currentCharity = action.payload;
      })
      .addCase(fetchCharityById.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(createCharity.fulfilled, (state, action) => {
        state.charities.push(action.payload);
      })
      .addCase(updateCharity.fulfilled, (state, action) => {
        const index = state.charities.findIndex(charity => charity.id === action.payload.id);
        if (index !== -1) {
          state.charities[index] = action.payload;
        }
        if (state.currentCharity?.id === action.payload.id) {
          state.currentCharity = action.payload;
        }
      });
  },
});

export const { clearCurrentCharity, clearError } = charitiesSlice.actions;
export const selectCharities = (state) => state.charities.charities;
export default charitiesSlice.reducer;