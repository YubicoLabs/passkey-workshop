//
//  SignInFailedViewController.swift
//  PawsKey
//
//  Created by Dennis Hills on 3/17/23.
//  Copyright © 2023 Yubico. All rights reserved.
//

import Foundation
import UIKit

class SignInFailedViewController: UIViewController {
    
    @IBAction func signIn(_ sender: Any) {
        self.view.window?.rootViewController = UIStoryboard(name: "Main", bundle: nil)
            .instantiateViewController(withIdentifier: "SignInViewController")
    }
}
