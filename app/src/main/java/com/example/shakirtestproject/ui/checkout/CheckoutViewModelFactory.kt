package com.example.shakirtestproject.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shakirtestproject.repositories.AppRepository

class CheckoutViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CheckoutViewModel(appRepository) as T
    }

}