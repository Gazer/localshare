package ar.com.p39.localshare.common.network

/**
 * Created by gazer on 5/29/16.
 */
interface WifiSSIDProvider {
    fun getBSSID(): String

    fun isConnected(): Boolean
}