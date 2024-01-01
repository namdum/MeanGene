package com.example.meangene.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.meangene.R
import com.example.meangene.activites.ShoppingActivity
import com.example.meangene.databinding.FragmentLoginBinding
import com.example.meangene.dialog.setUpBottomSheetDialog
import com.example.meangene.util.RegisterValidation
import com.example.meangene.util.Resource
import com.example.meangene.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment:Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dontHaveAccount.setOnClickListener {

            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.apply {
            loginLoginBtn.setOnClickListener {
                val email = loginEmailEdit.text.toString().trim()
                val password = loginPasswordEdit.text.toString()
                viewModel.logIn(email, password)

            }
        }
        binding.loginForgotPass.setOnClickListener {
            setUpBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }
        lifecycleScope.launch {
            viewModel.resetPassword.collect {
                when(it){
                    is Resource.Loading ->{

                    }is Resource.Success ->{
                        Snackbar.make(requireView(),
                            "Reset Link was sent to your email",Snackbar.LENGTH_LONG).show()

                    }is Resource.Error -> {
                        Snackbar.make(requireView(),
                            "Error: ${it.message}",Snackbar.LENGTH_LONG).show()

                    }else -> Unit


                }








            }
        }
        lifecycleScope.launch {
            viewModel.login.collect{
                when(it){
                is Resource.Loading ->{
                    binding.loginLoginBtn.startAnimation()
                }is Resource.Success ->{
                    binding.loginLoginBtn.revertAnimation()

                    Intent(requireActivity(), ShoppingActivity::class.java).also{intent ->
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }

                }is Resource.Error -> {
                    Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()
                    binding.loginLoginBtn.revertAnimation()

                }else -> Unit


                }

            }



        }
        //added validation to login frag
        lifecycleScope.launch {
            viewModel.validation.collect{ validation ->
                if(validation.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.loginEmailEdit.apply{
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }
                if(validation.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.loginPasswordEdit.apply{
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }

    }
}