package com.example.shakirtestproject.api

import com.example.shakirtestproject.models.ProductsList
import com.example.shakirtestproject.models.ProductsListItem
import com.example.shakirtestproject.models.UserTokenModel
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkEndpoints {

    //get products list
//    https://fakestoreapi.com/products?limit=10
    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("sort") sort: String?
    ): Response<ProductsList>

    //    https://fakestoreapi.com/products/1
    @GET("products/{productId}")
    suspend fun getProductDetails(@Path("productId") product_id: Int): Response<ProductsListItem>

    //get all available categories
    //    https://fakestoreapi.com/products/categories
    @GET("products/categories")
    suspend fun getCategories(): Response<List<String>>

    //specific category products
    //    https://fakestoreapi.com/products/category/jewelery
    @GET("products/category/{category}")
    suspend fun getSpecificCategoryProducts(@Path("category") category: String): Response<ProductsList>

    //user login
//    https://fakestoreapi.com/auth/login
    @POST("auth/login")
    suspend fun userLogin(
        @Body body: RequestBody
    ): Response<UserTokenModel>

}