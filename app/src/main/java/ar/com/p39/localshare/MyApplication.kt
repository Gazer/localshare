package ar.com.p39.localshare

import android.app.Application

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
        graph = DaggerApplicationComponent.builder().androidModule(AndroidModule(this)).build()
        graph.inject(this)

    }
}