import { useState, useRef, useEffect } from "react";
import { FiSearch } from "react-icons/fi";
import { FaHeart } from "react-icons/fa";
import { FaArrowRightLong } from "react-icons/fa6";
import { Link, useNavigate } from "react-router-dom";
import LoginModal from "../../page/Customer/LoginPage/LoginPage";
import ShopDropdown from "./ShopDropdown";
import { FiUser } from "react-icons/fi";
import { logout } from "../../service/logout";
import { clearExpiredToken } from "../../service/login/index";
// import { useCart } from '../../context/CartContext';
import logo from "../../assets/img/logo.png";

const Header = () => {
  const [showSearch, setShowSearch] = useState(false);
  const [searchValue, setSearchValue] = useState("");
  const searchRef = useRef(null);
  const [isScrolled, setIsScrolled] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showShopMenu, setShowShopMenu] = useState(false);
  const shopMenuRef = useRef(null);
  const [isShopDropdownOpen, setIsShopDropdownOpen] = useState(false);
  const shopButtonRef = useRef(null);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [user, setUser] = useState(null);
  const menuRef = useRef(null);
  const [error, setError] = useState(null);
  const [showChangePasswordModal, setShowChangePasswordModal] = useState(false);
  const navigate = useNavigate();

  // Kiểm tra token hết hạn và lấy user
  useEffect(() => {
    clearExpiredToken("user");
    const storedUser = localStorage.getItem("username");
    if (storedUser) setUser(storedUser);
  }, []);

  // Đóng search khi click ngoài
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (searchRef.current && !searchRef.current.contains(event.target)) {
        if (!searchValue.trim()) setShowSearch(false);
      }
    };
    if (showSearch) document.addEventListener("click", handleClickOutside);
    return () => document.removeEventListener("click", handleClickOutside);
  }, [showSearch, searchValue]);

  // Đổi trạng thái header khi scroll
  useEffect(() => {
    const handleScroll = () => setIsScrolled(window.scrollY > 0);
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  // Đóng shop menu khi click ngoài
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (shopMenuRef.current && !shopMenuRef.current.contains(event.target)) {
        setShowShopMenu(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // Đóng user menu khi click ngoài
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setShowUserMenu(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleShopClick = (e) => {
    e.stopPropagation();
    setIsShopDropdownOpen(!isShopDropdownOpen);
  };

  const handleLogout = async (e) => {
    e.preventDefault();

    const token = localStorage.getItem("token");
    try {
      const response = await logout(token);
      console.log("API Response:", response);
      if (response?.code === 200) {
        localStorage.removeItem("token");
        localStorage.removeItem("username");
        window.location.reload();
      } else {
        setError(response.message);
      }
    } catch (err) {
      console.error("Logout failed:", err);
      setError("Logout failed");
    }
    setUser(null);
    setShowUserMenu(false);
  };

  const handleProfileClick = () => {
    setShowUserMenu(false);
  };

  return (
    <>
      {isScrolled && <div className="h-16" />}
      <header
        className={`w-full bg-gray-900 ${
          isScrolled ? "fixed top-0 left-0 right-0 shadow-md z-50" : "relative"
        } transition-shadow duration-200`}
      >
        <div className="container mx-auto">
          <div className="flex items-center justify-between h-16 px-4 lg:px-8">
            {/* Left Section: Navigation Links */}
            <nav className="flex-1 hidden md:flex text-sm font-medium text-gray-300">
              {/* Shop Button with Dropdown */}
              <div className="relative group" ref={shopMenuRef}>
                <button
                  ref={shopButtonRef}
                  onClick={handleShopClick}
                  className="relative px-6 py-5 h-16 hover:text-blue-400 hover:bg-gray-800 transition-colors duration-200 text-gray-300 group"
                >
                  Menu
                  <span className="absolute bottom-0 left-0 w-full h-0.5 bg-blue-400 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-200" />
                </button>
                <ShopDropdown
                  isOpen={isShopDropdownOpen}
                  onClose={() => setIsShopDropdownOpen(false)}
                  shopButtonRef={shopButtonRef}
                />
              </div>

              <div className="relative group">
                <Link
                  to="/about-us"
                  className="relative px-6 py-5 hover:text-blue-400 h-16 hover:bg-gray-800 transition-colors duration-200 block"
                >
                  About Us
                  <span className="absolute bottom-0 left-0 w-full h-0.5 bg-blue-400 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-200" />
                </Link>
              </div>

              <div className="relative group">
                <Link
                  to="/blog"
                  className="relative px-6 py-5 hover:text-blue-400 h-16 hover:bg-gray-800 transition-colors duration-200 block"
                >
                  Movie Forum
                  <span className="absolute bottom-0 left-0 w-full h-0.5 bg-blue-400 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-200" />
                </Link>
              </div>

              <div className="relative group">
                <Link
                  to="/helps"
                  className="relative px-6 py-5 hover:text-blue-400 h-16 hover:bg-gray-800 transition-colors duration-200 block"
                >
                  Helps
                  <span className="absolute bottom-0 left-0 w-full h-0.5 bg-blue-400 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-200" />
                </Link>
              </div>
            </nav>

            {/* Center Section: Logo */}
            <div className="flex-1 flex items-center justify-center">
              <Link
                to="/"
                className="text-3xl font-bold text-white cursor-pointer"
                style={{ lineHeight: "2.5rem" }}
              >
                <img
                src={logo}
                alt="SIX Cinema Logo"
                className="h-14 w-14 mr-4 object-contain"
                style={{ borderRadius: "9px" }}
              />
              </Link>
              
              <Link
                to="/"
                className="text-3xl font-bold text-white cursor-pointer"
                style={{ lineHeight: "2.5rem" }}
              >
                SIX Cinema
              </Link>
            </div>

            {/* Right Section: Icons */}
            <div className="flex-1 flex items-center justify-end">
              {/* Search Icon */}
              <div className="relative group" ref={searchRef}>
                {showSearch ? (
                  <div className="flex items-center w-full border-b border-gray-700">
                    <input
                      type="text"
                      className="w-full text-sm border-none focus:ring-0 outline-none placeholder-gray-500 bg-gray-800 text-gray-300"
                      placeholder="Search..."
                      value={searchValue}
                      onChange={(e) => setSearchValue(e.target.value)}
                    />
                    <button
                      className="ml-2 text-gray-300 hover:text-blue-400 transition-colors"
                      onClick={handleSearch}
                    >
                      <FaArrowRightLong size={16} />
                    </button>
                  </div>
                ) : (
                  <div className="relative h-16 px-4 flex items-center hover:bg-gray-800 transition-colors duration-200">
                    <FiSearch
                      size={20}
                      className="cursor-pointer text-gray-300 hover:text-blue-400 transition-colors"
                      onClick={(e) => {
                        e.stopPropagation();
                        setShowSearch(true);
                      }}
                    />
                    <span className="absolute bottom-0 left-0 w-full h-0.5 bg-blue-400 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-200" />
                  </div>
                )}
              </div>

              {/* Wishlist Link */}
              <div className="relative group">
                <Link
                  to="/wishlist"
                  className="relative h-16 px-4 flex items-center hover:text-blue-400 hover:bg-gray-800 transition-colors duration-200 text-gray-300"
                >
                  <FaHeart
                    size={20}
                    className="cursor-pointer text-gray-300 hover:text-blue-400"
                  />
                  <span className="absolute bottom-0 left-0 w-full h-0.5 bg-blue-400 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-200" />
                </Link>
              </div>

              {/* Login/User Button */}
              <div className="relative group" ref={menuRef}>
                {user ? (
                  <div className="relative">
                    <button
                      className="relative h-16 px-4 flex items-center hover:text-blue-400 hover:bg-gray-800 transition-colors duration-200 text-gray-300"
                      onClick={() => setShowUserMenu(!showUserMenu)}
                    >
                      <FiUser
                        size={20}
                        className="text-gray-300 hover:text-blue-400"
                      />
                    </button>

                    {showUserMenu && (
                      <div className="absolute right-0 mt-2 w-48 bg-gray-800 rounded-lg shadow-lg py-2 z-50">
                        <Link
                          to="/profile"
                          className="block px-4 py-2 text-sm text-gray-300 hover:bg-gray-700 hover:text-blue-400"
                          onClick={handleProfileClick}
                        >
                          Profile
                        </Link>
                        <button
                          onClick={handleLogout}
                          className="block w-full text-left px-4 py-2 text-sm text-gray-300 hover:bg-gray-700 hover:text-blue-400"
                        >
                          Logout
                        </button>
                      </div>
                    )}
                  </div>
                ) : (
                  <button
                    onClick={() => setShowLoginModal(true)}
                    className="relative h-16 px-4 flex items-center hover:text-blue-400 hover:bg-gray-800 transition-colors duration-200 text-sm font-medium text-gray-300"
                  >
                    Login
                  </button>
                )}
              </div>
            </div>
          </div>

          {/* Mobile Navigation */}
          <div className="flex items-center justify-between md:hidden py-2 px-4">
            <button className="text-sm font-medium hover:text-black">
              Menu
            </button>
            <Link to="/shop" className="text-sm font-medium hover:text-black">
              Shop
            </Link>
          </div>
        </div>
      </header>

      {/* Login Modal */}
      <LoginModal
        isOpen={showLoginModal}
        onClose={() => setShowLoginModal(false)}
      />
    </>
  );
};

export default Header;
