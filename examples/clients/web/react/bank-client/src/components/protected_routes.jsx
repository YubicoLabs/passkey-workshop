import { Outlet } from "react-router-dom";
import AuthServices from "../services/AuthServices";
import { useEffect, useState } from "react";

export const ProtectedRoutes = ({ children }) => {
  const [loading, setLoading] = useState(true);

  const checkAuthenticated = async () => {
    console.log("This method should be called");
    const isAuth = await AuthServices.stillAuthenticated();
    console.log("Value of isAuth: " + isAuth);
    if (!isAuth) {
      console.log("No auth");
      localStorage.removeItem("APP_ACCESS_TOKENS");
      localStorage.removeItem("USER_INFO");
      window.location = AuthServices.AUTH_URL;
    } else {
      setLoading(false);
    }
  };

  checkAuthenticated();

  return loading ? <div></div> : <Outlet />;
};
