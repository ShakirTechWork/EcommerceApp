package com.example.shakirtestproject.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shakirtestproject.repositories.AppRepository

class UserLoginViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserLoginViewModel(appRepository) as T
    }

}