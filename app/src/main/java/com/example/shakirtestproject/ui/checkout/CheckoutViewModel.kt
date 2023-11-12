package com.example.shakirtestproject.ui.checkout

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shakirtestproject.models.CartItemModel
import com.example.shakirtestproject.models.QuantityClickActionModel
import com.example.shakirtestproject.repositories.AppRepository
import com.example.shakirtestproject.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "CheckoutViewModel"

class CheckoutViewModel(private val appRepository: AppRepository) : ViewModel() {

    private var _cartItemsList = MutableLiveData<ArrayList<CartItemModel>>()
    val cartItemList: LiveData<ArrayList<CartItemModel>> get() = _cartItemsList

    private var _cartTotalLiveData = MutableLiveData<Double>()
    val cartTotalLiveData: LiveData<Double> get() = _cartTotalLiveData

    fun getCartItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = appRepository.getCartItems()
            _cartItemsList.postValue(ArrayList(list))
        }
    }

    fun updateCartList(position: Int, item: QuantityClickActionModel) {

        val cartItemModel = CartItemModel(
            item.productId, item.productName, item.productImage, item.quantity, item.price,
            item.totalItemPrice, item.position
        )
        Log.d(TAG, "updateCartList: $cartItemModel")
        val list = _cartItemsList.value
        list?.let {
            it[position] = cartItemModel
            _cartItemsList.value = it
        }

        viewModelScope.launch(Dispatchers.IO) {
            appRepository.updateCartItemInDatabase(cartItemModel)
        }

    }

    fun deleteCartItemFromDatabase(item: QuantityClickActionModel) {
        val cartItemModel = CartItemModel(
            item.productId, item.productName, item.productImage, item.quantity, item.price,
            item.totalItemPrice, item.position
        )
        viewModelScope.launch(Dispatchers.IO) {
            val list = _cartItemsList.value
            list?.let {
                it.removeAt(item.position)
//                _cartItemsList.value = it
                _cartItemsList.postValue(it)
            }
            appRepository.deleteCartItemFromDatabase(cartItemModel)
        }
    }

    fun calculateCartTotal() {
        var cartTotal = 0.0
        val list = _cartItemsList.value
        list?.forEachIndexed { _, cartItemModel ->
            cartTotal += cartItemModel.quantity * cartItemModel.price
        }
        _cartTotalLiveData.value = Utils.formatDoubleValue(cartTotal)
    }

}