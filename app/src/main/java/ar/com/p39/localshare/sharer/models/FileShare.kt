package ar.com.p39.localshare.sharer.models

import java.io.InputStream


data class FileShare(val name:String, val size:Long, val contentType: String, val stream: InputStream) {
}
