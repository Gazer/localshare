package ar.com.p39.localshare.receiver.models

/**
 * Basic models for
 * Created by gazer on 5/14/16.
 */
data class DownloadList(var ssid:String, var files:List<DownloadFile>) {}

data class DownloadFile(var name:String, var site:String, var contentType:String) {}
