package ar.com.p39.localshare.receiver.models

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by gazer on 5/14/16.
 */
data class DownloadList(@JsonProperty("ssid") var ssid:String, @JsonProperty("files") var files:List<DownloadFile>) {
}

data class DownloadFile(@JsonProperty("name") var name:String, @JsonProperty("size") var site:String, @JsonProperty("contentType") var contentType:String) {
}
