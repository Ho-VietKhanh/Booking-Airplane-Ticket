import React from "react";
import InfoBooking from "./InfoBooking";

const BookSeat = ({
  movie,
  seatRows,
  mockSeats,
  selectedSeats,
  handleSelectSeat,
  handleBack,
}) => {
  // Hàm renderSeats được chuyển sang đây
  const renderSeats = () => (
    <div className="flex flex-col items-center">
      <div className="mb-2 text-center text-purple-500 font-semibold text-lg">
        Màn hình
      </div>
      <div className="w-full flex justify-center mb-2">
        <div className="w-3/4 h-2 rounded-b-full bg-purple-200 mb-4"></div>
      </div>
      <div className="flex flex-row gap-2 mb-2">
        <span className="flex items-center mr-4">
          <span className="w-5 h-5 rounded bg-gray-200 border mr-1"></span>{" "}
          Standard
        </span>
        <span className="flex items-center mr-4">
          <span className="w-5 h-5 rounded bg-yellow-200 border mr-1"></span> Vip
        </span>
        <span className="flex items-center mr-4">
          <span className="w-5 h-5 rounded bg-cyan-300 border mr-1"></span>{" "}
          Couple
        </span>
        <span className="flex items-center mr-4">
          <span className="w-5 h-5 rounded bg-purple-400 border mr-1"></span>{" "}
          Ghế đã chọn
        </span>
        <span className="flex items-center mr-4">
          <span className="w-5 h-5 rounded bg-red-400 border mr-1"></span> Ghế đã
          bán
        </span>
      </div>
      <div className="inline-block bg-white rounded-lg p-4 shadow">
        <table>
          <tbody>
            {seatRows.map(({ row, type, count }) => (
              <tr key={row}>
                <td className="pr-2 font-semibold text-gray-500">{row}</td>
                {Array(count)
                  .fill(0)
                  .map((_, idx) => {
                    const seat = mockSeats.find(
                      (s) => s.row === row && s.col === idx + 1
                    );
                    const isSelected = selectedSeats.some(
                      (s) => s.seatCode === seat.seatCode
                    );
                    let color =
                      type === "standard"
                        ? "bg-gray-200"
                        : type === "vip"
                        ? "bg-yellow-200"
                        : "bg-cyan-300";
                    if (seat.status === "sold") color = "bg-red-400";
                    if (isSelected) color = "bg-purple-400";

                    // Nếu là ghế đôi (couple), render button rộng gấp đôi và bỏ 1 cột tiếp theo
                    if (type === "couple") {
                      // Chỉ render ghế lẻ (1,3,5...) để mỗi button chiếm 2 cột
                      if ((idx + 1) % 2 === 1) {
                        const seat2 = mockSeats.find(
                          (s) => s.row === row && s.col === idx + 2
                        );
                        const isSelected2 = seat2
                          ? selectedSeats.some((s) => s.seatCode === seat2.seatCode)
                          : false;
                        let color2 = color;
                        if (seat2 && seat2.status === "sold") color2 = "bg-red-400";
                        if (isSelected2) color2 = "bg-purple-400";
                        // Nếu cả 2 ghế đều sold thì disable
                        const isSold =
                          seat.status === "sold" && seat2 && seat2.status === "sold";
                        return (
                          <td key={idx} colSpan={2} className="p-1" style={{ padding: 0 }}>
                            <button
                              type="button"
                              className={`w-16 h-8 rounded flex items-center justify-center text-xs font-bold border
                                ${color} ${isSelected ? "ring-2 ring-purple-400" : ""}
                                ${color2} ${isSelected2 ? "ring-2 ring-purple-400" : ""}
                                ${isSold ? "cursor-not-allowed opacity-50" : "hover:ring-2 hover:ring-purple-400"}
                              `}
                              disabled={seat.status === "sold" && seat2 && seat2.status === "sold"}
                              onClick={() => {
                                handleSelectSeat(seat);
                                if (seat2) handleSelectSeat(seat2);
                              }}
                              style={{
                                marginRight: 4,
                                background:
                                  isSelected || isSelected2
                                    ? "#c084fc"
                                    : seat.status === "sold" && seat2 && seat2.status === "sold"
                                    ? "#f87171"
                                    : "#22d3ee",
                              }}
                            >
                              {/* Không hiện mã ghế */}
                            </button>
                          </td>
                        );
                      } else {
                        // Bỏ qua cột chẵn vì đã gộp vào ghế đôi phía trước
                        return null;
                      }
                    }

                    // Ghế thường/vip
                    return (
                      <td key={idx} className="p-1">
                        <button
                          type="button"
                          className={`w-8 h-8 rounded ${color} border flex items-center justify-center text-xs font-bold ${
                            seat.status === "sold"
                              ? "cursor-not-allowed opacity-50"
                              : "hover:ring-2 hover:ring-purple-400"
                          }`}
                          disabled={seat.status === "sold"}
                          onClick={() => handleSelectSeat(seat)}
                        >
                          {/* {seat.seatCode} */}
                        </button>
                      </td>
                    );
                  })}
                <td className="pl-2 font-semibold text-gray-500">{row}</td>
              </tr>
            ))}
            <tr>
              <td></td>
              {Array(seatRows[0].count)
                .fill(0)
                .map((_, idx) => (
                  <td key={idx} className="text-xs text-gray-400 text-center pt-1">
                    {idx + 1}
                  </td>
                ))}
              <td></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );

  return (
    <div className="md:col-span-2 flex flex-col">
      <InfoBooking movie={movie} handleBack={handleBack} />
      <div className="flex-1 flex flex-col justify-center">{renderSeats()}</div>
    </div>
  );
};

export default BookSeat;