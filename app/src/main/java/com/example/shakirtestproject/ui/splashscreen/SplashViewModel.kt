package com.example.shakirtestproject.ui.splashscreen

import androidx.lifecycle.ViewModel
import com.example.shakirtestproject.repositories.AppRepository
import kotlinx.coroutines.flow.Flow

class SplashViewModel(private val appRepository: AppRepository): ViewModel() {

    val userTokenFlow: Flow<String>
        get() = appRepository.userTokenFlow

}