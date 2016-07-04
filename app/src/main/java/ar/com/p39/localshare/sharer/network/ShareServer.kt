package ar.com.p39.localshare.sharer.network

import android.content.ContentResolver
import android.util.Log
import java.io.IOException

import ar.com.p39.localshare.sharer.models.FileShare
import fi.iki.elonen.NanoHTTPD
import java.util.concurrent.Executors

/**
 * Handle HTTP Server stuff
 *
 * Provide interface fot sharing thinks over http.
 *
 * Created by gazer on 3/31/16.
 */
class ShareServer @Throws(IOException::class)
    constructor(val contentResolver: ContentResolver, val bssid:String, val ip: String, val files: List<FileShare>) : NanoHTTPD(ip, 8080) {

    init {
        setAsyncRunner(BoundRunner(Executors.newFixedThreadPool(5)));
        Log.d("SERVER", "Publishing URL : http://$ip:8080/sharer")
        start()
    }

    override fun serve(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val path = session.uri

        if ("/sharer".equals(path)) {
            return doSharer(session)
        } else if (path.startsWith("/get")) {
            val index = path.split("/").last().toInt()
            return doFile(index)
        }

        return do404()
    }

    private fun doFile(index: Int): Response {
        if (index >= files.size) {
            return do404()
        } else {
            val file = files[index]
            val stream = contentResolver.openInputStream(file.uri)
            return newChunkedResponse(Response.Status.OK, file.contentType, stream)
        }
    }

    private fun do404(): Response {
        return NanoHTTPD.newFixedLengthResponse("Not Found")
    }

    fun doSharer(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val params = session.parms
        if (params["shareInfo"] != null) {
            val files = files.mapIndexed {
                index, it -> """{"name": "${it.name}", "size": ${it.size}, "contentType": "${it.contentType}", "url": "http://$ip:8080/get/$index"}"""
            }.joinToString(",", "[", "]")

            val body = "{\"ssid\": \"$bssid\", \"files\": $files}"
            val r = NanoHTTPD.newFixedLengthResponse(body)
            r.addHeader("Content-Type", "application/json")
            return r
        } else {
            return redirectToGooglePlay()
        }
    }

    private fun redirectToGooglePlay(): NanoHTTPD.Response {
        return NanoHTTPD.newFixedLengthResponse("Redirect")
    }
}
