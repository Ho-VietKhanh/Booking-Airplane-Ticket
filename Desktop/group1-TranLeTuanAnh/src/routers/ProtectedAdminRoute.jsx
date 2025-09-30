/* eslint-disable react/prop-types */
import { Navigate } from "react-router-dom";

export const ProtectedAdminRoute = ({ children }) => {
  const adminUser = JSON.parse(localStorage.getItem("adminUser"));

  if (!adminUser) {
    return <Navigate to="/admin/login" replace />;
  }

  if (adminUser.username !== "admin" && adminUser.username !== "staff") {
    return <Navigate to="/admin/login" replace />;
  }

  return children;
};
