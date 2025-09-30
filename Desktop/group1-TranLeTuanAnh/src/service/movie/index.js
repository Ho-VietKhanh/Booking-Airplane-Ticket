import { instance } from "../instance";

// Public function - no token needed
export const getAllProducts = async (params) => {
  try {
    const response = await instance.get("admin/products", { params });
    return {
      error: false,
      result: response.result,
      message: response.message,
    };
  } catch (error) {
    console.error("Get products error:", error);
    return {
      error: true,
      message: error.response?.message || "Failed to fetch products",
    };
  }
};

// Admin functions - require token
export const getProductById = async (id) => {
  try {
    const token = localStorage.getItem("token");
    const response = await instance.get(`admin/products/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return {
      error: false,
      result: response.result,
      message: response.message,
    };
  } catch (error) {
    console.error("Get product error:", error);
    return {
      error: true,
      message: error.response?.message || "Failed to fetch product details",
    };
  }
};

export const addProduct = async (formData) => {
  try {
    const token = localStorage.getItem("token");

    // Get the file and form data
    const requestData = JSON.parse(formData.get("request"));
    const file = formData.get("thumbnail");

    // Upload to Cloudinary first
    const imageUrl = await uploadToCloudinary(file);

    // Create the final request data
    const productData = {
      ...requestData,
      thumbnail: imageUrl,
    };

    const response = await instance.post("admin/products", productData, {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    return {
      error: false,
      result: response.result,
      message: response.message,
    };
  } catch (error) {
    console.error("Add product error:", error);
    return {
      error: true,
      message: error.response?.message || "Failed to add product",
    };
  }
};

export const updateProduct = async (id, formData) => {
  try {
    const token = localStorage.getItem("token");

    // Nếu formData là FormData (có file mới)
    if (formData instanceof FormData) {
      const requestData = JSON.parse(formData.get("request"));
      const file = formData.get("thumbnail");

      // Nếu có file mới, upload lên Cloudinary
      let productData = { ...requestData };
      if (file) {
        const imageUrl = await uploadToCloudinary(file);
        productData.thumbnail = imageUrl;
      }

      const response = await instance.put(`admin/products/${id}`, productData, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });
      return {
        error: false,
        result: response.result,
        message: response.message,
      };
    } else {
      // Xử lý trường hợp không có file mới (giữ nguyên code cũ)
      const response = await instance.put(`admin/products/${id}`, formData, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return {
        error: false,
        result: response.result,
        message: response.message,
      };
    }
  } catch (error) {
    console.error("Update product error:", error);
    return {
      error: true,
      message: error.response?.message || "Failed to update product",
    };
  }
};

export const deleteProduct = async (id) => {
  try {
    const token = localStorage.getItem("token");
    const response = await instance.delete(`admin/products/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return {
      error: false,
      result: response.result,
      message: response.message,
    };
  } catch (error) {
    console.error("Delete product error:", error);
    return {
      error: true,
      message: error.response?.message || "Failed to delete product",
    };
  }
};

export const deleteMultipleProducts = async (ids) => {
  try {
    const token = localStorage.getItem("token");
    const response = await instance.delete("admin/products", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      data: { ids },
    });
    return {
      error: false,
      result: response.data.result,
      message: response.data.message,
    };
  } catch (error) {
    console.error("Delete products error:", error);
    return {
      error: true,
      message: error.response?.message || "Failed to delete products",
    };
  }
};

export const updateProductStatus = async (productId, status) => {
  try {
    const token = localStorage.getItem("token");
    const response = await instance.patch(
      `admin/products/change-status/${productId}?status=${status}`,
      { status },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return {
      error: false,
      result: response.result,
      message: response.message,
    };
  } catch (error) {
    console.error("Update product status error:", error);
    return {
      error: true,
      message: error.response?.message || "Failed to update product status",
    };
  }
};

// Add this function after getAllProducts
const uploadToCloudinary = async (file) => {
  try {
    const CLOUDINARY_UPLOAD_PRESET = "phuocnt-cloudinary";
    const CLOUDINARY_CLOUD_NAME = "dl5dphe0f";

    const formData = new FormData();
    formData.append("file", file);
    formData.append("upload_preset", CLOUDINARY_UPLOAD_PRESET);

    const response = await fetch(
      `https://api.cloudinary.com/v1_1/${CLOUDINARY_CLOUD_NAME}/image/upload`,
      {
        method: "POST",
        body: formData,
      }
    );
    const data = await response.json();
    return data.secure_url;
  } catch (error) {
    console.error("Cloudinary upload error:", error);
    throw new Error("Failed to upload image to Cloudinary");
  }
};

export const getProductDetail = async (slug) => {
  try {
    const response = await instance.get(`/products/${slug}`);
    return response;
  } catch (error) {
    return {
      error: true,
      message: error.response?.data?.message || "Có lỗi xảy ra",
    };
  }
};

// Lấy danh sách phim (không cần token)
export const getAllMovies = async (params) => {
  try {
    const response = await instance.get("/movies", { params });
    return {
      error: false,
      result: response.result,
      message: response.message,
    };
  } catch (error) {
    console.error("Get movies error:", error);
    return {
      error: true,
      message: error.response?.message || "Failed to fetch movies",
    };
  }
};

// Lấy chi tiết phim theo slug (public)
export const getMovieDetail = async (slug) => {
  try {
    const response = await instance.get(`/movies/${slug}`);
    return {
      error: false,
      result: response.result,
      message: response.message,
    };
  } catch (error) {
    return {
      error: true,
      message: error.response?.data?.message || "Có lỗi xảy ra",
    };
  }
};

// Các hàm quản trị phim (nếu cần)
export const addMovie = async (formData) => {
  try {
    const token = localStorage.getItem("token");
    const requestData = JSON.parse(formData.get("request"));
    const file = formData.get("thumbnail");
    const imageUrl = await uploadToCloudinary(file);

    const movieData = {
      ...requestData,
      thumbnail: imageUrl,
    };

    const response = await instance.post("/admin/movies", movieData, {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    return {
      error: false,
      result: response.result,
      message: response.message,
    };
  } catch (error) {
    console.error("Add movie error:", error);
    return {
      error: true,
      message: error.response?.message || "Failed to add movie",
    };
  }
};

export const updateMovie = async (id, formData) => {
  try {
    const token = localStorage.getItem("token");
    if (formData instanceof FormData) {
      const requestData = JSON.parse(formData.get("request"));
      const file = formData.get("thumbnail");
      let movieData = { ...requestData };
      if (file) {
        const imageUrl = await uploadToCloudinary(file);
        movieData.thumbnail = imageUrl;
      }
      const response = await instance.put(`/admin/movies/${id}`, movieData, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });
      return {
        error: false,
        result: response.result,
        message: response.message,
      };
    } else {
      const response = await instance.put(`/admin/movies/${id}`, formData, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return {
        error: false,
        result: response.result,
        message: response.message,
      };
    }
  } catch (error) {
    console.error("Update movie error:", error);
    return {
      error: true,
      message: error.response?.message || "Failed to update movie",
    };
  }
};

export const deleteMovie = async (id) => {
  try {
    const token = localStorage.getItem("token");
    const response = await instance.delete(`/admin/movies/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return {
      error: false,
      result: response.result,
      message: response.message,
    };
  } catch (error) {
    console.error("Delete movie error:", error);
    return {
      error: true,
      message: error.response?.message || "Failed to delete movie",
    };
  }
};
