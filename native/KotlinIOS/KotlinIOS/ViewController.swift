import UIKit
import SharedCode

typealias PasswordSuccess = PasswordGenerator.PasswordResultPasswordSuccess
typealias PasswordFailure = PasswordGenerator.PasswordResultPasswordFailure

class ViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let label = UILabel(frame: CGRect(x: 0, y: 0, width: 300, height: 21))
        label.center = CGPoint(x: 160, y: 285)
        label.textAlignment = .center
        label.font = label.font.withSize(25)
        label.text = CommonKt.createApplicationScreenMessage()
        
        let config = PasswordGenerator.PasswordConfiguration(requiredLength:12,
                                                             numericalType: PasswordGenerator.NumericalType(included: true, minimumCharacters: 0),
                                                             upperCaseLetterType: PasswordGenerator.UpperCaseLetterType(included: true, minimumCharacters: 0),
                                                             lowerCaseLetterType: PasswordGenerator.LowerCaseLetterType(included: true, minimumCharacters: 0),
                                                             specialCharacterLetterType: PasswordGenerator.SpecialCharacterLetterType(included: true, minimumCharacters: 0))
        
        let result = PasswordGenerator().generatePassword(passwordConfiguration: config)
        
        if let result = result as? PasswordSuccess {
            label.text = result.password
        } else if let result = result as? PasswordFailure {
            label.text = result.exception.message
        }
        
        view.addSubview(label)
    }
}
