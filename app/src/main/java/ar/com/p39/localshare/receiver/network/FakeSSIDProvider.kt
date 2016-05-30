package ar.com.p39.localshare.receiver.network

import ar.com.p39.localshare.Settings

/**
 * Created by gazer on 5/29/16.
 */
class FakeSSIDProvider(val settings: Settings): WifiSSIDProvider {
    override fun getBSSID(): String {
        return settings.fakedSSID
    }
}