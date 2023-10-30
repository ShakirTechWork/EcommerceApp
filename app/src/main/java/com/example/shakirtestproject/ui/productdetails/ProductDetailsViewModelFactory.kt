package com.example.shakirtestproject.ui.productdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shakirtestproject.repositories.AppRepository

class ProductDetailsViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductDetailsViewModel(appRepository) as T
    }

}