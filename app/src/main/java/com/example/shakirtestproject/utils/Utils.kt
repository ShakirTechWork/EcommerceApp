package com.example.shakirtestproject.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.roundToInt

object Utils {

    fun showLongToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun showShortToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun isInternetAvailable(context: Context): Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return this.getNetworkCapabilities(this.activeNetwork)?.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                ) ?: false
            } else {
                (@Suppress("DEPRECATION")
                return this.activeNetworkInfo?.isConnected ?: false)
            }
        }
    }

    fun formatDoubleValue(rawNumber: Double): Double {
        return String.format("%.2f", rawNumber).toDouble()
    }

    fun roundOffNumber(value: Double): Int {
        return value.roundToInt()
    }

    fun twoOptionAlertDialog(
        context: Context,
        title: String,
        msg: String,
        positiveText: String,
        negativeText: String,
        isCancelable: Boolean,
        positiveMethod: () -> Unit = {},
        negativeMethod: () -> Unit = {}
    ) {
        val dialogBuilder = MaterialAlertDialogBuilder(context)
        dialogBuilder.setMessage(msg)
            .setCancelable(isCancelable)
            .setPositiveButton(positiveText) { dialog, _ ->
                dialog.cancel()
                positiveMethod()
            }
            .setNegativeButton(negativeText) { dialog, _ ->
                dialog.cancel()
                negativeMethod()
            }
        val alert = dialogBuilder.create()
        alert.setTitle(title)
        alert.show()
    }

}