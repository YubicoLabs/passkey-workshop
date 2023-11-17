import { Outlet } from "react-router-dom";
import AuthServices from "../services/AuthServices";
import { useEffect } from "react";

export const ProtectedRoutes = ({ children }) => {
  useEffect(() => {
    const checkAuthenticated = async () => {
      const isAuth = await AuthServices.stillAuthenticated();
      console.log("Current auth status: " + isAuth);
      if (!isAuth) {
        localStorage.removeItem("APP_ACCESS_TOKENS");
        localStorage.removeItem("USER_INFO");
        window.location = AuthServices.AUTH_URL;
      }
    };

    checkAuthenticated();
  }, []);

  return <Outlet />;
};
