package com.example.meangene.util

import android.util.Patterns
import java.util.regex.Pattern

//validates email and passwords for values
fun validateEmail(email: String):RegisterValidation{

    if(email.isEmpty()){
        return RegisterValidation.Failed("Email cannot be empty")
    }
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
        return RegisterValidation.Failed("Email must be in proper format")
    }
    return RegisterValidation.Success
}

fun validatePassword(password: String ):RegisterValidation{
    if(password.isEmpty()){
        return RegisterValidation.Failed("Password cannot be empty")
    }
    if(password.length < 6){
        return RegisterValidation.Failed("Password cannot be less than 6 Chars")

    }
    return RegisterValidation.Success
}