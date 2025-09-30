/* eslint-disable react/prop-types */
import  { useState } from "react";
import { register } from "../../../service/register/index"; // Import API đăng ký
import NotificationModal from "../../../components/Nofication/NotificationModal";

const RegisterForm = ({ onBackToLogin }) => {
  const [registerData, setRegisterData] = useState({
    username: "",
    password: "",
    confirmPassword: "",
    dateOfBirth: "",
    gender: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [passwordError, setPasswordError] = useState(""); // Lỗi khi xác nhận mật khẩu không trùng
  const [modalInfo, setModalInfo] = useState({
    isOpen: false,
    message: "",
    type: "success"
  });

  const isFormValid =
    registerData.username &&
    registerData.password &&
    registerData.confirmPassword &&
    registerData.dateOfBirth &&
    registerData.gender &&
    registerData.password === registerData.confirmPassword;

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess(false);

    if (!isFormValid) return;

    setLoading(true);

    try {
      const requestData = {
        username: registerData.username,
        password: registerData.password,
        birthday: registerData.dateOfBirth,
        gender: registerData.gender.toUpperCase(),
      };

      const response = await register(requestData);

      if (response?.code === 201) {
        setModalInfo({
          isOpen: true,
          message: "Tạo tài khoản thành công! Vui lòng đăng nhập.",
          type: "success"
        });
        setRegisterData({
          username: "",
          password: "",
          confirmPassword: "",
          dateOfBirth: "",
          gender: "",
        });
        setTimeout(() => {
          onBackToLogin();
        }, 2000);
      } else {
        setModalInfo({
          isOpen: true,
          message: response.message || "Đăng ký thất bại!",
          type: "error"
        });
      }
    } catch (err) {
      setModalInfo({
        isOpen: true,
        message: "Đăng ký thất bại. Vui lòng thử lại!",
        type: "error"
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <NotificationModal
        isOpen={modalInfo.isOpen}
        message={modalInfo.message}
        type={modalInfo.type}
        onClose={() => {
          setModalInfo({ ...modalInfo, isOpen: false });
          if (modalInfo.type === "success") {
            onBackToLogin();
          }
        }}
      />
      <h2 className="text-2xl font-semibold text-gray-900">
        Create your account
      </h2>
      {error && <p className="text-red-500 text-sm">{error}</p>}
      {success && <p className="text-green-500 text-sm">{success}</p>}

      <form className="space-y-6" onSubmit={handleRegister}>
        <div>
          <input
            type="text"
            placeholder="Full Name"
            className="w-full p-3 border-b border-gray-300 bg-transparent text-gray-800 focus:outline-none focus:border-gray-900 placeholder-gray-500"
            required
            value={registerData.username}
            onChange={(e) =>
              setRegisterData({ ...registerData, username: e.target.value })
            }
          />
        </div>

        <div className="flex gap-4">
          <div className="flex-1">
            <label className="block text-sm text-gray-600 mb-2">
              Date of Birth
            </label>
            <input
              type="date"
              className="w-full p-3 border-b border-gray-300 bg-transparent text-gray-800 focus:outline-none focus:border-gray-900"
              required
              value={registerData.dateOfBirth}
              onChange={(e) =>
                setRegisterData({
                  ...registerData,
                  dateOfBirth: e.target.value,
                })
              }
            />
          </div>
          <div className="flex-1">
            <label className="block text-sm text-gray-600 mb-2">Gender</label>
            <select
              className="w-full p-3 border-b border-gray-300 bg-transparent text-gray-800 focus:outline-none focus:border-gray-900"
              required
              value={registerData.gender}
              onChange={(e) =>
                setRegisterData({ ...registerData, gender: e.target.value })
              }
            >
              <option value="">Select Gender</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
              <option value="OTHER">Other</option>
            </select>
          </div>
        </div>
        <div>
          <input
            type="password"
            placeholder="Create Password"
            className="w-full p-3 border-b border-gray-300 bg-transparent text-gray-800 focus:outline-none focus:border-gray-900 placeholder-gray-500"
            required
            value={registerData.password}
            onChange={(e) =>
              setRegisterData({ ...registerData, password: e.target.value })
            }
          />
        </div>
        <div>
          <input
            type="password"
            placeholder="Confirm Password"
            className="w-full p-3 border-b border-gray-300 bg-transparent text-gray-800 focus:outline-none focus:border-gray-900 placeholder-gray-500"
            required
            value={registerData.confirmPassword}
            onChange={(e) => {
              const value = e.target.value;
              setRegisterData({
                ...registerData,
                confirmPassword: value,
              });

              if (value !== registerData.password) {
                setPasswordError("Passwords do not match!");
              } else {
                setPasswordError("");
              }
            }}
          />
          {passwordError && (
            <p className="text-red-500 text-sm mt-1">{passwordError}</p>
          )}
        </div>

        <button
          type="submit"
          disabled={!isFormValid || loading}
          className={`w-full px-4 py-3 text-sm font-semibold rounded-md transition ${
            isFormValid && !loading
              ? "bg-black text-white hover:bg-gray-800"
              : "bg-gray-300 text-gray-500 cursor-not-allowed"
          }`}
        >
          {loading ? "Registering..." : "Create Account"}
        </button>
      </form>

      <div className="mt-6 text-center">
        <p className="text-sm text-gray-600 mb-4">Already have an account?</p>
        <button
          onClick={onBackToLogin}
          className="w-full flex items-center justify-center px-4 py-3 text-sm font-semibold border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 transition"
        >
          Back to login
          <span className="ml-2">→</span>
        </button>
      </div>
    </div>
  );
};

export default RegisterForm;
