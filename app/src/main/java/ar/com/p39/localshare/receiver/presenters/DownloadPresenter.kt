package ar.com.p39.localshare.presenters

import android.net.Uri
import android.util.Log
import ar.com.p39.localshare.common.presenters.Presenter
import ar.com.p39.localshare.receiver.models.DownloadList
import ar.com.p39.localshare.receiver.network.SharerClient
import ar.com.p39.localshare.receiver.views.DownloadView
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class DownloadPresenter() : Presenter<DownloadView>() {
    fun inspectUrl(bssid: String, url: String) {
        val uri = Uri.parse(url)
        val baseUrl = "http://${uri.host}:${uri.port}/"

        Log.d("Download", "BaseUrl = $baseUrl")

        val retrofit = Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build()

        val client = retrofit.create(SharerClient::class.java)

        client.getShareInfo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    {
                        list : DownloadList ->
                            if (bssid != list.ssid) {
                                view()?.connectoToWifi(list.ssid)
                            } else {
                                view()?.showFiles(list.files)
                            }
                    }, {
                        error ->
                            Log.e("Download", "Connect failed to $url : $error")
                            view()?.showError("Connect failed to $url")
                    }
            )
    }
}
