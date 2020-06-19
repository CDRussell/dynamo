package com.jetbrains.handson.mpp.mobile

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Switch
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.cdrussell.dynamo.PasswordGenerator.*
import com.cdrussell.dynamo.PasswordGenerator.PasswordResult.PasswordFailure
import com.cdrussell.dynamo.PasswordGenerator.PasswordResult.PasswordSuccess
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var generatedPasswordField: TextView
    private lateinit var bottomBar: BottomAppBar
    private lateinit var passwordLengthPicker: NumberPicker
    private lateinit var newPasswordButton: FloatingActionButton
    private lateinit var includeNumbersSwitch: Switch
    private lateinit var includeUppercaseSwitch: Switch
    private lateinit var includeLowercaseSwitch: Switch
    private lateinit var includeSpecialCharacterSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureViewReferences()
        configureUiEventHandlers()
        configureViewStateObserver()
        generateNewPassword()
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
        bottomBar = findViewById(R.id.bottomAppBar)
        generatedPasswordField = findViewById(R.id.generatedPasswordText)
        passwordLengthPicker = findViewById(R.id.lengthPicker)
        newPasswordButton = findViewById(R.id.newPasswordButton)
        includeUppercaseSwitch = findViewById(R.id.useUppercaseSwitch)
        includeLowercaseSwitch = findViewById(R.id.useLowercaseSwitch)
        includeNumbersSwitch = findViewById(R.id.useNumbersSwitch)
        includeSpecialCharacterSwitch = findViewById(R.id.useSpecialCharactersSwitch)
    }

    private fun configureUiEventHandlers() {
        newPasswordButton.setOnClickListener { generateNewPassword() }

        bottomBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.copyPassword -> {
                    generatedPasswordField.text.copyToClipboard()
                    true
                }
                else -> false
            }
        }
        with(passwordLengthPicker) {
            minValue = 6
            maxValue = 30
            wrapSelectorWheel = false
        }
    }

    private fun CharSequence.copyToClipboard() {
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("password", this)
        clipboard.setPrimaryClip(clip)

        Snackbar.make(bottomBar, R.string.copiedToClipboard, Snackbar.LENGTH_SHORT).also { snackbar ->
            snackbar.anchorView = bottomBar
        }.show()
    }

    private fun generateNewPassword() {
        val passwordConfiguration = PasswordConfiguration(
            requiredLength = passwordLengthPicker.value,
            numericalType = NumericalType(included = includeNumbersSwitch.isChecked),
            upperCaseLetterType = UpperCaseLetterType(included = includeUppercaseSwitch.isChecked),
            lowerCaseLetterType = LowerCaseLetterType(included = includeLowercaseSwitch.isChecked),
            specialCharacterLetterType = SpecialCharacterLetterType(included = includeSpecialCharacterSwitch.isChecked)
        )
        viewModel.generateNewPassword(passwordConfiguration)
    }
}
