/* eslint-disable react/prop-types */


const ForgotPasswordForm = ({ onBackToLogin }) => {
    const handleSubmit = (e) => {
      e.preventDefault();
      // Xử lý logic quên mật khẩu
    };
  
    return (
      <div className="space-y-6">
        <h2 className="text-2xl font-semibold text-gray-900">Reset your password</h2>
        <p className="text-sm text-gray-600">
          Enter your email address and we'll send you a link to reset your password.
        </p>
        
        <form className="space-y-6" onSubmit={handleSubmit}>
          <div>
            <input
              type="email"
              placeholder="Email Address"
              className="w-full p-3 border-b border-gray-300 bg-transparent text-gray-800 focus:outline-none focus:border-gray-900 placeholder-gray-500"
              required
            />
          </div>
  
          <button
            type="submit"
            className="w-full px-4 py-3 bg-black text-white text-sm font-semibold rounded-md hover:bg-gray-800 transition"
          >
            Send Reset Link
          </button>
        </form>
  
        <div className="mt-6 text-center">
          <p className="text-sm text-gray-600 mb-4">Remember your password?</p>
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
  
  export default ForgotPasswordForm; 