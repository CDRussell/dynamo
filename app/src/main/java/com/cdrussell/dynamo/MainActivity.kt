package com.cdrussell.dynamo

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Switch
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.Observer
import com.cdrussell.dynamo.PasswordGenerator.*
import com.cdrussell.dynamo.PasswordGenerator.PasswordResult.PasswordFailure
import com.cdrussell.dynamo.PasswordGenerator.PasswordResult.PasswordSuccess
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.jetbrains.handson.mpp.mobile.R


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
        restoreUiPreferences()
        configureUiEventHandlers()
        configureViewStateObserver()
        generateNewPassword()
    }

    private fun restoreUiPreferences() {
        with(sharedPrefs()) {
            val pwLength = getInt(PREF_KEY_PASSWORD_LENGTH, 12)
            with(passwordLengthPicker) {
                minValue = 4
                maxValue = 60
                wrapSelectorWheel = false
                value = pwLength
            }

            includeUppercaseSwitch.isChecked = getBoolean(PREF_KEY_PASSWORD_USE_UPPERCASE, true)
            includeLowercaseSwitch.isChecked = getBoolean(PREF_KEY_PASSWORD_USE_LOWERCASE, true)
            includeNumbersSwitch.isChecked = getBoolean(PREF_KEY_PASSWORD_USE_NUMBERS, true)
            includeSpecialCharacterSwitch.isChecked = getBoolean(PREF_KEY_PASSWORD_USE_SPECIAL_CHARS, true)
        }
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
        passwordLengthPicker.setOnValueChangedListener { _, _, newValue ->
            generateNewPassword()
            savePreferredLength(newValue)
        }

        includeUppercaseSwitch.setOnCheckedChangeListener { _, checked ->
            generateNewPassword()
            saveCharacterTypePreference(PREF_KEY_PASSWORD_USE_UPPERCASE, checked)
        }

        includeLowercaseSwitch.setOnCheckedChangeListener { _, checked ->
            generateNewPassword()
            saveCharacterTypePreference(PREF_KEY_PASSWORD_USE_LOWERCASE, checked)
        }

        includeNumbersSwitch.setOnCheckedChangeListener { _, checked ->
            generateNewPassword()
            saveCharacterTypePreference(PREF_KEY_PASSWORD_USE_NUMBERS, checked)
        }

        includeSpecialCharacterSwitch.setOnCheckedChangeListener { _, checked ->
            generateNewPassword()
            saveCharacterTypePreference(PREF_KEY_PASSWORD_USE_SPECIAL_CHARS, checked)
        }
    }

    private fun CharSequence.copyToClipboard() {
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("password", this)
        clipboard.setPrimaryClip(clip)

        Snackbar.make(bottomBar,
            R.string.copiedToClipboard, Snackbar.LENGTH_SHORT).also { snackbar ->
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

    private fun sharedPrefs(): SharedPreferences {
        return getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
    }

    private fun saveCharacterTypePreference(preferenceKey: String, checked: Boolean) {
        sharedPrefs().edit { putBoolean(preferenceKey, checked) }
    }

    private fun savePreferredLength(newValue: Int) {
        sharedPrefs().edit { putInt(PREF_KEY_PASSWORD_LENGTH, newValue) }
    }

    companion object {
        private const val PREF_FILE = "ui_prefs"
        private const val PREF_KEY_PASSWORD_LENGTH = "pw_length"
        private const val PREF_KEY_PASSWORD_USE_UPPERCASE = "pw_use_upper"
        private const val PREF_KEY_PASSWORD_USE_LOWERCASE = "pw_use_lower"
        private const val PREF_KEY_PASSWORD_USE_NUMBERS = "pr_use_numbers"
        private const val PREF_KEY_PASSWORD_USE_SPECIAL_CHARS = "pr_use_special_chars"
    }
}
