package com.jetbrains.handson.mpp.mobile

import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.cdrussell.dynamo.PasswordGenerator.*
import com.cdrussell.dynamo.PasswordGenerator.PasswordResult.PasswordFailure
import com.cdrussell.dynamo.PasswordGenerator.PasswordResult.PasswordSuccess

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var generatedPasswordField: TextView
    private lateinit var passwordLengthPicker: NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureViewReferences()
        configureViewStateObserver()
    }

    private fun configureViewStateObserver() {
        viewModel.viewState.observe(this, Observer {
            when (it.result) {
                is PasswordSuccess -> {
                    generatedPasswordField.text = it.result.password
                }
                is PasswordFailure -> {
                    generatedPasswordField.text = it.result.exception.message
                }
            }
        })
    }

    private fun configureViewReferences() {
        generatedPasswordField = findViewById(R.id.generatedPasswordText)
        findViewById<Button>(R.id.generatePasswordButton).setOnClickListener { generateNewPassword() }

        findViewById<NumberPicker>(R.id.lengthPicker).also {
            it.minValue = 0
            it.maxValue = 12
            it.wrapSelectorWheel = false
            passwordLengthPicker = it
        }

    }

    private fun generateNewPassword() {
        val passwordConfiguration = PasswordConfiguration(
            requiredLength = passwordLengthPicker.value,
            numericalType = NumericalType(included = true),
            upperCaseLetterType = UpperCaseLetterType(included = true),
            lowerCaseLetterType = LowerCaseLetterType(included = true),
            specialCharacterLetterType = SpecialCharacterLetterType(included = true)
        )
        viewModel.generateNewPassword(passwordConfiguration)
    }
}
