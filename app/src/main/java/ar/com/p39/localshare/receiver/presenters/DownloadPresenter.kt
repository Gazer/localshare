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
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.BufferedInputStream
import java.io.InputStream

class DownloadPresenter(val httpClient: OkHttpClient) : Presenter<DownloadView>() {
    lateinit var downloadFiles: DownloadList

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
                                downloadFiles = list
                                view()?.showFiles(list.files)
                            }
                        },
                        {
                            error ->
                            Log.e("Download", "Connect failed to $url : $error")
                            view()?.showError("Connect failed to $url")
                        }
                )
    }

    fun startDownload() {
        view()?.disableUi()

        Observable.just((downloadFiles as DownloadList).files)
                .flatMapIterable { it }
                .flatMap { file ->
                    file.status = 1
                    view()?.downloadStart()
                    downloadFileObserver(file)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            file: DownloadFile ->
                            if (file.data != null) {
                                Log.d("Download", "Completed")
                                file.status = 2
                                view()?.downloadCompleted(file.data as ByteArray)
                            }
                        },
                        {
                            error ->
                            Log.e("Download", "Connect failed to url : $error")
                            view()?.showError("Connect failed to url")
                        },
                        {
                            view()?.downloadFinished()
                        }
                )
    }

    private fun downloadFileObserver(file: DownloadFile): Observable<DownloadFile> {
        return Observable.defer {
            Observable.create(Observable.OnSubscribe<DownloadFile> { subscriber ->
                Log.d("Download", "Map : $file")
                val request = Request.Builder().url(file.url).build();
                val response = httpClient.newCall(request).execute();

                val stream: InputStream = response.body().byteStream();
                val input: BufferedInputStream = BufferedInputStream(stream);

                file.data = input.readBytes()

                subscriber.onNext(file)
                subscriber.onCompleted()
                stream.close()
                input.close()
            }).subscribeOn(Schedulers.io())
        }
    }
}
