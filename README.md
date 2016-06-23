# Local Share


This app aims to make easy to share photos from one device to another connecter over the same
wifi network.

---
>Made with ❤️ by Ricardo Markiewicz [https://twitter.com/gazeria](https://twitter.com/gazeria).

Special thanks to :

* Open source community, you rock :)
* [Artem Zinnatullin](https://twitter.com/artem_zin) for write [Android Development Culture Document](http://artemzin.com/blog/android-development-culture-the-document-qualitymatters/).
* Android community for write a excelente docs about good practices.
* IntelliJ

## Background

Today we have a lot of ways to share information between devices : NFC, Bluetooth, Email, Whatsapp, etc.

* Email : indirect method, need to upload and download the file. On slow connections like my country that take a little bit.
* Whatsapp : quality issues. We loose metadata
* BT & NFC : Most people don't know how to use it. Not all devices works as we expect. On my country only a few devices has NFC.

## My approach

When a user want to share a photo with a friend just select the photos
and press the standart share Intent. After that a window will popup with a QR code.

Another user can use our app to scan that QR, get a list of files and download to his devices with a P2P
embed HTTP server on the source device.

Screenshots:

<img src="/site/Screenshot1.png" width="400"> <img src="/site/Screenshot2.png" width="400"> <img src="/site/Screenshot3.png" width="400">

# TODOs

* Automated Tests :)
* Travis-CI integration
* Improve debug mode developer settings panel
