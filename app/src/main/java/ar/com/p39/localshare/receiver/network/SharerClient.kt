package ar.com.p39.localshare.receiver.network

import ar.com.p39.localshare.receiver.models.DownloadList
import retrofit2.http.GET
import rx.Observable

/**
 * Created by gazer on 5/14/16.
 */
interface SharerClient {
    @GET("/sharer?shareInfo=1")
    fun getShareInfo() : Observable<DownloadList>
}