package com.example.shakirtestproject.ui.productdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shakirtestproject.api.ApiStatus
import com.example.shakirtestproject.models.ProductsList
import com.example.shakirtestproject.models.ProductsListItem
import com.example.shakirtestproject.repositories.AppRepository
import kotlinx.coroutines.flow.Flow

class ProductDetailsViewModel(private val appRepository: AppRepository) : ViewModel() {

    private val _isRetryLiveData = MutableLiveData<Boolean>()
    val isRetryLiveData: MutableLiveData<Boolean> get() = _isRetryLiveData

    fun getProductsDetails(productId: Int): Flow<ApiStatus<ProductsListItem?>> {
        return appRepository.getProductDetails(productId)
    }

    fun setRetry(boolean: Boolean) {
        _isRetryLiveData.value = boolean
    }

}