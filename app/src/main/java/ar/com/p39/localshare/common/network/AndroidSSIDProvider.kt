package ar.com.p39.localshare.common.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager

/**
 * Created by gazer on 5/29/16.
 */
class AndroidSSIDProvider(val context: Context): WifiSSIDProvider {
    private var wifiManager: WifiManager
    private var info: WifiInfo?
    private var wifi: NetworkInfo?

    init {
        wifiManager = context.getSystemService (Context.WIFI_SERVICE) as WifiManager;
        info = wifiManager.connectionInfo;

        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    }

    override fun isConnected(): Boolean {
        return wifi?.isConnected ?: false
    }

    override fun getBSSID():String {
        val tmp = info?.ssid ?: ""
        return tmp.replace("\"", "")
    }
}