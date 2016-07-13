package ar.com.p39.localshare.receiver.views

import ar.com.p39.localshare.receiver.models.DownloadFile

/**
 * Created by gazer on 5/1/16.
 */
interface DownloadView {
    fun connectoToWifi(ssid: String)

    fun showFiles(files: List<DownloadFile>)

    fun showError(s: String)

    fun disableUi()

    fun downloadStart()

    fun downloadCompleted(data: ByteArray)

    fun downloadFinished()

    fun showInvalidUrlError()
}