package com.example.meangene.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meangene.data.Product
import com.example.meangene.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore

):ViewModel() {
    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts : StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealsProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsProducts : StateFlow<Resource<List<Product>>> = _bestDealsProducts

    private val _baseProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val baseProducts : StateFlow<Resource<List<Product>>> = _baseProducts

    private val pagingInfo = PagingInfo()

    init {
        fetchSpecialProducts()
        fetchBestDeals()
        fetchBaseProducts()
    }


    fun fetchSpecialProducts(){
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category", "Special Products")
            .get().addOnSuccessListener(){ result ->
                val specialProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }


            }

    }
    fun fetchBestDeals(){
        viewModelScope.launch {
            _bestDealsProducts.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category", "Best Deals")
            .get().addOnSuccessListener(){ result ->
                val bestDealsProducts = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Success(bestDealsProducts))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Error(it.message.toString()))
                }


            }

    }
    fun fetchBaseProducts(){

        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _baseProducts.emit(Resource.Loading())
            }
            firestore.collection("Products").limit(pagingInfo.bestProductsPage * 10)
                .get().addOnSuccessListener() { result ->
                    val baseProducts = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = baseProducts == pagingInfo.oldestBestProducts
                    pagingInfo.oldestBestProducts = baseProducts
                    viewModelScope.launch {
                        _baseProducts.emit(Resource.Success(baseProducts))
                    }
                    pagingInfo.bestProductsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _baseProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
    internal data class PagingInfo(
         var bestProductsPage:Long = 1,
        var oldestBestProducts: List<Product> = emptyList(),
        var isPagingEnd:Boolean = false
    )
}