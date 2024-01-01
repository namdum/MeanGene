package com.example.meangene.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meangene.data.Users
import com.example.meangene.util.RegisterFieldsState
import com.example.meangene.util.RegisterValidation
import com.example.meangene.util.Resource
import com.example.meangene.util.validateEmail
import com.example.meangene.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
   private val firebaseAuth: FirebaseAuth
):ViewModel() {

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()


    private val _resetPassowrd = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassowrd.asSharedFlow()

    fun logIn(email:String, password:String){

        //added check validation conditional
        if (checkValidation(email, password)) {
            runBlocking {

                _login.emit(Resource.Loading())
            }

            viewModelScope.launch { _login.emit(Resource.Loading()) }
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        it.user?.let {
                            _login.emit(Resource.Success(it))

                        }
                    }

                }.addOnFailureListener {
                    viewModelScope.launch {
                        _login.emit(Resource.Error(it.message.toString()))

                    }
                }
        } else{
            //add log in field state if user not validated log in
            val loginFieldState = RegisterFieldsState(validateEmail(email),validatePassword(password))
            runBlocking {
                _validation.send(loginFieldState)
            }
        }
    }
    fun resetPassword(email:String){
        viewModelScope.launch {
            _resetPassowrd.emit(Resource.Loading())
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _resetPassowrd.emit(Resource.Success(email))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _resetPassowrd.emit(Resource.Error(it.message.toString()))
                    }

                }
        }

    }
    fun checkValidation(email: String, password: String):Boolean{
        val emailValidation = validateEmail(email)
        val passwordValidation = validatePassword(password)
        val successRegister = emailValidation is RegisterValidation.Success && passwordValidation is
                RegisterValidation.Success

        return successRegister
    }

}