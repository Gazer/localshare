package ar.com.p39.localshare.receiver

import ar.com.p39.localshare.receiver.activities.DownloadActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Main DI module
 *
 * Created by gazer on 5/16/16.
 */
@Singleton
@Component(modules = arrayOf(AndroidModule::class))
interface ApplicationComponent {
    fun inject(myApplication: MyApplication)
    fun inject(downloadActivity: DownloadActivity)
}