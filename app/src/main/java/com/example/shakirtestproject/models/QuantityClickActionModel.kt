package com.example.shakirtestproject.models

data class QuantityClickActionModel(
    val productId: Int,
    val productName: String,
    val productImage: String,
    val quantity: Int,
    val price: Double,
    val totalItemPrice: Double,
    val action: String,
    val position: Int
)
