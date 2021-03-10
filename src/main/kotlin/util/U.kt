package util

import network.Client
import network.Server
import java.net.ServerSocket
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.SynchronousQueue
import kotlin.concurrent.thread

object U {

    val pool by lazy { Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()) }
    val serverSocket by lazy { ServerSocket(52190) }
    val connections = Collections.synchronizedSet(mutableSetOf<Client>())
    val waitQ = LinkedBlockingQueue<Client>(16)

    fun startServer() = Server().also { it.isDaemon = true }.start()

    fun stopServer() {
        for (client in connections) client.close()
        connections.clear()
        waitQ.clear()
        serverSocket.close()
        pool.shutdown()
    }

// fun help() { for (cmd in util.Command.values()) println(cmd.name + " :\t " + cmd.ex)}

    fun list() { for (client in connections) display(client.socket.inetAddress) }

    fun count() = display("현재 ${connections.size}대 연결되었습니다.")

    fun display(str: Any, name : String? = null) =
        println("[${LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss"))}] " +
                (if (name != null) "$name : " else "") + "$str")

    fun handle(client: Client, code: Int) = with (client) {
        try {
            when (code) {
                in 1..59 -> opponent?.send(code)
                70 -> {
                    if (isPlaying) {
                        opponent?.send(70)
                    } else {
                        ready = true
                        if (ready && opponent?.ready == true) {
                            Thread.sleep(2000)

                            send(70)
                            isPlaying = true

                            opponent?.send(70)
                            opponent?.isPlaying = true
                        }
                    }
                }
                in 75..100 -> {
                    total = code - 70
                    if (total * opponent!!.total > 0) {
                        Thread.sleep(2000)

                        var p1 = 65
                        var p2 = 69
                        if (total > opponent!!.total) p1 = p2.also { p2 = p1 }

                        send(p1)
                        opponent?.send(p2)
                    } else return@with
                }
                200 -> {
                    opponent?.send(200)
                    Thread.sleep(2000)

                    opponent?.initialize()
                    initialize()
                }
//                201 -> connections.remove(client)
                else -> TODO("undefined code")
            }
        } catch (_: Exception) {
            TODO("property 'opponent' is null")
        }
    }
}