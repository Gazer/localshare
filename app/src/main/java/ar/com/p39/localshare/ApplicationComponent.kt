package ar.com.p39.localshare

import ar.com.p39.localshare.common.network.NetworkModule
import ar.com.p39.localshare.receiver.activities.DownloadActivity
import ar.com.p39.localshare.receiver.activities.ScanActivity
import ar.com.p39.localshare.receiver.fragments.DeveloperSettingsComponent
import ar.com.p39.localshare.receiver.fragments.DeveloperSettingsModule
import ar.com.p39.localshare.sharer.ShareActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Main DI module
 *
 * Created by gazer on 5/16/16.
 */
@Singleton
@Component(modules = arrayOf(AndroidModule::class, NetworkModule::class, DeveloperSettingsModule::class))
interface ApplicationComponent {
    fun inject(myApplication: MyApplication)
    fun inject(scanActivity: ScanActivity)

    fun plusDeveloperSettingsComponent(): DeveloperSettingsComponent

    fun plus(shareActivityModule: ShareActivity.ShareActivityModule): ShareActivity.ShareActivityComponent
    fun plus(downloadModule: DownloadActivity.DownloadModule): DownloadActivity.DownloadComponent
}