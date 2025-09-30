const NotificationModal = ({ isOpen, message, type, onClose }) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center z-50">
      <div className="fixed inset-0 bg-black opacity-50" onClick={onClose}></div>
      <div className="bg-white rounded-lg p-6 relative z-10 max-w-md w-full mx-4">
        <div className={`text-center ${type === 'success' ? 'text-green-600' : 'text-red-600'}`}>
          <div className="text-2xl mb-2">
            {type === 'success' ? '✓' : '✕'}
          </div>
          <p className="text-lg font-semibold">
            {message}
          </p>
          <button
            onClick={onClose}
            className="mt-4 bg-gray-800 text-white px-4 py-2 rounded hover:bg-gray-700 transition-colors"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
};

export default NotificationModal; 