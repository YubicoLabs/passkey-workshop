import { Outlet } from "react-router-dom";
import AuthServices from "../services/AuthServices";
import { useEffect } from "react";

export const ProtectedRoutes = ({ children }) => {
  useEffect(() => {
    const checkAuthenticated = async () => {
      const isAuth = await AuthServices.stillAuthenticated();
      if (!isAuth) {
        window.location = AuthServices.AUTH_URL;
      }
    };

    checkAuthenticated();
  }, []);

  return <Outlet />;
};
