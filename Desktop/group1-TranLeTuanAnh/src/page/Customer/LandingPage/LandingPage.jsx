import { Link } from "react-router-dom";
import { FaArrowRight } from "react-icons/fa";
import { motion } from "framer-motion";
import { movies } from "./MovieList";
import ProductCard from "./MovieCard";
import HeroSection from "../../../components/HeroSection/HeroSection";
import heroImg from "../../../assets/img/hero-landingPage.png";
import { useEffect } from "react";

const LandingPage = () => {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  const fadeIn = {
    hidden: { opacity: 0, y: 50 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.8 } },
  };

  const slideIn = {
    hidden: { x: "-100%" },
    visible: { x: 0, transition: { duration: 1 } },
  };

  return (
    <div className="flex flex-col bg-[#281f5cef] text-white font-sans">
      {/* Hero Section */}
      <HeroSection />

      {/* Quick Booking Section */}
<div className="bg-slate-300 text-gray-900 rounded-md shadow-md px-6 py-4 mx-auto mt-4 max-w-7xl flex flex-wrap justify-center items-center gap-4">
  <h2 className="text-xl font-bold mr-4">ĐẶT VÉ NHANH</h2>
  <select className="border border-gray-300 rounded-md px-4 py-2 font-bold text-purple-700">
    <option>1. Chọn Rạp</option>
  </select>
  <select className="border border-gray-300 rounded-md px-4 py-2 font-bold text-purple-700">
    <option>2. Chọn Phim</option>
  </select>
  <select className="border border-gray-300 rounded-md px-4 py-2 font-bold text-purple-700">
    <option>3. Chọn Ngày</option>
  </select>
  <select className="border border-gray-300 rounded-md px-4 py-2 font-bold text-purple-700">
    <option>4. Chọn Suất</option>
  </select>
  <button className="bg-purple-700 hover:bg-purple-800 text-white font-bold py-2 px-6 rounded-md">
    ĐẶT NGAY
  </button>
</div>
      {/* Movie Highlight */}
      <motion.div
        className="flex items-center justify-center p-10"
        variants={fadeIn}
        initial="hidden"
        whileInView="visible"
        viewport={{ once: true }}
      >
        <div className="grid grid-cols-1 md:grid-cols-2 gap-10 max-w-8xl">
          <div className="flex items-center justify-center">
            <motion.img
              src="https://image.dienthoaivui.com.vn/x,webp,q90/https://dashboard.dienthoaivui.com.vn/uploads/dashboard/editor_upload/poster-phim-hoat-hinh-13.jpg?q=w_1600,h_902,x_0,y_0,c_fill"
              alt="movie"
              className="rounded-lg shadow-lg hover:shadow-xl transition-all duration-300"
              whileHover={{ scale: 1.05 }}
            />
          </div>

          <motion.div
            className="flex flex-col justify-center"
            variants={fadeIn}
            initial="hidden"
            animate="visible"
          >
            <p className="text-sm text-yellow-500 uppercase tracking-wide">Action | Drama</p>
            <h2 className="text-4xl font-bold text-white mt-2 leading-snug">
              Grave of the Fireflies
            </h2>
            <p className="text-gray-300 mt-4">
              Japan during the war was engulfed in flames, with bombs raining down everywhere. The country fell into crisis — food was scarce, and people's lives were filled with hardship. Through the narration of Seita's ghost, we are taken back to the past — to where the tragedy began.
            </p>
            <Link
              to="/movies"
              className="inline-flex items-center text-yellow-500 hover:opacity-75 transition-opacity"
            >
              <motion.div
                className="border border-yellow-500 px-6 py-3 flex items-center justify-between min-w-[180px] mt-4 hover:bg-yellow-500 hover:text-black transition"
                whileHover={{ scale: 1.1 }}
              >
                <span className="">Book Now</span>
                <FaArrowRight className="ml-3" />
              </motion.div>
            </Link>
          </motion.div>
        </div>
      </motion.div>

      {/* Now Showing */}
      <motion.div
        className="max-w-7xl mx-auto p-8"
        variants={fadeIn}
        initial="hidden"
        animate="visible"
      >
        <h1 className="text-3xl font-bold mb-4 text-white">Now Showing</h1>
        <p className="text-gray-400 mb-6">
          Check out the hottest movies in theaters right now.
        </p>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
          {movies.map((product, index) => (
            <motion.div
              key={index}
              variants={fadeIn}
              initial="hidden"
              whileInView="visible"
              viewport={{ once: true }}
            >
              <ProductCard {...product} />
            </motion.div>
          ))}
        </div>
        <motion.div className="mt-8" whileHover={{ x: 10 }}>
          <a
            href="/all-movies"
            className="text-sm font-medium underline text-yellow-500 hover:text-white"
          >
            All Movies →
          </a>
        </motion.div>
      </motion.div>

      {/* Coming soon */}
      <motion.div
        className="max-w-7xl mx-auto p-8"
        variants={fadeIn}
        initial="hidden"
        animate="visible"
      >
        <h1 className="text-3xl font-bold mb-4 text-white">Coming soon</h1>
        <p className="text-gray-400 mb-6">
          Check out the hottest movies in theaters right now.
        </p>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
          {movies.map((product, index) => (
            <motion.div
              key={index}
              variants={fadeIn}
              initial="hidden"
              whileInView="visible"
              viewport={{ once: true }}
            >
              <ProductCard {...product} />
            </motion.div>
          ))}
        </div>
        <motion.div className="mt-8" whileHover={{ x: 10 }}>
          <a
            href="/all-movies"
            className="text-sm font-medium underline text-yellow-500 hover:text-white"
          >
            All Movies →
          </a>
        </motion.div>
      </motion.div>

      {/* Promotion */}
      <motion.div
        className="relative min-h-[420px] max-h-[480px] w-full"
        variants={fadeIn}
        initial="hidden"
        whileInView="visible"
        viewport={{ once: true }}
      >
        <div
          className="absolute inset-0 bg-cover bg-center"
          style={{ backgroundImage: `url(${heroImg})` }}
        ></div>
        <div className="absolute inset-0 bg-black bg-opacity-60"></div>
        <div className="relative flex flex-col items-start pt-12 max-w-4xl ml-12 mt-4 px-6 text-white">
          <p className="text-sm uppercase tracking-wide mb-4 text-yellow-400">Promotion</p>
          <h1 className="text-4xl font-bold mb-6 text-white">
            Explore Upcoming Blockbusters
          </h1>
          <p className="text-lg mb-8 leading-relaxed text-gray-300">
            Don't miss our most anticipated upcoming releases — from action-packed adventures to heartwarming dramas.
          </p>
          <motion.button
            className="bg-transparent border border-yellow-500 text-yellow-500 py-2 px-6 rounded-md hover:bg-yellow-500 hover:text-black transition"
            whileHover={{ scale: 1.1 }}
          >
            Discover More
          </motion.button>
        </div>
      </motion.div>
    </div>
  );
};

export default LandingPage;