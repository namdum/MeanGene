package com.example.meangene.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.meangene.R
import com.example.meangene.data.Users
import com.example.meangene.databinding.FragmentRegisterBinding
import com.example.meangene.util.RegisterValidation
import com.example.meangene.util.Resource
import com.example.meangene.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment:Fragment() {
    private lateinit var binding:FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> ()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding  = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registerHeaderText.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)

        }

        binding.apply {

            registerRegisterBtn.setOnClickListener{
                val user = Users(
                   registerFirstnameEdit.text.toString().trim(),
                    registerLastnameEdit.text.toString().trim(),
                    registerEmailEdit.text.toString().trim()
                )
                val password = registerPasswordEdit.text.toString()
                viewModel.createAccountEmailAndPassword(user, password)
            }
        }
        lifecycleScope.launch {
            viewModel.register.collect{
                when(it){
                    is Resource.Loading ->{
                        binding.registerRegisterBtn.startAnimation()
                    }
                    is Resource.Success ->{
                        Log.d("test", it.data.toString())
                        binding.registerRegisterBtn.revertAnimation()

                    }
                    is Resource.Error ->{
                        Log.e(TAG, it.message.toString())
                        binding.registerRegisterBtn.revertAnimation()

                    }
                    else -> Unit

                }
            }
        }
        lifecycleScope.launch {
            viewModel.validation.collect{ validation ->
                if(validation.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.registerEmailEdit.apply{
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }
                if(validation.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.registerPasswordEdit.apply{
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }
}