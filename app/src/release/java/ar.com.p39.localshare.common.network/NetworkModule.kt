package ar.com.p39.localshare.common.network

import android.app.Application
import android.net.wifi.WifiManager
import ar.com.p39.localshare.Settings
import dagger.Module
import dagger.Provides

@Module
class NetworkModule(private val application: Application) {
    @Provides
    fun provideWifiSSIDPRovider(wifiManager: WifiManager, settings: Settings): WifiSSIDProvider {
        return AndroidSSIDProvider(application)
    }
}