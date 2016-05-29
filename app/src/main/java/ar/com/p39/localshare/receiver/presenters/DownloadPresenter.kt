package ar.com.p39.localshare.receiver.presenters

import android.net.Uri
import android.util.Log
import ar.com.p39.localshare.common.presenters.Presenter
import ar.com.p39.localshare.receiver.models.DownloadFile
import ar.com.p39.localshare.receiver.models.DownloadList
import ar.com.p39.localshare.receiver.network.SharerClient
import ar.com.p39.localshare.receiver.views.DownloadView
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class DownloadPresenter(val httpClient: OkHttpClient) : Presenter<DownloadView>() {
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
                            list: DownloadList ->
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

    fun startDownload() {
        view()?.disableUi()

        view()?.downloadNextFile()
    }

    fun download(file: DownloadFile) {
        Observable.create(Observable.OnSubscribe<kotlin.ByteArray> { subscriber ->
            val request = Request.Builder().url(file.url).build();
            val response = httpClient.newCall(request).execute();

            val stream: InputStream = response.body().byteStream();
            val input: BufferedInputStream = BufferedInputStream(stream);

            val bytes = input.readBytes()

            subscriber.onNext(bytes)
            subscriber.onCompleted()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            data: ByteArray ->
                                view()?.downloadCompleted(file)
                        },
                        {
                            error ->
                                Log.e("Download", "Connect failed to ${file.url} : $error")
                                view()?.showError("Connect failed to ${file.url}")
                        }
                )
    }
}
