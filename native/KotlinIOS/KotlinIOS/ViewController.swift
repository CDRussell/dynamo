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
        
//        let label = UILabel(frame: CGRect(x: 0, y: 0, width: 300, height: 21))
//        label.center = CGPoint(x: 160, y: 285)
//        label.textAlignment = .center
//        label.font = label.font.withSize(25)
//        label.text = CommonKt.createApplicationScreenMessage()
//        view.addSubview(label)
        
       generatePassword()
    }
    
    func generatePassword() {
       let config = PasswordGenerator.PasswordConfiguration(requiredLength:12,
                                                            numericalType: PasswordGenerator.NumericalType(included: true, minimumCharacters: 0),
                                                            upperCaseLetterType: PasswordGenerator.UpperCaseLetterType(included: true, minimumCharacters: 0),
                                                            lowerCaseLetterType: PasswordGenerator.LowerCaseLetterType(included: true, minimumCharacters: 0),
                                                            specialCharacterLetterType: PasswordGenerator.SpecialCharacterLetterType(included: true, minimumCharacters: 0))
       
       let result = PasswordGenerator().generatePassword(passwordConfiguration: config)
       
       if let result = result as? PasswordSuccess {
           passwordField.text = result.password
       } else if let result = result as? PasswordFailure {
           passwordField.text = result.exception.message
       }
    }
}
