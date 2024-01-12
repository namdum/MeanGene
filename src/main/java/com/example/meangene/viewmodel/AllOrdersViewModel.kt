package com.example.meangene.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meangene.data.order.Order
import com.example.meangene.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllOrdersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _allOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val allOrders = _allOrders.asStateFlow()

    private val pagingInfo = PagingInfo()


    init {
        getAllOrders()
    }

    fun getAllOrders(){
        viewModelScope.launch {
            _allOrders.emit(Resource.Loading())
        }

        firestore.collection("user").document(auth.uid!!).collection("orders")
            .limit(pagingInfo.allOrdersPage * 10).get()
            .addOnSuccessListener {
                val orders = it.toObjects(Order::class.java)
                pagingInfo.isPagingEnd = orders == pagingInfo.oldestAllOrders
                pagingInfo.oldestAllOrders = orders
                viewModelScope.launch {
                    _allOrders.emit(Resource.Success(orders))
                }
                pagingInfo.allOrdersPage++
            }.addOnFailureListener {
                viewModelScope.launch {
                    _allOrders.emit(Resource.Error(it.message.toString()))
                }
            }
    }

}
internal data class PagingInfo(
    var allOrdersPage:Long = 1,
    var oldestAllOrders: List<Order> = emptyList(),
    var isPagingEnd:Boolean = false
)














