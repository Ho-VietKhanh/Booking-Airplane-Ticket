import axios from "axios";

export const login = async (username, password) => {
  try {
    const response = await axios.post(
      "https://nail-terminals-product-directed.trycloudflare.com/api/auth/login",
      {
        username,
        password,
      }
    );

    // Nếu API trả về token trong response.data.result.token
    if (response.data?.token) {
      localStorage.setItem("token", response.data.token);
      localStorage.setItem("username", username);
    }

    return response.data;
  } catch (error) {
    console.error("Login error:", error);

    return {
      error: true,
      message: error.response?.data?.message || "Login failed!",
    };
  }
};

export function clearExpiredToken(type) {
  // ...implementation...
}
