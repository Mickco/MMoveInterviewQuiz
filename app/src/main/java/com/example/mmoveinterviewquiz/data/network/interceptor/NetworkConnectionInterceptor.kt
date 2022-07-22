package com.example.mmoveinterviewquiz.data.network.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.UnknownHostException

class NetworkConnectionInterceptor(private val context: Context) : Interceptor {

    private val isConnected: Boolean
        get() {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val nc = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                nc.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected) {
            throw UnknownHostException()
        }
        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }
}