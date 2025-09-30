import { Outlet } from "react-router-dom";
import HeaderComponent from "../components/Header/Header";
import FooterComponent from "../components/Footer/Footer";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css"; // Đừng quên import CSS của Toastify

function RootLayout() {
  return (
    <div className="min-h-screen flex flex-col">
      <HeaderComponent />

      <main className="flex-1">
        <Outlet />
      </main>

      <FooterComponent />

      {/* Đặt ToastContainer ở đây */}
      <ToastContainer
        position="top-right" // Vị trí thông báo
        autoClose={3000} // Tự đóng sau 3 giây
        hideProgressBar={false} // Hiển thị thanh tiến trình
        newestOnTop={false} // Tin mới không đè lên tin cũ
        closeOnClick // Đóng khi click
        rtl={false} // Không phải right-to-left
        pauseOnFocusLoss // Tạm dừng khi mất focus
        draggable // Có thể kéo thông báo
        pauseOnHover // Tạm dừng khi hover
      />
    </div>
  );
}

export default RootLayout;
