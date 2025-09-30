import { FaFacebook, FaInstagram, FaTiktok, FaYoutube } from "react-icons/fa";

const Footer = () => {
  return (
    <footer className="bg-gray-900 text-gray-300 py-10 mt-auto">
      <div className="max-w-6xl mx-auto px-4 grid grid-cols-2 md:grid-cols-5 gap-8 text-sm">
        {/* Logo and Social Links */}
        <div className="col-span-2 md:col-span-1">
          <h2 className="text-2xl font-bold text-white"> SIX Cinema</h2>
          <p className="mt-4">FOLLOW US</p>
          <div className="flex space-x-4 mt-2">
            <a href="#" className="text-gray-300 hover:text-blue-500 transition-colors">
              <i className="fab fa-twitter"></i>
            </a>
            <a href="#" className="text-gray-300 hover:text-pink-500 transition-colors">
              <i className="fab fa-instagram"></i>
            </a>
            <a href="#" className="text-gray-300 hover:text-blue-700 transition-colors">
              <i className="fab fa-facebook"></i>
            </a>
          </div>
        </div>

        {/* Learn English */}
        <div>
          <h3 className="font-semibold text-white mb-3">Infomation</h3>
          <ul className="space-y-2">
            <li>
              <a href="#" className="hover:text-blue-400 transition-colors">
                Cinema System
              </a>
            </li>
            <li>
              <a href="#" className="hover:text-blue-400 transition-colors">
                Cinema Complex
              </a>
            </li>
          </ul>
        </div>

        {/* Guides */}
        <div>
          <h3 className="font-semibold text-white mb-3">Terms & Conditions</h3>
          <ul className="space-y-2">
            <li>
              <a href="#" className="hover:text-blue-400 transition-colors">
                Membership Regulations
              </a>
            </li>
            <li>
              <a href="#" className="hover:text-blue-400 transition-colors">
                Terms and Conditions
              </a>
            </li>
            <li>
              <a href="#" className="hover:text-blue-400 transition-colors">
                General Rules and Policies
              </a>
            </li>
          </ul>
        </div>

        {/* Services */}
        <div>
          <h3 className="font-semibold text-white mb-3">Social network</h3>
          <div className="flex flex-col space-y-6">
            <div className="flex space-x-8 text-white text-2xl">
              <a href="https://www.facebook.com/tran.le.tuan.anh.830328?locale=vi_VN" aria-label="Facebook" className="hover:text-blue-500 transition-colors" target="_blank" rel="noopener noreferrer">
                <FaFacebook />
              </a>
              <a href="https://www.instagram.com/" aria-label="Instagram" className="hover:text-pink-500 transition-colors" target="_blank" rel="noopener noreferrer">
                <FaInstagram />
              </a>
            </div>
            <div className="flex space-x-8 text-white text-2xl">
              <a href="https://www.tiktok.com/vi-VN//" aria-label="TikTok" className="hover:text-black transition-colors" target="_blank" rel="noopener noreferrer">
                <FaTiktok />
              </a>
              <a href="https://www.youtube.com/c/t%C3%B4i%C4%91icoded%E1%BA%A1oblog" aria-label="YouTube" className="hover:text-red-600 transition-colors" target="_blank" rel="noopener noreferrer">
                <FaYoutube />
              </a>
            </div>
          </div>
        </div>

        {/* Contact */}
        <div>
          <h3 className="font-semibold text-white mb-3">Contact</h3>
          <ul className="space-y-2">
            <li>
              <a href="#" className="hover:text-blue-400 transition-colors">
                 Opening hours: 9:00 AM – 10:00 PM 
              </a>
            </li>
            <li>
              <a href="#" className="hover:text-blue-400 transition-colors">
               Support email: cskh@sixcinema.vn
              </a>
            </li>
            <li>
              <a href="#" className="hover:text-blue-400 transition-colors">
                Hotline: 8386 6969 6969
              </a>
            </li>
          </ul>
        </div>
      </div>

      <div className="border-t border-gray-700 mt-8 pt-4 text-xs text-center text-gray-500">
        <p  >Công ty TNHH MTV SIX Cinema Việt Nam </p>
        <p>© 2025 SIX Cinema | All rights reserved</p>
      </div>
    </footer>
  );
};

export default Footer;
