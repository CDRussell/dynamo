import UIKit
import SharedCode

typealias PasswordSuccess = PasswordGenerator.PasswordResultPasswordSuccess
typealias PasswordFailure = PasswordGenerator.PasswordResultPasswordFailure

class ViewController: UIViewController {
        
    @IBAction func generatePasswordButtonPressed(_ sender: Any) {
        generatePassword()
    }
    
    @IBOutlet weak var passwordField: UILabel!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let tap = UITapGestureRecognizer(target: self, action: #selector(copyPassword(sender:)))
        passwordField.addGestureRecognizer(tap)
        
        generatePassword()
    }
    
    func generatePassword() {
       let config = PasswordGenerator.PasswordConfiguration(requiredLength:12,
                                                            numericalType: PasswordGenerator.NumericalType(included: true),
                                                            upperCaseLetterType: PasswordGenerator.UpperCaseLetterType(included: true ),
                                                            lowerCaseLetterType: PasswordGenerator.LowerCaseLetterType(included: true),
                                                            specialCharacterLetterType: PasswordGenerator.SpecialCharacterLetterType(included: true))
       
       let result = PasswordGenerator().generatePassword(passwordConfiguration: config)
       
       if let result = result as? PasswordSuccess {
           passwordField.text = result.password
       } else if let result = result as? PasswordFailure {
           passwordField.text = result.exception.message
       }
    }
    
    @objc func copyPassword(sender: UITapGestureRecognizer) {
        UIPasteboard.general.string = passwordField.text
        if let output = UIPasteboard.general.string {
            print("Copied \(output)")
        }
    }
}
