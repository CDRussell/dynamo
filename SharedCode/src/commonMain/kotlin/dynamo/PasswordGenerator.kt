package com.cdrussell.dynamo

import com.cdrussell.dynamo.PasswordGenerator.PasswordResult.PasswordFailure
import com.cdrussell.dynamo.PasswordGenerator.PasswordResult.PasswordSuccess
import kotlin.random.Random

class PasswordGenerator {

    private val asciiNumbers = initialiseNumbers()
    private val asciiUppercaseLetters = initialiseUppercaseLetters()
    private val asciiLowercaseLetters = initialiseLowercaseLetters()
    private val asciiSpecialCharacters = initialiseSpecialCharacters()

    fun generatePassword(passwordConfiguration: PasswordConfiguration): PasswordResult {
        if (passwordConfiguration.requiredLength <= 0) return PasswordFailure(IllegalArgumentException("Required password length but be > 0"))
        if (!passwordConfiguration.characterTypesAvailable()) return PasswordFailure(IllegalArgumentException("No characters types selected"))

        val minimumCharactersToSatisfyEachConstraint = calculateMinimumCharactersFromCriteria(passwordConfiguration)

        if (minimumCharactersToSatisfyEachConstraint > passwordConfiguration.requiredLength) {
            return PasswordFailure(IllegalArgumentException("$minimumCharactersToSatisfyEachConstraint characters required to match subtypes, but total password length is too short: ${passwordConfiguration.requiredLength}"))
        }

        val characterCandidates = initializeAvailableCharacters(passwordConfiguration)

        val sb = StringBuilder()
        for (i in 0 until passwordConfiguration.requiredLength) {
            sb.append(characterCandidates.randomCharacter())
        }

        return PasswordSuccess(sb.toString())
    }

    private fun initializeAvailableCharacters(passwordConfiguration: PasswordConfiguration): MutableList<Char> {
        val characterCandidates = mutableListOf<Char>().also {
            if (passwordConfiguration.numericalType.included) it.addAll(asciiNumbers)
            if (passwordConfiguration.upperCaseLetterType.included) it.addAll(asciiUppercaseLetters)
            if (passwordConfiguration.lowerCaseLetterType.included) it.addAll(asciiLowercaseLetters)
            if (passwordConfiguration.specialCharacterLetterType.included) it.addAll(asciiSpecialCharacters)
        }
        return characterCandidates
    }

    private fun initialiseNumbers(): List<Char> {
        return initialiseAsciiRange(48, 57)
    }

    private fun initialiseUppercaseLetters(): List<Char> {
        return initialiseAsciiRange(65, 90)
    }

    private fun initialiseLowercaseLetters(): List<Char> {
        return initialiseAsciiRange(97, 122)
    }

    private fun initialiseSpecialCharacters(): List<Char> {
        return mutableListOf<Char>().also {
            it.addAll(initialiseAsciiRange(33, 47))
            it.addAll(initialiseAsciiRange(58, 64))
            it.addAll(initialiseAsciiRange(91, 96))
            it.addAll(initialiseAsciiRange(123, 126))
        }
    }

    private fun initialiseAsciiRange(startRange: Int, endRange: Int): List<Char> {
        val list = mutableListOf<Char>()
        for (i in startRange..endRange) {
            list.add(i.toChar())
        }
        return list.toList()
    }

    private fun calculateMinimumCharactersFromCriteria(passwordConfiguration: PasswordConfiguration): Int {
        val minimumCharactersToSatisfyEachConstraint = 0 +
                passwordConfiguration.numericalType.minimumCharactersRequired() +
                passwordConfiguration.lowerCaseLetterType.minimumCharactersRequired() +
                passwordConfiguration.upperCaseLetterType.minimumCharactersRequired() +
                passwordConfiguration.specialCharacterLetterType.minimumCharactersRequired()
        return minimumCharactersToSatisfyEachConstraint
    }

    // This should really use a `SecureRandom` equivalent but that's not available for Kotlin MPP currently
    private fun MutableList<Char>.randomCharacter() = this[Random.nextInt(this.size)]

    sealed class PasswordResult {
        data class PasswordSuccess(val password: String) : PasswordResult()
        data class PasswordFailure(val exception: Exception) : PasswordResult()
    }

    data class PasswordConfiguration(
        val requiredLength: Int,
        val numericalType: NumericalType,
        val upperCaseLetterType: UpperCaseLetterType,
        val lowerCaseLetterType: LowerCaseLetterType,
        val specialCharacterLetterType: SpecialCharacterLetterType
    )

    data class SelectedCharacter(val character: Char, val type: CharacterType)

    abstract class CharacterType(open val included: Boolean)

    data class NumericalType(override val included: Boolean) : CharacterType(included)
    data class UpperCaseLetterType(override val included: Boolean) : CharacterType(included)
    data class LowerCaseLetterType(override val included: Boolean) : CharacterType(included)
    data class SpecialCharacterLetterType(override val included: Boolean) : CharacterType(included)

    private fun CharacterType.minimumCharactersRequired(): Int {
        if (!this.included) return 0
        return 1
    }

    private fun PasswordConfiguration.characterTypesAvailable(): Boolean {
        if (upperCaseLetterType.included) return true
        if (lowerCaseLetterType.included) return true
        if (numericalType.included) return true
        if (specialCharacterLetterType.included) return true
        return false
    }
}