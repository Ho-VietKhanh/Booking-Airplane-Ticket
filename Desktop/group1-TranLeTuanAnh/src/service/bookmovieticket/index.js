import { instance } from "../instance";

// Lấy danh sách ghế theo suất chiếu
export const getSeatsByShowtime = async (showtimeId) => {
  try {
    const response = await instance.get(`/showtimes/${showtimeId}/seats`);
    return {
      error: false,
      result: response.result,
      message: response.message,
    };
  } catch (error) {
    console.error("Get seats error:", error);
    return {
      error: true,
      message: error.response?.message || "Failed to fetch seats",
    };
  }
};

// Đặt vé
export const bookTickets = async (bookingData) => {
  try {
    const token = localStorage.getItem("token");
    const response = await instance.post("/bookings", bookingData, {
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
    console.error("Book tickets error:", error);
    return {
      error: true,
      message: error.response?.message || "Failed to book tickets",
    };
  }
};