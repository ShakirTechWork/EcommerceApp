package com.example.shakirtestproject.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.example.shakirtestproject.Application
import com.example.shakirtestproject.MainActivity
import com.example.shakirtestproject.R
import com.example.shakirtestproject.ui.login.UserLoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val repository = (application as Application).appRepository

        splashViewModel = ViewModelProvider(this, SplashViewModelFactory(repository))[SplashViewModel::class.java]

        splashViewModel.userTokenFlow.asLiveData().observe(this) {
            if (it.equals("")) {
                navigate("UserLoginActivity")
            } else {
                navigate("MainActivity")
            }
        }

    }

    private fun navigate(nextScreen: String) {
        lifecycleScope.launch {
            delay(1000) // 1 seconds delay for splash screen
            if (nextScreen == "UserLoginActivity") {
                val intent = Intent(this@SplashActivity, UserLoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}