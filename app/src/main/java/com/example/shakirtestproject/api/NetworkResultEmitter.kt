package com.example.shakirtestproject.api

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response

private const val TAG = "NetworkResultEmitter"
@OptIn(ExperimentalStdlibApi::class)
fun <T> result(call: suspend () -> Response<T>): Flow<ApiStatus<T?>> = flow {
    emit(ApiStatus.Loading)

    try {

//        Log.d(TAG, "result123: called on: ${Thread.currentThread().name}")
//        Log.d(TAG, "result123: called on: ${coroutineContext[CoroutineDispatcher.Key]}")

//        val c = call()
        val c = withContext(Dispatchers.IO) {
            Log.d(TAG, "result123: API called on: ${Thread.currentThread().name}")
            Log.d(TAG, "result123: API called on: ${kotlin.coroutines.coroutineContext[CoroutineDispatcher.Key]}")
            call()
        }
        c.let {
            if (c.isSuccessful) {
                emit(ApiStatus.Success(it.body()))
            } else if (c.code()==401) {
                emit(ApiStatus.Failure("401", "Unauthorized"))
            } else if (c.code() == 404) {
                emit(ApiStatus.Failure("404", "Not Found"))
            } else {
                c.errorBody()?.let {error ->
                    error.close()
                    Log.d(TAG, "error_result: $error")
                    val errorJsonString = error.string() // Assuming error.string() is used to get the JSON string
                    val jsonObject = JSONObject(errorJsonString)
                    val errorCode = jsonObject.getJSONObject("error").getInt("code")
                    val errorMessage = jsonObject.getJSONObject("error").getString("message")
                    emit(ApiStatus.Failure(errorCode.toString(), errorMessage))

                }
            }
        }

    } catch (t: Throwable) {
        Log.e(TAG, "API_Error: ${t.printStackTrace()}" )
        emit(ApiStatus.Failure(t.message.toString(), ""))
    }

}