package com.example.shakirtestproject.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.shakirtestproject.Application
import com.example.shakirtestproject.MainActivity
import com.example.shakirtestproject.api.ApiStatus
import com.example.shakirtestproject.databinding.ActivityUserLoginBinding
import com.example.shakirtestproject.models.UserLoginModel
import com.example.shakirtestproject.utils.Utils

private const val TAG = "UserLoginActivity"

class UserLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserLoginBinding

    private lateinit var userLoginViewModel: UserLoginViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = (application as Application).appRepository

        userLoginViewModel = ViewModelProvider(
            this,
            UserLoginViewModelFactory(repository)
        )[UserLoginViewModel::class.java]

        binding.edtUserName.setText("mor_2314")
        binding.edtUserPassword.setText("83r5^_")

        binding.edtUserName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d(TAG, "beforeTextChanged: ${p0.toString()}")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.tilUsername.error != null) {
                    binding.tilUsername.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d(TAG, "afterTextChanged: ${p0.toString()}")
            }

        })

        binding.edtUserPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d(TAG, "beforeTextChanged: ${p0.toString()}")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.tilUserPassword.error != null) {
                    binding.tilUserPassword.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d(TAG, "afterTextChanged: ${p0.toString()}")
            }

        })

        binding.btnLogin.setOnClickListener {

            val userName = binding.edtUserName.text?.trim().toString()
            val userPassword = binding.edtUserPassword.text?.trim().toString()

            if (userName.isNotBlank() && userPassword.isNotBlank()) {
                val userLoginModel = UserLoginModel(password = userPassword, username = userName)

                if (Utils.isInternetAvailable(this)) {
                    userLoginViewModel.makeUserLogin(userLoginModel).asLiveData().observe(this) {
                        when (it) {
                            is ApiStatus.Success -> {
                                val data = it.data
                                Log.d(TAG, "Called_API_Status  makeUserLogin: Success: $data")
                                if (data != null && data.token.isNotBlank()) {
                                    userLoginViewModel.setIsLoading(false)
                                    Utils.showShortToast(this, "Logging you in!")
                                    userLoginViewModel.storeUser(
                                        userName,
                                        userPassword,
                                        it.data.token
                                    )
                                    val intent =
                                        Intent(this@UserLoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    userLoginViewModel.setIsLoading(false)
                                    Utils.showShortToast(this, "Retry again.")
                                }
                            }
                            is ApiStatus.Failure -> {
                                Log.d(
                                    TAG,
                                    "Called_API_Status  makeUserLogin: Failure:  message:${it.message}         code:${it.code}"
                                )
                                userLoginViewModel.setIsLoading(false)
                                Utils.showLongToast(this, "Please enter the correct credentials.")
                            }
                            ApiStatus.Loading -> {
                                userLoginViewModel.setIsLoading(true)
                                Log.d(TAG, "Called_API_Status  makeUserLogin: Loading")
                            }
                        }
                    }
                } else {
                    Utils.showLongToast(this, "Please check your Internet Connection.")
                }
            } else if (userName.isBlank() && userPassword.isBlank()) {
                binding.tilUsername.error = "Please enter this detail!"
                binding.edtUserPassword.error = "Please enter this detail!"
            } else if (userName.isBlank()) {
                binding.tilUsername.error = "Please enter this detail!"
            } else if (userPassword.isBlank()) {
                binding.tilUserPassword.error = "Please enter this detail!"
            }
        }

    }
}