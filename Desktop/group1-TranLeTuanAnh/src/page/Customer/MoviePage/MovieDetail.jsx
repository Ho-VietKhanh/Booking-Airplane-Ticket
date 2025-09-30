import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Heart } from "lucide-react";
import { getMovieDetail } from "../../../service/movie";
import { toast } from "react-toastify";
import imgMovie from "../../../assets/Rectangle 3.png";
import { FaSpinner } from "react-icons/fa";

// MOCK DATA cho thiết kế giao diện
const mockData = {
  title: "Your Name",
  description: "Hai thiếu niên hoán đổi thân xác và dần phát hiện ra mối liên kết kỳ lạ giữa họ.",
  genre: "Romance, Fantasy",
  duration: 106,
  releaseDate: "2016-08-26",
  director: "Makoto Shinkai",
  actors: "Ryunosuke Kamiki, Mone Kamishiraishi",
  thumbnail: imgMovie,
  note: "Phim đạt nhiều giải thưởng quốc tế, là hiện tượng phòng vé Nhật Bản năm 2016.",
};

const LOCATIONS = [
  "SIX Cinema Hà Nội",
  "SIX Cinema Đà Nẵng",
  "SIX Cinema Hồ Chí Minh",
];

const TIME_SLOTS = [
  "09:00",
  "11:30",
  "14:00",
  "16:30",
  "19:00",
  "21:30",
];

function getNext7Days() {
  const days = [];
  const today = new Date();
  for (let i = 0; i < 7; i++) {
    const d = new Date(today);
    d.setDate(today.getDate() + i);
    days.push({
      value: d.toISOString().split("T")[0],
      label: d.toLocaleDateString("vi-VN", { weekday: "short", day: "2-digit", month: "2-digit" }),
    });
  }
  return days;
}

// Hàm lọc khung giờ hợp lệ theo ngày chọn
function getAvailableTimeSlots(selectedDate) {
  const now = new Date();
  const todayStr = now.toISOString().split("T")[0];
  if (selectedDate !== todayStr) return TIME_SLOTS;

  // Nếu là hôm nay, chỉ lấy các khung giờ lớn hơn giờ hiện tại
  const currentMinutes = now.getHours() * 60 + now.getMinutes();
  return TIME_SLOTS.filter((slot) => {
    const [h, m] = slot.split(":").map(Number);
    const slotMinutes = h * 60 + m;
    return slotMinutes > currentMinutes;
  });
}

// Hàm trả về mảng khung giờ với trạng thái disabled cho hôm nay
function getTimeSlotsWithDisabled(selectedDate) {
  const now = new Date();
  const todayStr = now.toISOString().split("T")[0];
  if (selectedDate !== todayStr) {
    // Tất cả khung giờ đều chọn được
    return TIME_SLOTS.map(time => ({ value: time, label: time, disabled: false }));
  }
  // Nếu là hôm nay, các khung giờ đã qua sẽ bị disabled
  const currentMinutes = now.getHours() * 60 + now.getMinutes();
  return TIME_SLOTS.map(time => {
    const [h, m] = time.split(":").map(Number);
    const slotMinutes = h * 60 + m;
    return {
      value: time,
      label: time,
      disabled: slotMinutes <= currentMinutes,
    };
  });
}

