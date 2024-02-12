import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom";
import Container from "react-bootstrap/Container";
import { useEffect } from "react";

import "vanilla-cookieconsent/dist/cookieconsent.css";
import * as CookieConsent from "vanilla-cookieconsent";

import Header from "./components/header";

import Home from "./pages/home/home";
import Account from "./pages/account/account";

import "bootstrap/dist/css/bootstrap.min.css";
import "./App.css";
import Transactions from "./pages/transactions/transactions";
import AuthCallback from "./pages/callbacks/auth";
import { ProtectedRoutes } from "./components/protected_routes";


function App() {

  /**
  * All config. options available here:
  * https://cookieconsent.orestbida.com/reference/configuration-reference.html
  */
  useEffect(() => {
    CookieConsent.run({
      categories: {
        necessary: {
          enabled: true,  // this category is enabled by default
          readOnly: true  // this category cannot be disabled
        },
        analytics: {}
      },

      language: {
        default: 'en',
        translations: {
          en: {
            consentModal: {
              title: 'We use cookies',
              description: 'Ensure that you get the best experience on our site. By browsing this site without restricting the use of cookies, you consent to our and third party use of cookies as set out in our Cookie Notice.',
              acceptAllBtn: 'Accept all',
              acceptNecessaryBtn: 'Reject all',
              showPreferencesBtn: 'Manage Individual preferences'
            },
            preferencesModal: {
              title: 'Manage cookie preferences',
              acceptAllBtn: 'Accept all',
              acceptNecessaryBtn: 'Reject all',
              savePreferencesBtn: 'Accept current selection',
              closeIconLabel: 'Close modal',
              sections: [
                {
                  title: 'Privacy Overview',
                  description: 'This site uses cookies to improve your experience while navigating through the website. The information does not usually identify you, but it can give you a more personalized web experience. Blocking some types of cookies may impact your experience on our site and the services we are able to offer.'
                },
                {
                  title: 'Strictly Necessary cookies',
                  description: 'These cookies are essential for the proper functioning of the website and cannot be disabled.',

                  //this field will generate a toggle linked to the 'necessary' category
                  linkedCategory: 'necessary'
                },
                {
                  title: 'Performance and Analytics',
                  description: 'These cookies collect information about how you use our website and provide feedback.',
                  linkedCategory: 'analytics'
                },
                {
                  title: 'More information',
                  description: 'For more details relative to cookies and other sensitive data, please read the <a href="https://www.yubico.com/support/terms-conditions/privacy-notice/">privacy policy</a>. For any queries in relation to our policy on cookies and your choices, please <a href="https://support.yubico.com">contact us</a>.'
                }
              ]
            }
          }
        }
      }
    });
  }, []);

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
