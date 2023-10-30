package com.example.shakirtestproject.roomdb

import androidx.annotation.WorkerThread
import androidx.room.*
import com.example.shakirtestproject.models.CartItemModel

@Dao
interface RoomDao {

    @Insert
    fun addItem(quotes: List<CartItemModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @WorkerThread
    suspend fun updateCartItem(item: CartItemModel)

    @Delete
    @WorkerThread
    suspend fun deleteCartItem(item: CartItemModel)

    @Delete
    suspend fun delete(item: CartItemModel)

    @Query("SELECT * FROM cart")
    @WorkerThread
    suspend fun getCartItems() : List<CartItemModel>

}