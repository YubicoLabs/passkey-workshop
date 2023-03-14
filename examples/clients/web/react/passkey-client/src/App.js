import './App.css';
import { BrowserRouter, Route, Routes} from "react-router-dom";
import Container from "react-bootstrap/Container";

import Header from './components/header';

import Home from './pages/home/home';
import SignIn from './pages/sign_in/sign_in';
import SignUp from './pages/sign_up/sign_up';
import TestPanel from './pages/test_panel/test_panel';
import Callback from './pages/callback/callback';
import Footer from './components/footer';

function App() {
  return (
    <div className="d-flex flex-column min-vh-100">
      <BrowserRouter>
        <Header />
        <Container className='content'>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="sign_in" element={<SignIn />} />
            <Route path="sign_up" element={<SignUp />} />
            <Route path="test_panel" element={<TestPanel />} />
            <Route path="/oidc/callback" element={<Callback callbackInfo={{message: "Loading your profile"}}  />} />
            <Route path="/logout" element={<Callback callbackInfo={{message: "Thank you for visiting"}}  />} />
          </Routes>
        </Container>
        <Footer />
      </BrowserRouter>
    </div>

  );
}

export default App;
