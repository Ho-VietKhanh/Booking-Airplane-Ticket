import { motion } from "framer-motion";
import { Carousel } from "antd";

const HeroSection = () => {
  const fadeIn = {
    hidden: { opacity: 0, y: 50 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.8 } },
  };

  const heroImages = [
    "https://baokhanhhoa.vn/file/e7837c02857c8ca30185a8c39b582c03/042025/b_20250401144223.webp",
    "https://image.anninhthudo.vn/w1000/Uploaded/2025/ipjoohb/2023_09_27/00e237e7-6e51-475a-863b-439f46467a1f-2821-7326.jpeg",
    "https://media.vietnamplus.vn/images/7255a701687d11cb8c6bbc58a6c8078592edd9ac94029ca62f11ee250135dabadaf25b373d440f487a1a0ba46378c987/mat_biec.jpg",
  ];

  return (
    <Carousel autoplay className="w-full">
      {heroImages.map((src, index) => (
        <motion.div
          key={index}
          className="flex items-center justify-center bg-gray-900 py-4"
          variants={fadeIn}
          initial="hidden"
          animate="visible"
        >
          <img
            src={src}
            alt={`hero-${index}`}
            className="w-full max-w-screen-lg h-[400px] object-cover mx-auto rounded-lg shadow-lg"
          />
        </motion.div>
      ))}
    </Carousel>
  );
};

export default HeroSection;
