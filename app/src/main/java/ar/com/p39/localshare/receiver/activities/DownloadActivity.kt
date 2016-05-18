package ar.com.p39.localshare.receiver.activities

import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import ar.com.p39.localshare.R
import ar.com.p39.localshare.presenters.DownloadPresenter
import ar.com.p39.localshare.MyApplication
import ar.com.p39.localshare.receiver.adapters.DownloadFileAdapter
import ar.com.p39.localshare.receiver.models.DownloadFile
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
    lateinit var wifiManager: WifiManager

    @Inject
    lateinit var picasso: Picasso

    val list: RecyclerView by bindView(R.id.list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        MyApplication.graph.inject(this)

        val info = wifiManager.connectionInfo;
        val url = intent.extras.getString("url")

        presenter.bindView(this)
        presenter.inspectUrl(info.bssid, url)

        list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun onDestroy() {
        presenter.unbindView(this)
        super.onDestroy()
    }
    override fun connectoToWifi(ssid: String) {
        Snackbar.make(list, "Need to connecto to $ssid wifi network to continue", Snackbar.LENGTH_SHORT).show()
    }

    override fun showFiles(files: List<DownloadFile>) {
        list.adapter = DownloadFileAdapter(picasso, files)
    }

    override fun showError(s: String) {
        Snackbar.make(list, s, Snackbar.LENGTH_SHORT).show()
    }
}
