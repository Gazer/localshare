package ar.com.p39.localshare.common

import android.net.wifi.WifiManager
import android.util.Log
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder

/**
 * Extension method to get the Wifi IPAddress
 *
 * @return {@code InetAddress} or {@code null}
 */
fun WifiManager.IpAddress(): String? {
    var ipAddress = connectionInfo.ipAddress

    // Convert little-endian to big-endian if needed
    if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
        ipAddress = Integer.reverseBytes(ipAddress)
    }

    val ipByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()

    try {
        return InetAddress.getByAddress(ipByteArray).hostAddress
    } catch (ex: UnknownHostException) {
        Log.e("WifiManager.IpAddress", "Unable to get host address.")
    }
    return null
}