const MovieDetail = () => {
  const { slug } = useParams();
  const navigate = useNavigate();
  const [movie, setMovie] = useState(null);
  const [mainImage, setMainImage] = useState(imgMovie);
  const [isLoading, setIsLoading] = useState(true);

  // State cho form đặt vé
  const [selectedLocation, setSelectedLocation] = useState("");
  const [selectedTime, setSelectedTime] = useState("");
  const [selectedDate, setSelectedDate] = useState(getNext7Days()[0].value);

  useEffect(() => {
    const fetchMovieDetail = async () => {
      try {
        const response = await getMovieDetail(slug);
        if (!response.error && response.result) {
          setMovie(response.result);
          if (response.result.thumbnail) setMainImage(response.result.thumbnail);
        } else {
          setMovie(mockData);
          setMainImage(mockData.thumbnail);
        }
      } catch (error) {
        setMovie(mockData);
        setMainImage(mockData.thumbnail);
      } finally {
        setIsLoading(false);
      }
    };
    fetchMovieDetail();
  }, [slug]);

  const handleBook = (e) => {
    e.preventDefault();
    if (!selectedLocation || !selectedTime || !selectedDate) {
      toast.warning("Vui lòng chọn đầy đủ địa điểm, ngày và giờ chiếu!");
      return;
    }
    // Đúng route: phải có slug trên URL
    navigate(`/book-movie-ticket/${slug}`, {
      state: {
        movie,
        location: selectedLocation,
        time: selectedTime,
        date: selectedDate,
      },
    });
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-96">
        <FaSpinner className="animate-spin text-4xl text-blue-500" />
      </div>
    );
  }

  if (!movie) {
    return <div>Không tìm thấy phim</div>;
  }

  return (
    <div className="max-w-6xl mx-auto p-8 grid grid-cols-1 md:grid-cols-2 gap-12 bg-white rounded-lg shadow-sm">
      {/* Movie Poster */}
      <div className="relative space-y-6">
        <div className="w-full h-[500px] rounded-lg overflow-hidden bg-gray-50 flex items-center justify-center">
          {mainImage ? (
            <img
              src={mainImage}
              alt={movie.title}
              className="w-full h-full object-contain hover:scale-105 transition-transform duration-300"
            />
          ) : (
            <span className="text-gray-500">Không có ảnh</span>
          )}
        </div>
        {/* Movie Description */}
        <div className="mt-8">
          <div className="bg-white rounded-lg overflow-hidden transition-all duration-300 hover:shadow-lg">
            <div className="w-full flex justify-between items-center text-left font-medium p-4 bg-gray-50">
              <span className="text-gray-800">Nội dung phim</span>
            </div>
            <div className="p-4">
              <p className="text-gray-600 leading-relaxed">
                {movie.description}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Movie Info Section */}
      <div className="space-y-6">
        <div className="space-y-4">
          <h1 className="text-4xl font-bold text-gray-800">{movie.title}</h1>
          <div className="flex items-center space-x-3">
            <span className="text-blue-600 text-lg font-semibold">
              Thể loại: {movie.genre || "Đang cập nhật"}
            </span>
            <span className="text-gray-500 text-base">
              | Thời lượng: {movie.duration || "Đang cập nhật"} phút
            </span>
          </div>
        </div>

        <div className="space-y-2">
          <div className="flex items-center space-x-2">
            <span className="text-gray-600 font-medium">Đạo diễn:</span>
            <span className="text-gray-800">{movie.director || "Đang cập nhật"}</span>
          </div>
          <div className="flex items-center space-x-2">
            <span className="text-gray-600 font-medium">Diễn viên:</span>
            <span className="text-gray-800">{movie.actors || "Đang cập nhật"}</span>
          </div>
          <div className="flex items-center space-x-2">
            <span className="text-gray-600 font-medium">Khởi chiếu:</span>
            <span className="text-gray-800">{movie.releaseDate || "Đang cập nhật"}</span>
          </div>
        </div>

        {/* Booking Form Section */}
        <form className="pt-6 space-y-4" onSubmit={handleBook}>
          <div className="flex flex-col md:flex-row md:items-center md:space-x-4 space-y-3 md:space-y-0">
            {/* Địa điểm */}
            <div className="flex-1">
              <label className="block text-gray-700 font-medium mb-1">Địa điểm</label>
              <select
                className="w-full border rounded-md px-3 py-2"
                value={selectedLocation}
                onChange={e => setSelectedLocation(e.target.value)}
                required
              >
                <option value="">Chọn địa điểm</option>
                {LOCATIONS.map(loc => (
                  <option key={loc} value={loc}>{loc}</option>
                ))}
              </select>
            </div>
            {/* Ngày */}
            <div className="flex-1">
              <label className="block text-gray-700 font-medium mb-1">Ngày chiếu</label>
              <select
                className="w-full border rounded-md px-3 py-2"
                value={selectedDate}
                onChange={e => setSelectedDate(e.target.value)}
                required
              >
                {getNext7Days().map(day => (
                  <option key={day.value} value={day.value}>{day.label}</option>
                ))}
              </select>
            </div>
            {/* Giờ */}
            <div className="flex-1">
              <label className="block text-gray-700 font-medium mb-1">Khung giờ</label>
              <select
                className="w-full border rounded-md px-3 py-2"
                value={selectedTime}
                onChange={e => setSelectedTime(e.target.value)}
                required
              >
                <option value="">Chọn giờ</option>
                {getTimeSlotsWithDisabled(selectedDate).every(slot => slot.disabled) ? (
                  <option disabled>Không còn khung giờ hợp lệ</option>
                ) : (
                  getTimeSlotsWithDisabled(selectedDate).map(slot => (
                    <option
                      key={slot.value}
                      value={slot.value}
                      disabled={slot.disabled}
                      style={slot.disabled ? { color: "#aaa" } : {}}
                    >
                      {slot.label}
                    </option>
                  ))
                )}
              </select>
            </div>
          </div>
          <div className="flex items-center space-x-4 mt-4">
            <button
              type="submit"
              className="bg-purple-700 hover:bg-purple-800 text-white px-8 py-2 rounded-md flex-1 transition-colors duration-300"
            >
              ĐẶT VÉ
            </button>
            <button
              type="button"
              className="p-2 border rounded-md hover:border-red-500 hover:text-red-500 transition-all duration-300"
              onClick={() => toast.info("Đã thêm vào yêu thích")}
            >
              <Heart size={22} />
            </button>
          </div>
        </form>

        {/* Thông tin thêm */}
        <div className="pt-6 space-y-3">
          <h3 className="text-xl font-semibold text-gray-800">
            Thông tin thêm
          </h3>
          <p className="text-gray-600 leading-relaxed">
            {movie.note || "Vui lòng liên hệ rạp để biết thêm chi tiết về suất chiếu, giá vé và các ưu đãi."}
          </p>
        </div>
      </div>
    </div>
  );
};

export default MovieDetail;
