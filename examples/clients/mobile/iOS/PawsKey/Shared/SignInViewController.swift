/*
Abstract:
The view where the user can sign in, or create an account.
*/

import AuthenticationServices
import UIKit
import os

class SignInViewController: UIViewController {
    @IBOutlet weak var userNameLabel: UILabel!
    @IBOutlet weak var userNameField: UITextField!

    private var signInObserver: NSObjectProtocol?
    private var failedSignInObserver: NSObjectProtocol?
    private var signInErrorObserver: NSObjectProtocol?
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        signInObserver = NotificationCenter.default.addObserver(forName: .UserSignedIn, object: nil, queue: nil) {_ in
            self.didFinishSignIn()
        }
        
        failedSignInObserver = NotificationCenter.default.addObserver(forName: .UserFailedSignIn, object: nil, queue: nil) {_ in
            self.didFailSignIn()
        }

        signInErrorObserver = NotificationCenter.default.addObserver(forName: .ModalSignInSheetCanceled, object: nil, queue: nil) { _ in
            self.didFailSignIn()
        }
        userNameLabel.isHidden = false
        userNameField.isHidden = false
        
//        guard let window = self.view.window else { fatalError("The view was not in the app's view hierarchy!") }
//        (UIApplication.shared.delegate as? AppDelegate)?.accountManager.signInWith(username: self.username, anchor: window, preferImmediatelyAvailableCredentials: false)
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        if let signInObserver = signInObserver {
            NotificationCenter.default.removeObserver(signInObserver)
        }
        
        if let failedSignInObserver = failedSignInObserver {
            NotificationCenter.default.removeObserver(failedSignInObserver)
        }

        if let signInErrorObserver = signInErrorObserver {
            NotificationCenter.default.removeObserver(signInErrorObserver)
        }
        
        super.viewDidDisappear(animated)
    }
    
    @IBAction func registerPlatformPasskey(_ sender: Any) {
        guard let userName = userNameField.text else {
            return
        }
        
        if(!userName.isEmpty) {
            guard let window = self.view.window else { fatalError("The view was not in the app's view hierarchy!") }
            (UIApplication.shared.delegate as? AppDelegate)?.accountManager.signUpWithPlatform(userName: userName, anchor: window)
        } else {
            displayAlert(title: "Got Username?", "No user name provided")
        }
        
    }
    
    @IBAction func registerSecurityKey(_ sender: Any) {
        guard let userName = userNameField.text else {
            Logger().log("No userName provided")
            return
        }
        
        if(!userName.isEmpty) {
            guard let window = self.view.window else { fatalError("The view not in view hierarchy!") }
            (UIApplication.shared.delegate as? AppDelegate)?.accountManager.signUpWithSecurityKey(userName: userName, anchor: window)
        } else {
            displayAlert(title: "Got Username?", "No user name provided")
        }
    }
    
    @IBAction func signIn(_ sender: Any) {
        guard let userName = userNameField.text else { return }
        guard let window = self.view.window else { fatalError("The view was not in the app's view hierarchy!") }
        (UIApplication.shared.delegate as? AppDelegate)?.accountManager.signInWithPasskey(userName: userName, anchor: window)
//        (UIApplication.shared.delegate as? AppDelegate)?.accountManager.signInWithSecurityKey(userName: userName, anchor: window)
//        (UIApplication.shared.delegate as? AppDelegate)?.accountManager.signInWithPlatform(userName: userName, anchor: window)
    }
    
    func displayAlert(title: String, _ msg: String) {
        let dialogMessage = UIAlertController(title: title, message: msg, preferredStyle: .alert)
        
        // Create OK button with action handler
        let ok = UIAlertAction(title: "OK", style: .default, handler: { (action) -> Void in })
        
        dialogMessage.addAction(ok)
        
        // Present alert message to user
        self.present(dialogMessage, animated: true, completion: nil)
        
    }
    
    @IBAction func tappedBackground(_ sender: Any) {
        self.view.endEditing(true)
    }

    func didFinishSignIn() {
        self.view.window?.rootViewController = UIStoryboard(name: "Main", bundle: nil)
            .instantiateViewController(withIdentifier: "UserHomeViewController")
    }
    
    func didFailSignIn() {
        self.view.window?.rootViewController = UIStoryboard(name: "Main", bundle: nil)
            .instantiateViewController(withIdentifier: "SignInFailedViewController")
    }
}

