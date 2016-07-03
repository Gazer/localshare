package ar.com.p39.localshare

import android.app.Application
import ar.com.p39.localshare.common.network.NetworkModule
import ar.com.p39.localshare.receiver.fragments.DeveloperSettingsModule
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

/**
 * Base application object
 *
 * Created by gazer on 5/16/16.
 */
class MyApplication : Application() {

    companion object {
        // platformStatic allow access it from java code
        @JvmStatic lateinit var graph: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        graph = DaggerApplicationComponent.builder()
                .androidModule(AndroidModule(this))
                .networkModule(NetworkModule(this))
                .build()
        graph.inject(this)

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }
    }
}