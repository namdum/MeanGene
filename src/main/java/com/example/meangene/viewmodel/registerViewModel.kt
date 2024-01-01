package com.example.meangene.viewmodel

import androidx.lifecycle.ViewModel
import com.example.meangene.data.Users
import com.example.meangene.util.Constants.USER_COLLECTION
import com.example.meangene.util.RegisterFieldsState
import com.example.meangene.util.RegisterValidation
import com.example.meangene.util.Resource
import com.example.meangene.util.validateEmail
import com.example.meangene.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(val firebaseAuth:FirebaseAuth, val db:FirebaseFirestore
):ViewModel(){

    private  val _register = MutableStateFlow<Resource<Users>>(Resource.Unspecified())
      val register:Flow<Resource<Users>>  = _register

    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()

fun createAccountEmailAndPassword(users:Users, password:String){
    if(checkValidation(users, password)) {

        runBlocking {

            _register.emit(Resource.Loading())
        }
        firebaseAuth.createUserWithEmailAndPassword(users.email, password)
            .addOnSuccessListener {
                it.user?.let {
                    saveUserInfo(it.uid, users)
                    //_register.value = Resource.Success(it)
                }
            }.addOnFailureListener {

                _register.value = Resource.Error(it.message.toString())

            }
    } else{
        val registerFieldState = RegisterFieldsState(validateEmail(users.email),validatePassword(password))
        runBlocking {
            _validation.send(registerFieldState)
        }
    }
    }

    fun saveUserInfo(userUId: String, user:Users){
        db.collection(USER_COLLECTION)
            .document(userUId)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)

            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())

            }

    }
fun checkValidation(user:Users, password: String):Boolean{
    val emailValidation = validateEmail(user.email)
    val passwordValidation = validatePassword(password)
    val successRegister = emailValidation is RegisterValidation.Success && passwordValidation is
            RegisterValidation.Success

    return successRegister
}

}