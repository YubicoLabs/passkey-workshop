import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom";
import Container from "react-bootstrap/Container";

import Header from "./components/header";

import Home from "./pages/home/home";
import Account from "./pages/account/account";

import "bootstrap/dist/css/bootstrap.min.css";
import "./App.css";
import Transactions from "./pages/transactions/transactions";
import AuthCallback from "./pages/callbacks/auth";
import { ProtectedRoutes } from "./components/protected_routes";

function App() {
  return (
    <div>
      <BrowserRouter>
        <Header />
        <Container>
          <Routes>
            <Route path="/" element={<ProtectedRoutes />}>
              <Route path="/" element={<Home />} />
              <Route path="/dashboard" element={<Home />} />
              <Route path="/account" element={<Account />} />
              <Route path="/transactions/*" element={<Transactions />} />
            </Route>
            <Route path="/callback/auth" element={<AuthCallback />} />
            <Route path="/error" element={<h1>ERROR</h1>} />
            <Route path='*' element={<Navigate to='/' />} />
          </Routes>
        </Container>
      </BrowserRouter>
    </div>
  );
}

export default App;
