package ar.com.p39.localshare

import ar.com.p39.localshare.receiver.activities.DownloadActivity
import ar.com.p39.localshare.receiver.activities.ScanActivity
import ar.com.p39.localshare.sharer.ShareActivity
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
    fun inject(scanActivity: ScanActivity)
    fun inject(downloadActivity: DownloadActivity)
    fun inject(shareActivity: ShareActivity)
}