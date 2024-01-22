package com.example.meangene.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meangene.data.Address
import com.example.meangene.data.Product
import com.example.meangene.data.order.Order
import com.example.meangene.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _search = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val search = _search.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun getSearch(searchVar: String) {
        println("...........$searchVar")
        val validateInputs = validateInputs(searchVar)

        if (validateInputs){
        viewModelScope.launch {
            _search.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .orderBy("name")
            .startAt(searchVar)
            .endAt(searchVar + '\uf8ff').get()

            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    it.let {
                    _search.emit(Resource.Success(products))
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {

                    _search.emit(Resource.Error(it.message.toString()))
                }
            }
            }
    }
    private fun validateInputs(search: String): Boolean {
        return search.trim().isNotEmpty()
    }
}