package ar.com.p39.localshare.sharer

import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.OpenableColumns
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import ar.com.p39.localshare.BuildConfig
import ar.com.p39.localshare.MyApplication
import ar.com.p39.localshare.R
import ar.com.p39.localshare.common.IpAddress
import ar.com.p39.localshare.common.network.WifiSSIDProvider
import ar.com.p39.localshare.common.ui.QRImageView
import ar.com.p39.localshare.sharer.models.FileShare
import ar.com.p39.localshare.sharer.presenters.SharePresenter
import ar.com.p39.localshare.sharer.views.ShareView
import butterknife.bindView
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import java.io.IOException
import java.util.*
import javax.inject.Inject

class ShareActivity : AppCompatActivity(), ShareView {
    val qr: QRImageView by bindView(R.id.qr)
    val help: TextView by bindView(R.id.help)
    val helpWifi: TextView by bindView(R.id.help_wifi)

    @Inject
    lateinit var presenter: SharePresenter

    @Inject
    lateinit var bssidProvider: WifiSSIDProvider

    @Inject
    lateinit var wifiManager: WifiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        MyApplication.graph.plus(ShareActivityModule(intent)).inject(this)

        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)

        supportActionBar?.title = getString(R.string.title_activity_share)

        presenter.bindView(this)
        presenter.checkWifiStatus(bssidProvider.isConnected(), bssidProvider.getBSSID())

        helpWifi.text = getString(R.string.help_network, bssidProvider.getBSSID())
    }


    override fun showUriData(uri: Uri) {
        showUriData(listOf(uri))
    }

    override fun showUriData(uris: List<Uri>) {
        val files = ArrayList<FileShare>()

        for (uri in uris) {
            val returnCursor = contentResolver.query(uri, null, null, null, null)

            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
            returnCursor.moveToFirst()


            try {
                val share = FileShare(
                        returnCursor.getString(nameIndex),
                        returnCursor.getLong(sizeIndex),
                        contentResolver.getType(uri),
                        contentResolver.openInputStream(uri)
                )
                files.add(share)
            } catch (e: IOException) {
            } finally {
                returnCursor.close()
            }
        }
        shareFiles(files)
    }

    fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

    private fun shareFiles(files: List<FileShare>) {
        val ip = wifiManager.IpAddress()

        if (ip != null) {
            presenter.startSharing(ip, files)

            qr.setData("http://$ip:8080/sharer")

            val totalSize: Double = files.map { it.size }.sum() / (1024 * 1024.0)

            trackFiles(files.size)
        } else {
            showWifiError()
        }
    }

    private fun trackFiles(size: Int) {
        // TODO : DI!
        if (!BuildConfig.DEBUG) {
            Answers.getInstance().logCustom(CustomEvent("Files Shared").putCustomAttribute("Count", size));
        }
    }

    override fun showWifiError() {
        Snackbar.make(help, R.string.connect_to_wifi    , Snackbar.LENGTH_INDEFINITE).show()
        help.visibility = View.GONE
        qr.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        presenter.stopSharing()
    }

    @Subcomponent(modules = arrayOf(ShareActivityModule::class))
    interface ShareActivityComponent {
        fun inject(shareActivity: ShareActivity);
    }

    @Module
    class ShareActivityModule(private var intent: Intent) {
        @Provides
        fun provideSharePresenter(): SharePresenter {
            return SharePresenter(intent)
        }
    }
}
