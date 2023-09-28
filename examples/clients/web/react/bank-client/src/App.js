import logo from './logo.svg';
import { BrowserRouter, Route, Routes} from "react-router-dom";
import Container from "react-bootstrap/Container";

import Header from './components/header';

import Home from './pages/home/home';
import Account from './pages/account/account';

import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';
import Transactions from './pages/transactions/transactions';

function App() {
  return (
    <div>
      <BrowserRouter>
        <Header />
        <Container>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/account" element={<Account />} />
            <Route path="/transactions/*" element={<Transactions />} />
          </Routes>
        </Container>
      </BrowserRouter>
    </div>
  );
}

export default App;
