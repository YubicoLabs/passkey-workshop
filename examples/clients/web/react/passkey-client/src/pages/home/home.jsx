import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import OIDCServices from "../../services/OIDCServices";

export default function Home() {
  const navigation = useNavigate();

  useEffect(() => {
    const effectStillAuthenticated = async () => {
      const signedIn = await OIDCServices.stillAuthenticated();
      if (!signedIn) {
        navigation("/sign_in");
      }
    };
    effectStillAuthenticated();
  }, []);

  return <div>in home</div>;
}
