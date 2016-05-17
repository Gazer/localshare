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
import ar.com.p39.localshare.sharer.models.FileShare
import ar.com.p39.localshare.sharer.presenters.SharePresenter
import ar.com.p39.localshare.sharer.views.ShareView

class ShareActivity : AppCompatActivity(), ShareView {

    private var dataView: TextView? = null
    private var qr: QRImageView? = null

    internal var presenter: SharePresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)

        dataView = findViewById(R.id.files) as TextView?
        qr = findViewById(R.id.qr) as QRImageView?

        presenter = SharePresenter(intent)
        presenter!!.bindView(this)

        readWifiStatus()
    }

    private fun readWifiStatus() {
        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

        var wifiManager = getSystemService (Context.WIFI_SERVICE) as WifiManager;
        var info = wifiManager.getConnectionInfo ();

        presenter!!.checkWifiStatus(wifi.isConnected, info.bssid)
    }

    protected fun wifiIpAddress(context: Context): String {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var ipAddress = wifiManager.connectionInfo.ipAddress

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            ipAddress = Integer.reverseBytes(ipAddress)
        }

        val ipByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()

        val ipAddressString: String?
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).hostAddress
        } catch (ex: UnknownHostException) {
            Log.e("WIFIIP", "Unable to get host address.")
            ipAddressString = null
        }

        return ipAddressString ?: ""
    }

    override fun showUriData(uri: Uri) {
        val files = ArrayList<FileShare>()

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
            returnCursor.close()

            shareFiles(files)
        } catch (e: IOException) {
        }
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
                returnCursor.close()

            } catch (e: IOException) {
            }

        }
        shareFiles(files)
    }

    fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

    private fun shareFiles(files: List<FileShare>) {
        val ip = wifiIpAddress(this)

        presenter!!.startSharing(ip, files)

        qr!!.setData("http://$ip:8080/sharer")

        val totalSize :Double = files.map { it.size }.sum() / (1024*1024.0)

        dataView!!.text =
                "Files : ${files.size}\n" +
                "Size : ${totalSize.format(2)} MB"
    }

    override fun showWifiError() {
        if (dataView != null) {
            Snackbar.make(dataView as TextView, "Please connecto to wifi", Snackbar.LENGTH_INDEFINITE).show()
        }
    }

    override fun onStop() {
        super.onStop()
        presenter!!.stopSharing()
    }
}
