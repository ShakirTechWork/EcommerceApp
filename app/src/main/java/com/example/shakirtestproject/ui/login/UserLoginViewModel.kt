package com.example.shakirtestproject.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shakirtestproject.api.ApiStatus
import com.example.shakirtestproject.models.UserLoginModel
import com.example.shakirtestproject.models.UserTokenModel
import com.example.shakirtestproject.repositories.AppRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

private const val TAG = "UserLoginViewModel"
class UserLoginViewModel(private val appRepository: AppRepository) : ViewModel() {

    private var _isLoadingLiveData = MutableLiveData<Boolean>()
    val isLoadingLiveData: MutableLiveData<Boolean> get() = _isLoadingLiveData

    fun makeUserLogin(userLoginModel: UserLoginModel): Flow<ApiStatus<UserTokenModel?>> {
        // Convert the data class instance to a JSON string
        val gson = Gson()
        val jsonBody = gson.toJson(userLoginModel)

        // Convert the JSON string to a RequestBody
        val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
        return appRepository.makeUserLogin(requestBody)
    }

    fun storeUser(name: String, password: String, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.storeUser(name, password, token)
        }
    }

    fun setIsLoading(boolean: Boolean) {
        Log.d(TAG, "setIsLoading: ${_isLoadingLiveData.value}")
        _isLoadingLiveData.value = boolean
    }

}