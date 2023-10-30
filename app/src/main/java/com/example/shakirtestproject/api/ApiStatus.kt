package com.example.shakirtestproject.api

sealed class ApiStatus<out T> {
    data class Success<out R>(val data: R?) : ApiStatus<R>()
    data class Failure(val code: String, val message: String) : ApiStatus<Nothing>()
    object Loading : ApiStatus<Nothing>()
}
