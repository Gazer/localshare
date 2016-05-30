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
import java.util.concurrent.TimeUnit

class DownloadPresenter(val httpClient: OkHttpClient) : Presenter<DownloadView>() {
    var files: DownloadList? = null

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
                                files = list
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

        if (files != null) {
            downloadNextFile()
        }
    }

    private fun downloadNextFile() {
        val currentFile = nextFile((files as DownloadList).files)
        if (currentFile != null) {
            currentFile.status = 1
            view()?.downloadStart()
            download(currentFile)
        } else {
            view()?.downloadFinished()
        }
    }

    private fun nextFile(files: List<DownloadFile>): DownloadFile? {
        for (file in files) {
            if (file.status == 0) {
                return file
            }
        }

        return null
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
                .delay(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            data: ByteArray ->
                            file.status = 2
                            view()?.downloadCompleted(data)
                            downloadNextFile()
                        },
                        {
                            error ->
                            Log.e("Download", "Connect failed to ${file.url} : $error")
                            view()?.showError("Connect failed to ${file.url}")
                        }
                )
    }
}
