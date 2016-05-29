package ar.com.p39.localshare.receiver.network

import android.content.Context
import android.net.wifi.WifiManager

/**
 * Created by gazer on 5/29/16.
 */
class AndroidSSIDProvider(val context: Context): WifiSSIDPRovider {
    override fun getBSSID():String {
        val wifiManager = context.getSystemService (Context.WIFI_SERVICE) as WifiManager;
        val info = wifiManager.connectionInfo;
        return info.ssid
    }
}