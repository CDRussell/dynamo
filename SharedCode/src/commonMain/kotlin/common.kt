package com.jetbrains.handson.mpp.mobile

expect fun platformName(): String

fun createApplicationScreenMessage(): String {
    return "Kotlin Rocks on ${platformName()}"
}

fun generatePassword() : String {
    return "password"
}
