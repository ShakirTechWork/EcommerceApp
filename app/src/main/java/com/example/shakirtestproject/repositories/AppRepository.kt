package com.example.shakirtestproject.repositories

import android.content.Context
import com.example.shakirtestproject.api.NetworkEndpoints
import com.example.shakirtestproject.api.result
import com.example.shakirtestproject.datastore.AppDataStore
import com.example.shakirtestproject.models.CartItemModel
import com.example.shakirtestproject.roomdb.AppDatabase
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

class AppRepository(
    private val networkEndpoints: NetworkEndpoints,
    private val appDataStore: AppDataStore,
    private val appDatabase: AppDatabase,
    private val applicationContext: Context
) {

    fun getProducts(limit: Int, sort: String?) = result {
        networkEndpoints.getProducts(limit, sort)
    }

    fun getProductDetails(productId: Int) = result {
        networkEndpoints.getProductDetails(productId)
    }

    fun makeUserLogin(userLoginModel: RequestBody) = result {
        networkEndpoints.userLogin(userLoginModel)
    }

    fun getCategories() = result {
        networkEndpoints.getCategories()
    }

    fun getSpecificCategoryProducts(categoryName: String) = result {
        networkEndpoints.getSpecificCategoryProducts(categoryName)
    }

    suspend fun updateCartItemInDatabase(cartItemModel: CartItemModel) {
        appDatabase.roomDao().updateCartItem(cartItemModel)
    }

    suspend fun deleteCartItemFromDatabase(cartItemModel: CartItemModel) {
        appDatabase.roomDao().deleteCartItem(cartItemModel)
    }

    suspend fun getCartItems() : List<CartItemModel> {
        return appDatabase.roomDao().getCartItems()
    }

    suspend fun storeUser(name: String, password: String, token: String) {
        appDataStore.storeUser(name, password, token)
    }

    suspend fun logoutUser() {
        appDataStore.logoutUser()
    }

    fun deleteDatabase() {
        appDatabase.clearAllTables()
    }

    val userTokenFlow: Flow<String>
        get() = appDataStore.userTokenFlow

}