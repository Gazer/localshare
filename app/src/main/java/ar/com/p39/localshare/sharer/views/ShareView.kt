package ar.com.p39.localshare.sharer.views

import android.net.Uri

interface ShareView {
    fun showWifiError()

    fun showUriData(uri: Uri)
    fun showUriData(uri: List<Uri>)
}
