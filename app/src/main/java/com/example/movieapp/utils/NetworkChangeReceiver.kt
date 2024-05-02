package com.example.movieapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.annotation.RequiresApi


class NetworkChangeReceiver(private val listener: NetworkChangeListener) {
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    @RequiresApi(Build.VERSION_CODES.N)
    fun register(context: Context) {
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                listener.onNetworkChanged(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                listener.onNetworkChanged(false)
            }
        }
        connectivityManager?.registerDefaultNetworkCallback(networkCallback!!)
    }

    fun unregister() {
        connectivityManager?.unregisterNetworkCallback(networkCallback!!)
    }

    interface NetworkChangeListener {
        fun onNetworkChanged(isConnected: Boolean)
    }
}