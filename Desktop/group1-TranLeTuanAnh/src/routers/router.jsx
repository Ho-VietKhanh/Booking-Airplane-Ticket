import { createBrowserRouter } from "react-router-dom";
import RootLayout from "../layout/RootLayout";
import { ProtectedAdminRoute } from "./ProtectedAdminRoute";

import LandingPage from "../page/Customer/LandingPage/LandingPage";
import LoginModal from "../page/Customer/LoginPage/LoginPage";
import AdminPage from "../page/Admin/AdminPage";
import LoginAdmin from "../page/Admin/LoginAdmin/LoginAdmin";
import MovieDetail from "../page/Customer/MoviePage/MovieDetail";
import BookMovieTicket from "../page/Customer/BookMovieTickets/BookMovieTicket";
import ProfilePage from "../page/Customer/Profile/ProfilePage";


export const router = createBrowserRouter([
  {
    path: "/",
    element: <RootLayout />,
    children: [
      {
        path: "",
        element: <LandingPage />,
      },
    
      {
        path: "/login",
        element: <LoginModal />,
      },
      {
        path: "/movies/:slug",
        element: <MovieDetail />,
      },
      {
        path: "/book-movie-ticket/:slug",
        element: <BookMovieTicket />,
      },
      {
        path: "/profile",
        element: <ProfilePage />,
      },
    ],
  },
  {
    path: "/admin/login",
    element: <LoginAdmin />,
  },
  {
    path: "/admin",
    children: [
      {
        path: "login",
        element: <LoginAdmin />,
      },
      {
        path: "",
        element: (
          <ProtectedAdminRoute>
            <AdminPage />
          </ProtectedAdminRoute>
        ),
        children: [
        //   {
        //     path: "",
        //     element: <Dashboard />,
        //   },
        //   {
        //     path: "user",
        //     element: <UserManagement />,
        //   },
        //   {
        //     path: "order",
        //     element: <OrderManagement />,
        //   },
        //   {
        //     path: "product",
        //     element: <ProductManagement />,
        //   },
         
          
        ],
      },
    ],
  },
]);
