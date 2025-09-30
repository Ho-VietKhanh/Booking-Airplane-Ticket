import React from "react";

const InfoBooking = ({ movie, handleBack }) => (
  <div className="flex flex-col md:flex-row gap-4 bg-white rounded-lg p-4 shadow mb-4">
    <img
      src={movie.poster}
      alt={movie.title}
      className="w-36 h-52 object-cover rounded-lg border bg-gray-100"
    />
    <div className="flex-1">
      <h2 className="text-xl font-bold text-purple-700 uppercase mb-1">{movie.title}</h2>
      <p className="text-gray-700 mb-2">{movie.description}</p>
      <div className="mb-1">
        <span className="font-semibold">Đạo diễn:</span> {movie.director}
      </div>
      <div className="mb-1">
        <span className="font-semibold">Diễn viên:</span> {movie.actors}
      </div>
      <div className="mb-1">
        <span className="font-semibold">Thể loại:</span> {movie.genre}
      </div>
      <div className="mb-1">
        <span className="font-semibold">Khởi chiếu:</span> {movie.releaseDate}
        {" | "}
        <span className="font-semibold">Thời lượng:</span> {movie.duration} phút
      </div>
      <button
        className="mt-2 text-purple-700 hover:underline text-sm"
        onClick={handleBack}
        type="button"
      >
        ← CHỌN PHIM KHÁC
      </button>
    </div>
  </div>
);

export default InfoBooking;