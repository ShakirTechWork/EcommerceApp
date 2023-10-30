package com.example.shakirtestproject.ui.productslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shakirtestproject.repositories.AppRepository

class ProductsListViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductsListViewModel(appRepository) as T
    }

}