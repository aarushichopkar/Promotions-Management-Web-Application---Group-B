import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import axiosInstance from '../component/utils/axiosinstance'

export const showProduct = createAsyncThunk(
  "showProduct",
  async (args, { rejectWithValue }) => {
    const response = await axiosInstance.get("product/getProducts");
    console.log(response);
    try {
      return response.data;
    } catch (error) {
      return rejectWithValue(error);
    }
  }
);

export const removeProduct = createAsyncThunk(
  "removeProduct",
  async (productId, { rejectWithValue }) => {
  try {
        const response = await axiosInstance.delete("product/deleteProduct?product_id=" + productId);
        console.log("Remove Product Response:", response);
        if (!response.status === 200) {
          throw new Error('Failed to delete product');
        }
        return productId;
      } catch (error) {
        return rejectWithValue(error.response.data);
      }
  }
);

export const addNewProduct = createAsyncThunk(
  "addProduct",
  async (product, { rejectWithValue }) => {
  try{
    const response = await axiosInstance.post("/product/add?promotion_id=" + product.proId, product, {
            headers: {
              'Content-Type': 'application/json'
            }
          });
    console.log("add product response:", response);
    if(!response.status === 200) {
      throw new Error('failed to add product');
    }

  } catch (error) {
      return rejectWithValue(error.response.data);
    }
  }
);

export const product = createSlice({
  name: "product",
  initialState: {
    product: [],
    loading: false,
    error: null,
    searchData: [],
  },

  reducers: {
    searchUser: (state, action) => {
      console.log(action.payload);
      state.searchData = action.payload;
    },
  },

  extraReducers: (builder) => {
    builder
      .addCase(showProduct.pending, (state) => {
        state.loading = true;
      })
      .addCase(showProduct.fulfilled, (state, action ) => {
        state.loading = false;
        state.product = action.payload;

      })
      .addCase(showProduct.rejected, (state, action) => {
        state.loading = false;
        state.product = [];
        state.error = action.payload;
//        state.product = action.payload;
      })

      .addCase(removeProduct.pending, (state) => {
        state.loading = true;
      })
      .addCase(removeProduct.fulfilled, (state, action) => {
        state.loading = false;
        state.product = state.product.filter(product => product.id !== action.payload);
      })
      .addCase(removeProduct.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
  }
});

export default product.reducer;

export const { searchUser} = product.actions;