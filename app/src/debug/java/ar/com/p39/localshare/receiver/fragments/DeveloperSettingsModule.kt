package ar.com.p39.localshare.receiver.fragments

import ar.com.p39.localshare.common.ui.ViewModifier
import ar.com.p39.localshare.receiver.ui.ScanViewModifier
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class DeveloperSettingsModule {
    @Provides @Named(SCAN_ACTIVITY_VIEW_MODIFIER)
    fun provideScanActivityViewModifier(): ViewModifier {
        return ScanViewModifier()
    }

    companion object {
        const val SCAN_ACTIVITY_VIEW_MODIFIER = "SCAN_ACTIVITY_VIEW_MODIFIER"
    }
}