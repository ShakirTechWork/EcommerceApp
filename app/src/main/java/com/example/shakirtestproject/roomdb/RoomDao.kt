package com.example.shakirtestproject.roomdb

import androidx.room.*
import com.example.shakirtestproject.models.CartItemModel

@Dao
interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCartItem(item: CartItemModel)

    @Delete
    suspend fun deleteCartItem(item: CartItemModel)

    @Query("SELECT * FROM cart")
    suspend fun getCartItems() : List<CartItemModel>

}