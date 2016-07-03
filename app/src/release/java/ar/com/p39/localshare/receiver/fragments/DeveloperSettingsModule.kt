package ar.com.p39.localshare.receiver.fragments

import android.view.View
import ar.com.p39.localshare.common.ui.ViewModifier
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class DeveloperSettingsModule {
    class NoOpViewModifier : ViewModifier {
        override fun <T : View> modify(view: T): T {
            return view
        }
    }

    @Provides @Named(SCAN_ACTIVITY_VIEW_MODIFIER)
    fun provideViewModifier(): ViewModifier {
        return NoOpViewModifier()
    }

    companion object {
        const val SCAN_ACTIVITY_VIEW_MODIFIER = "scan_activity_view_modifier"
    }
}