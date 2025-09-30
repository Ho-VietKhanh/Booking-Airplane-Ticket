import { instance } from "../instance";

export const logout = async (token) => {
  try {
    const response = await instance.post("auth/logout", {
      token,
    });
    return response;
  } catch (error) {
    console.error("Logout error:", error);

    return {
      error: true,
      message: error.response?.data?.message || "Logout failed",
    };
  }
};
