package ar.com.p39.localshare.sharer.network

import java.io.IOException

import ar.com.p39.localshare.sharer.models.FileShare
import fi.iki.elonen.NanoHTTPD

/**
 * Handle HTTP Server stuff
 *
 * Provide interface fot sharing thinks over http.
 *
 * Created by gazer on 3/31/16.
 */
class ShareServer @Throws(IOException::class)
    constructor(val bssid:String, val ip: String, val files: List<FileShare>) : NanoHTTPD(ip, 8080) {

    init {
        start()
    }

    override fun serve(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        var path = session.uri

        if ("/sharer".equals(path)) {
            return doSharer(session)
        } else if (path.startsWith("/get")) {
            var index = path.split("/").last().toInt()
            return doFile(index)
        }

        return do404()
    }

    private fun doFile(index: Int): Response {
        if (index >= files.size) {
            return do404()
        } else {
            var file = files[index]
            return Response(Response.Status.OK, file.contentType, file.stream)
        }
    }

    private fun do404(): Response {
        return NanoHTTPD.Response("Not Found")
    }

    fun doSharer(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val params = session.parms
        if (params["shareInfo"] != null) {
            var files = files.map { it.toJson() }.joinToString(",", "[", "]")

            var body = "{\"ssid\": \"$bssid\", \"files\": $files}"
            val r = NanoHTTPD.Response(body)
            r.addHeader("Content-Type", "application/json")
            return r
        } else {
            return redirectToGooglePlay()
        }
    }

    private fun redirectToGooglePlay(): NanoHTTPD.Response {
        return NanoHTTPD.Response("Redirect")
    }
}
