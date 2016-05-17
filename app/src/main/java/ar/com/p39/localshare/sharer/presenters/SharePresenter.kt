package ar.com.p39.localshare.sharer.presenters

import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import ar.com.p39.localshare.common.presenters.Presenter
import ar.com.p39.localshare.sharer.models.FileShare
import ar.com.p39.localshare.sharer.network.ShareServer
import ar.com.p39.localshare.sharer.views.ShareView

/**
 * Presenter to handle logic in share activity
 *
 * Created by gazer on 3/31/16.
 */
class SharePresenter(val intent: Intent) : Presenter<ShareView>() {
    var bssid: String? = null

    fun checkWifiStatus(connected: Boolean, bssid: String) {
        this.bssid = bssid
        if (connected) {
            prepareData()
        } else {
            view()?.showWifiError()
        }
    }

    private var httpServer: ShareServer? = null

    fun startSharing(ip: String, files: List<FileShare>) {
        stopSharing()
        httpServer = ShareServer(bssid!!, ip, files)
    }

    fun stopSharing() {
        httpServer?.stop()
        httpServer = null
    }

    private fun prepareData() {
        val action = intent.getAction()
        val type = intent.getType()

        // Figure out what to do based on the intent type
        if (Intent.ACTION_SEND == action && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent) // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE == action && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent) // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    internal fun handleSendImage(intent: Intent) {
        if (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) != null) {
            var uri:Uri = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri;
            view()?.showUriData(uri)
        }
    }

    internal fun handleSendMultipleImages(intent: Intent) {
        val imageUris = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
        if (imageUris != null) {
            view()?.showUriData(imageUris)
        }
    }
}
