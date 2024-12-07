package `in`.devcold.movica.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

object NetworkUtils {

    private lateinit var connectivityManager: ConnectivityManager

    private var networkChangedCallback: SimpleCallback? = null

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            networkChangedCallback?.invoke()
        }

        override fun onLost(network: Network) {
            networkChangedCallback?.invoke()
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            networkChangedCallback?.invoke()
        }

        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            networkChangedCallback?.invoke()
        }
    }

    fun init(context: Context) {
        connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    }

    fun isConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun registerNetworkCallback() = connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

    fun unregisterNetworkCallback() = connectivityManager.unregisterNetworkCallback(networkCallback)

    fun setNetworkChangedCallback(callback: SimpleCallback) {
        networkChangedCallback = callback
    }

    fun removeNetworkChangedCallback() {
        networkChangedCallback = null
    }
}

