import { useLocation, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { toast } from "react-toastify";
import BookSeat from "./BookSeat";
import BookFood from "./BookFood";

// MOCK DATA
const mockMovie = {
  title: "THE STONE: BUỒN THẦN BÁN THÁNH",
  description:
    "Khi Ake tuyệt vọng cần tiền để chi trả cho việc điều trị bệnh nặng của người cha...",
  director: "Pae Arak Amornsupasiri, Vuthiphong Sukhantirad",
  actors: "Joonay Jinjett Wattanasin, Kornnadi Laosubinprasoret",
  genre: "Action",
  releaseDate: "23/05/2025",
  duration: 126,
  poster: "https://i.imgur.com/3QyQZ4A.png",
};

const mockShowtime = {
  cinema: "BHD Star Pham Hung",
  screen: "Screen 3",
  date: "22/5/2025",
  time: "22h50",
  type: ["T10", "Phụ đề", "2D"],
};

const mockFoods = [
  {
    id: 1,
    name: "OL Combo Single Sweet 32Oz - Pepsi 22Oz",
    img: "https://i.imgur.com/2nCt3Sbl.png",
    price: 69300,
    oldPrice: 77000,
  },
  {
    id: 2,
    name: "OL Combo Couple Sweet 32Oz - Pepsi 22Oz",
    img: "https://i.imgur.com/2nCt3Sbl.png",
    price: 98100,
    oldPrice: 109000,
  },
  {
    id: 3,
    name: "OL Food CB Ga Vong Sweet 32Oz - Pepsi 22Oz",
    img: "https://i.imgur.com/2nCt3Sbl.png",
    price: 102600,
    oldPrice: 114000,
  },
  {
    id: 4,
    name: "OL Food CB KTC Sweet 32Oz - Pepsi 22Oz",
    img: "https://i.imgur.com/2nCt3Sbl.png",
    price: 102600,
    oldPrice: 114000,
  },
  {
    id: 5,
    name: "OL Food CB Xuc Xich Sweet 32Oz - Pepsi 22Oz",
    img: "https://i.imgur.com/2nCt3Sbl.png",
    price: 102600,
    oldPrice: 114000,
  },
];

// Layout ghế
const seatRows = [
  { row: "A", type: "standard", count: 10 },
  { row: "B", type: "standard", count: 10 },
  { row: "C", type: "standard", count: 10 },
  { row: "D", type: "standard", count: 10 },
  { row: "E", type: "standard", count: 10 },
  { row: "F", type: "standard", count: 10 },
  { row: "G", type: "vip", count: 10 },
  { row: "H", type: "vip", count: 10 },
  { row: "I", type: "vip", count: 10 },
  { row: "J", type: "vip", count: 10 },
  { row: "K", type: "couple", count: 10 }, // Ghế đôi cũng chỉ 10 cột
];

const mockSeats = seatRows
  .map(({ row, type, count }) =>
    Array(count)
      .fill(0)
      .map((_, idx) => ({
        row,
        col: idx + 1,
        type,
        status: "available",
        seatCode: `${row}${idx + 1}`,
      }))
  )
  .flat();

const BookMovieTicket = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const movie = location.state?.movie || mockMovie;
  const showtime = location.state
    ? {
        cinema: location.state.location,
        date: location.state.date,
        time: location.state.time,
        screen: location.state.screen || mockShowtime.screen,
        type: location.state.type || mockShowtime.type,
      }
    : mockShowtime;

  const [selectedSeats, setSelectedSeats] = useState([]);
  const [timer, setTimer] = useState(360); // 6 phút
  const [foodCounts, setFoodCounts] = useState(
    mockFoods.reduce((acc, food) => ({ ...acc, [food.id]: 0 }), {})
  );

  useEffect(() => {
    const interval = setInterval(() => {
      setTimer((t) => (t > 0 ? t - 1 : 0));
    }, 1000);
    return () => clearInterval(interval);
  }, []);

  const formatTimer = (t) => {
    const m = Math.floor(t / 60);
    const s = t % 60;
    return `${m} phút, ${s < 10 ? "0" : ""}${s} giây`;
  };

  const handleSelectSeat = (seat) => {
    if (seat.status === "sold") return;
    const isSelected = selectedSeats.some((s) => s.seatCode === seat.seatCode);
    if (isSelected) {
      setSelectedSeats(selectedSeats.filter((s) => s.seatCode !== seat.seatCode));
    } else {
      setSelectedSeats([...selectedSeats, seat]);
    }
  };

  const handleFoodChange = (id, delta) => {
    setFoodCounts((prev) => ({
      ...prev,
      [id]: Math.max(0, prev[id] + delta),
    }));
  };

  const handleNext = () => {
    if (selectedSeats.length === 0) {
      toast.warning("Vui lòng chọn ít nhất 1 ghế!");
      return;
    }
    toast.success("Chuyển sang thanh toán");
  };

  const handleBack = () => navigate(-1);

  const seatPrice = 50000;
  const foodTotal = mockFoods.reduce(
    (sum, food) => sum + foodCounts[food.id] * food.price,
    0
  );
  const total = selectedSeats.length * seatPrice + foodTotal;
  const totalFoodCount = Object.values(foodCounts).reduce((sum, count) => sum + count, 0);

  // Phân loại ghế đã chọn
  const seatTypeCount = selectedSeats.reduce(
    (acc, seat) => {
      if (seat.type === "vip") acc.vip += 1;
      else if (seat.type === "couple") acc.couple += 1;
      else acc.standard += 1;
      return acc;
    },
    { standard: 0, vip: 0, couple: 0 }
  );

  return (
    <div className="max-w-6xl mx-auto p-2 md:p-6 grid grid-cols-1 md:grid-cols-3 gap-6 bg-white min-h-screen">
      {/* Chọn ghế + InfoBooking */}
      <BookSeat
        movie={movie}
        seatRows={seatRows}
        mockSeats={mockSeats}
        selectedSeats={selectedSeats}
        handleSelectSeat={handleSelectSeat}
        handleBack={handleBack}
      />
      {/* Đồ ăn + Thanh toán */}
      <div className="flex flex-col gap-4">
        <BookFood
          mockFoods={mockFoods}
          foodCounts={foodCounts}
          handleFoodChange={handleFoodChange}
        />
        {/* Thanh toán */}
        <div className="bg-white rounded-lg p-4 shadow">
          <div className="font-semibold text-gray-700 mb-2">{showtime.cinema}</div>
          <div className="text-sm text-gray-500 mb-2">
            {showtime.screen ? showtime.screen + " - " : ""}
            {showtime.date} • Suất chiếu: {showtime.time}
          </div>
          <div className="border-b mb-2"></div>
          <div className="font-bold text-purple-700 uppercase mb-1">{movie.title}</div>
          <div className="flex gap-2 mb-2 flex-wrap">
            {(showtime.type || []).map((t) => (
              <span
                key={t}
                className="bg-gray-100 text-purple-700 border border-purple-400 rounded px-2 py-0.5 text-xs font-semibold"
              >
                {t}
              </span>
            ))}
          </div>
          {/* Hiển thị loại ghế đã chọn */}
          <div className="text-sm mb-2">
            {selectedSeats.length === 0 && "Chưa chọn ghế"}
            {seatTypeCount.standard > 0 && (
              <span>
                {seatTypeCount.standard} x Ghế Thường
              </span>
            )}
            {seatTypeCount.vip > 0 && (
              <span className="ml-2">
                {seatTypeCount.vip} x Ghế VIP
              </span>
            )}
            {seatTypeCount.couple > 0 && (
              <span className="ml-2">
                {Math.ceil(seatTypeCount.couple / 2)} x Ghế Couple
              </span>
            )}
            {selectedSeats.length > 0 && (
              <span className="ml-2 text-gray-500">
                {selectedSeats.map((s) => s.seatCode).join(", ")}
              </span>
            )}
          </div>
          {/* Hiển thị loại nước/đồ ăn đã chọn */}
          {Object.entries(foodCounts)
            .filter(([id, count]) => count > 0)
            .map(([id, count]) => {
              const food = mockFoods.find((f) => f.id === Number(id));
              return (
                <div key={id} className="text-xs text-gray-600">
                  {count} x {food?.name}
                </div>
              );
            })}

          
          <div className="font-semibold mb-2">
            Tổng tiền:{" "}
            <span className="text-purple-700 text-lg">
              {total.toLocaleString()} VND
            </span>
          </div>
          <button
            className="w-full bg-purple-500 hover:bg-purple-600 text-white font-semibold py-2 rounded mt-2 transition"
            onClick={handleNext}
            disabled={selectedSeats.length === 0}
          >
            THANH TOÁN ({selectedSeats.length + totalFoodCount})
          </button>
          <button
            className="w-full mt-2 text-purple-700 hover:underline text-sm"
            onClick={handleBack}
            type="button"
          >
            ← Trở lại
          </button>
          <div className="text-xs text-gray-400 mt-2">
            Còn lại {formatTimer(timer)}
          </div>
        </div>
      </div>
    </div>
  );
};

export default BookMovieTicket;
