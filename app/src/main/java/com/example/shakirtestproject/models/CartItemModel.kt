package com.example.shakirtestproject.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartItemModel(
    @PrimaryKey(autoGenerate = false)
    val product_id: Int,
    val product_name: String,
    val product_image: String,
    var quantity: Int,
    val price: Double,
    val total_item_price: Double,
    val position: Int
)
