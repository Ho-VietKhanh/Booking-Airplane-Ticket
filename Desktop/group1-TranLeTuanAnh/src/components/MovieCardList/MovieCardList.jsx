import { GoHeart } from "react-icons/go";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import { useState } from "react";

// Nếu có chức năng đặt vé, hãy import hàm addTicketToCart hoặc tương tự
// import { addTicketToCart } from "../../service/cart/cart";

function MovieCardList({
  id,
  slug,
  title,
  description,
  genre,
  duration,
  thumbnail,
}) {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);

  // Nếu có chức năng đặt vé, sửa lại hàm này cho phù hợp
  const handleBookTicket = async () => {
    const token = localStorage.getItem("token");

    if (!token) {
      toast.warning("Vui lòng đăng nhập để đặt vé");
      navigate("/login");
      return;
    }

    setIsLoading(true);
    try {
      // const response = await addTicketToCart(id);
      // if (response.error) {
      //   toast.error(response.message || "Đặt vé thất bại");
      // } else {
      //   toast.success(response.message || "Đặt vé thành công");
      // }
      toast.success("Đặt vé thành công (demo)");
    } catch (error) {
      toast.error("Có lỗi xảy ra khi đặt vé");
    } finally {
      setIsLoading(false);
    }
  };

  const handleCardClick = () => {
    navigate(`/movies/${slug}`); // Điều hướng đến trang chi tiết phim
  };

  return (
    <div className="group cursor-pointer relative flex flex-col items-center text-center bg-white p-4 hover:shadow-xl transition duration-300 rounded-lg min-h-[480px]">
      {/* Image Section */}
      <div onClick={handleCardClick}>
        <div className="h-48 mt-4 flex items-center justify-center relative">
          <img
            src={thumbnail}
            alt={title}
            className="max-w-full max-h-full object-contain"
          />
          <GoHeart
            className="absolute -bottom-0 right-3 cursor-pointer"
            onClick={(e) => {
              e.stopPropagation();
              toast.info("Đã thêm vào yêu thích (demo)");
            }}
          />
        </div>

        {/* Text Section */}
        <div className="flex flex-col items-center mt-4 space-y-2 flex-1 w-full">
          {/* Title */}
          <div className="h-10 flex items-center justify-center">
            <h3 className="font-semibold text-lg line-clamp-1">{title}</h3>
          </div>

          {/* Description */}
          <div className="h-12 flex items-center justify-center">
            <p className="text-sm text-gray-500 line-clamp-2">{description}</p>
          </div>

          {/* Genre */}
          <div className="h-8 flex items-center justify-center">
            <p className="text-sm text-gray-700">{genre}</p>
          </div>

          {/* Duration */}
          <div className="h-10 flex items-center justify-center">
            <p className="font-semibold text-black">{duration} phút</p>
          </div>
        </div>
      </div>

      {/* Button Section */}
      <div className="w-full mt-2">
        <button
          className={`w-full bg-black text-white py-2 px-6 rounded-md text-sm ${
            isLoading ? "opacity-50 cursor-not-allowed" : "opacity-100"
          } transition duration-300`}
          onClick={handleBookTicket}
          disabled={isLoading}
        >
          {isLoading ? "Đang xử lý..." : "Đặt vé"}
        </button>
      </div>
    </div>
  );
}

export default MovieCardList;
