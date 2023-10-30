package com.example.shakirtestproject

import android.app.Application
import com.example.shakirtestproject.api.NetworkEndpoints
import com.example.shakirtestproject.api.RetrofitHelper
import com.example.shakirtestproject.datastore.AppDataStore
import com.example.shakirtestproject.repositories.AppRepository
import com.example.shakirtestproject.roomdb.AppDatabase

class Application: Application() {

    lateinit var appRepository: AppRepository

    override fun onCreate() {
        super.onCreate()
        initialize()
    }

    private fun initialize() {
        val networkEndpoints = RetrofitHelper.getRetrofitInstance().create(NetworkEndpoints::class.java)
        val appDatabase = AppDatabase.getDatabase(applicationContext)
        val appDataStore = AppDataStore(applicationContext)
        appRepository = AppRepository(networkEndpoints, appDataStore, appDatabase, applicationContext)
    }

}