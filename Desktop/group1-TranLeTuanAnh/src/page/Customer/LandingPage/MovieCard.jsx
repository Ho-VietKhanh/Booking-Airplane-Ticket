import { useNavigate } from "react-router-dom";
/* eslint-disable react/prop-types */

const MovieCard = ({ tag, title, description, genre, duration, imgSrc, slug }) => {
  const navigate = useNavigate();

  const handleDetailClick = () => {
    navigate(`/movies/${slug}`);
  };

  return (
    <div
      className="group cursor-pointer relative flex flex-col items-center text-center bg-gradient-to-br from-gray-50 to-gray-200 p-5 hover:shadow-2xl transition duration-300 rounded-xl min-h-[480px] border border-gray-100 hover:border-blue-400"
      onClick={handleDetailClick}
      style={{ boxShadow: "0 4px 24px 0 rgba(0,0,0,0.06)" }}
    >
      {/* Tag */}
      {tag && (
        <span className="absolute top-3 left-3 text-xs font-bold bg-blue-100 text-blue-600 px-3 py-1 rounded-full shadow-sm uppercase tracking-wider">
          {tag}
        </span>
      )}

      {/* Image Section */}
      <div className="h-52 w-full flex items-center justify-center rounded-lg overflow-hidden bg-white shadow group-hover:scale-105 transition-transform duration-300">
        <img
          src={imgSrc}
          alt={title}
          className="max-h-full max-w-full object-cover transition-transform duration-300 group-hover:scale-105"
        />
      </div>

      {/* Text Section */}
      <div className="flex flex-col items-center mt-5 space-y-2 flex-1 w-full">
        {/* Title */}
        <div className="h-12 flex items-center justify-center">
          <h3 className="font-bold text-xl text-gray-800 line-clamp-1 group-hover:text-blue-600 transition-colors">{title}</h3>
        </div>

        {/* Description */}
        <div className="h-14 flex items-center justify-center">
          <p className="text-sm text-gray-500 line-clamp-2">{description}</p>
        </div>

        {/* Genre & Duration */}
        <div className="flex items-center justify-center gap-3 mt-2">
          <span className="inline-block bg-blue-50 text-blue-500 text-xs font-medium px-3 py-1 rounded-full">
            {genre}
          </span>
          <span className="inline-block bg-gray-100 text-gray-700 text-xs font-medium px-3 py-1 rounded-full">
            {duration} phút
          </span>
        </div>
      </div>

      {/* Button Section */}
      <div className="w-full mt-4">
        <button
          className="w-full bg-gradient-to-r from-blue-600 to-blue-400 hover:from-blue-700 hover:to-blue-500 text-white py-2 px-6 rounded-md text-base font-semibold shadow transition duration-300 opacity-0 group-hover:opacity-100"
          onClick={(e) => {
            e.stopPropagation();
            handleDetailClick();
          }}
        >
          Xem chi tiết
        </button>
      </div>
    </div>
  );
};

export default MovieCard;
