import logo from './logo.svg';
import './App.css';
import { BrowserRouter, Route, Link, Routes} from "react-router-dom";
import Container from "react-bootstrap/Container";

import Header from './components/header';

import Home from './pages/home/home';
import SignIn from './pages/sign_in/sign_in';
import SignUp from './pages/sign_up/sign_up';
import TestPanel from './pages/test_panel/test_panel';
import HomeOIDC from './pages/OIDC_test/home';
import Callback from './pages/OIDC_test/callback';
import SignInCallback from './pages/sign_in/sign_in_callback';

function App() {
  return (
    <div>
      <BrowserRouter>
        <Header />
        <Container>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="sign_in" element={<SignIn />} />
            <Route path="sign_up" element={<SignUp />} />
            <Route path="test_panel" element={<TestPanel />} />
            <Route path="/oidc/callback" element={<SignInCallback />} />
            <Route path="/oidc/home" element={<HomeOIDC />} />
          </Routes>
        </Container>
      </BrowserRouter>
    </div>

  );
}

export default App;
