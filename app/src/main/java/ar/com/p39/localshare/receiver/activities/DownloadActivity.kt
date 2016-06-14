package ar.com.p39.localshare.receiver.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import ar.com.p39.localshare.MyApplication
import ar.com.p39.localshare.R
import ar.com.p39.localshare.common.network.WifiSSIDProvider
import ar.com.p39.localshare.receiver.adapters.DownloadFileAdapter
import ar.com.p39.localshare.receiver.models.DownloadFile
import ar.com.p39.localshare.receiver.presenters.DownloadPresenter
import ar.com.p39.localshare.receiver.views.DownloadView
import butterknife.bindView
import com.squareup.picasso.Picasso
import javax.inject.Inject

/**
 * Allow the user to see which files are available to download
 *
 * Created by gazer on 5/1/16.
 */
class DownloadActivity : AppCompatActivity(), DownloadView {
    @Inject
    lateinit var presenter: DownloadPresenter

    @Inject
    lateinit var bssidProvider: WifiSSIDProvider

    @Inject
    lateinit var picasso: Picasso

    val toolbar: Toolbar by bindView(R.id.toolbar)
    val list: RecyclerView by bindView(R.id.list)
    val button: Button by bindView(R.id.download)
    val finish: Button by bindView(R.id.finish)
    val loading: ProgressBar by bindView(R.id.loading)

    private var adapter: DownloadFileAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        MyApplication.graph.inject(this)

        setSupportActionBar(toolbar)

        supportActionBar?.title = getString(R.string.title_activity_download)

        val url = intent.extras.getString("url")

        presenter.bindView(this)
        presenter.inspectUrl(bssidProvider.getBSSID(), url)

        list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        button.setOnClickListener { checkWritePermission() }
        finish.setOnClickListener { finish() }
    }

    private fun checkWritePermission() {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                showError("To be able to save the imagen to your device we need write permission")
            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, permissions, WRITE_EXTERNAL_STORAGE);
            }
        } else {
            presenter.startDownload()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == WRITE_EXTERNAL_STORAGE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                presenter.startDownload()
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                showError("We can start download due lack of permission")
            }
        }
    }

    override fun onDestroy() {
        presenter.unbindView(this)
        super.onDestroy()
    }

    override fun connectoToWifi(ssid: String) {
        Snackbar.make(list, "Need to connecto to $ssid wifi network to continue", Snackbar.LENGTH_SHORT).show()
    }

    override fun showFiles(files: List<DownloadFile>) {
        adapter = DownloadFileAdapter(picasso, files)
        list.adapter = adapter

        loading.visibility = View.GONE
        list.visibility = View.VISIBLE
        button.visibility = View.VISIBLE
    }

    override fun showError(s: String) {
        Snackbar.make(list, s, Snackbar.LENGTH_SHORT).show()
    }

    override fun disableUi() {
        button.isEnabled = false
    }

    override fun downloadStart() {
        adapter?.notifyDataSetChanged()
    }

    override fun downloadCompleted(data: ByteArray) {
        val opt = BitmapFactory.Options();
        val imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.size, opt);

        val url = MediaStore.Images.Media.insertImage(contentResolver, imageBitmap, "", "")

        Log.d("Download", "New url image : $url")

        adapter?.notifyDataSetChanged()
    }

    override fun downloadFinished() {
        Snackbar.make(list, "All files was downloaded!", Snackbar.LENGTH_SHORT).show()
        finish.visibility = View.VISIBLE
        button.visibility = View.GONE
    }

    companion object {
        val WRITE_EXTERNAL_STORAGE = 1
    }
}
