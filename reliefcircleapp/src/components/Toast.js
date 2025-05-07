import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

// Custom toast styles
const toastStyles = {
  position: "top-right",
  autoClose: 3000,
  hideProgressBar: false,
  closeOnClick: true,
  pauseOnHover: true,
  draggable: true,
  progress: undefined,
  theme: "light",
};

// Custom toast functions
export const showToast = {
  success: (message) => toast.success(message, toastStyles),
  error: (message) => toast.error(message, toastStyles),
  info: (message) => toast.info(message, toastStyles),
  warning: (message) => toast.warning(message, toastStyles),
};

// Toast container component
export const Toast = () => {
  return (
    <ToastContainer
      position="top-right"
      autoClose={3000}
      hideProgressBar={false}
      newestOnTop
      closeOnClick
      rtl={false}
      pauseOnFocusLoss
      draggable
      pauseOnHover
      theme="light"
    />
  );
};

export default Toast; 