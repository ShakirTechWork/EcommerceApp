package com.example.shakirtestproject.models

data class ProductsListItem(
    val category: String,
    val description: String,
    val id: Int,
    val image: String,
    val price: Double,
    val rating: Rating,
    val title: String,
    var quantity: Int = 0,
)