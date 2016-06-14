package ar.com.p39.localshare.sharer

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Parcelable
import android.provider.OpenableColumns
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.format.Formatter
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import ar.com.p39.localshare.MyApplication

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix

import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder
import java.util.ArrayList
import java.util.EnumMap

import ar.com.p39.localshare.R
import ar.com.p39.localshare.common.IpAddress
import ar.com.p39.localshare.common.ui.QRImageView
import ar.com.p39.localshare.common.network.WifiSSIDProvider
import ar.com.p39.localshare.sharer.models.FileShare
import ar.com.p39.localshare.sharer.presenters.SharePresenter
import ar.com.p39.localshare.sharer.views.ShareView
import butterknife.bindView
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Inject
import javax.inject.Singleton

class ShareActivity : AppCompatActivity(), ShareView {
    val dataView: TextView by bindView(R.id.files)
    val qr: QRImageView by bindView(R.id.qr)
    val help: TextView by bindView(R.id.help)

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

        presenter.bindView(this)
        presenter.checkWifiStatus(bssidProvider.isConnected(), bssidProvider.getBSSID())
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

            dataView.text =
                    "Url : ${"http://$ip:8080/sharer"}" +
                    "Files : ${files.size}\n" +
                    "Size : ${totalSize.format(2)} MB"
        } else {
            showWifiError()
        }
    }

    override fun showWifiError() {
        Snackbar.make(dataView, "Please connecto to wifi", Snackbar.LENGTH_INDEFINITE).show()
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
