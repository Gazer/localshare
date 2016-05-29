package ar.com.p39.localshare

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import ar.com.p39.localshare.receiver.network.AndroidSSIDProvider
import ar.com.p39.localshare.receiver.network.FakeSSIDProvider
import ar.com.p39.localshare.receiver.network.WifiSSIDPRovider
import ar.com.p39.localshare.receiver.presenters.DownloadPresenter
import ar.com.p39.localshare.sharer.presenters.SharePresenter
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

/**
 * Module for Android injections
 *
 * Created by gazer on 5/16/16.
 */
@Module
class AndroidModule(private val application: Application) {
    /**
     * Allow the application context to be injected but require that it be annotated with [ ][ForApplication] to explicitly differentiate it from an activity context.
     */
    @Provides
    @Singleton
    @ForApplication
    fun provideApplicationContext(): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideDownloadPresenter(client: OkHttpClient): DownloadPresenter {
        return DownloadPresenter(client)
    }

    @Provides
    fun provideWifiMananger(): WifiManager {
        return application.getSystemService (Context.WIFI_SERVICE) as WifiManager;
    }

    @Provides
    @Singleton
    fun providePicasso(): Picasso {
        return Picasso.with(application)
    }

    @Provides
    @Singleton
    fun provideSettings(): Settings {
        return Settings(false, "home")
    }

    @Provides
    @Singleton
    fun providesOkHTTPClient(): OkHttpClient {
        return OkHttpClient()
    }

    @Provides
    fun provideWifiSSIDPRovider(wifiManager: WifiManager, settings: Settings): WifiSSIDPRovider {
        if (settings.fakeSSID) {
            return FakeSSIDProvider(settings);
        }
        return AndroidSSIDProvider(application)
    }
}