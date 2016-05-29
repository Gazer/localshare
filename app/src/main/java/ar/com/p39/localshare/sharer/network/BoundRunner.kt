package ar.com.p39.localshare.sharer.network

import fi.iki.elonen.NanoHTTPD
import java.util.*
import java.util.concurrent.ExecutorService

/**
 * Runner used to limit the concurrency of the NanoHTTPD server
 *
 * Created by gazer on 5/18/16.
 */
class BoundRunner(val executorService: ExecutorService): NanoHTTPD.AsyncRunner {

    private val running = Collections.synchronizedList(ArrayList<NanoHTTPD.ClientHandler>());


    override fun closeAll() = ArrayList<NanoHTTPD.ClientHandler>(running).forEach {
        it.close()
    }

    override fun closed(clientHandler: NanoHTTPD.ClientHandler?) {
        running.remove(clientHandler)
    }

    override fun exec(code: NanoHTTPD.ClientHandler?) {
        executorService.submit(code)
        running.add(code)
    }
}