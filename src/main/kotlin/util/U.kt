package util

import network.Client
import network.Match
import network.Server
import java.net.ServerSocket
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

object U {

    lateinit var pool: ExecutorService
    lateinit var serverSocket: ServerSocket
    val connections = Collections.synchronizedSet(mutableSetOf<Client>())
    val arena = Collections.synchronizedMap(mutableMapOf<Int, Match>())
    val waitQ = LinkedBlockingQueue<Client>(16)
    var id = -1

    fun startServer() {
        try {
            pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
            serverSocket = ServerSocket(52190)
            Server().apply { isDaemon = true }.start()
            FindGame().apply { isDaemon = true }.start()
        } catch (_: Exception) {
            display("서버 포트가 사용중입니다. 프로그램을 중지하고 다른 포트를 사용하세요.")
        }
    }

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

    fun handle(match: Int, player: Int, code: Int) = with (arena[match] ?: throw Exception()) {
        try {
            when (code) {
                in 1..59 -> players[(player + 1) % 2].send(code)
                70, 130 -> {
                    Thread.sleep(1500)
                    players[(player + 1) % 2].send(code)
                    if (turn == 24) {
                        players[0].apply { ready = false }.send(254)
                        players[1].apply { ready = false }.send(254)
                    }
                }
                62, 68 -> {
                    rematch[player] = code - 65
                    if (rematch[0] * rematch[1] > 0 && rematch[0] > 0) {
                        players[0].send(63)
                        players[1].send(63)
                    } else if (rematch[0] * rematch[1] == 0) return@with
                    else {
                        players[0].send(67)
                        players[1].send(67)
                    }
                }
                in 75..100 -> {
                    score[player] = code - 70
                    if (score[0] * score[1] > 0) {
                        Thread.sleep(1500)

                        var s0 = 61
                        var s1 = 69
                        if (score[0] > score[1]) s1 = s0.also { s0 = s1 }

                        players[0].send(s0)
                        players[1].send(s1)
                    } else return@with
                }
                in 111..129 -> {
                    ++turn
                    players[(player + 1) % 2].send(code)
                }
//                200 -> connections.remove(client)
                201 -> {
                    players[(player + 1) % 2].send(201)
                    Thread.sleep(1500)

                    players[0].initialize()
                    players[1].initialize()
                    arena.remove(match)
                }
                251 -> {}
                255 -> {
                    if (isPlaying) {
                        Thread.sleep(1500)
                        players[(player + 1) % 2].send(255)
                    } else {
                        players[player].ready = true
                        if (players[0].ready && players[1].ready) {
                            Thread.sleep(2000)

                            players[0].send(255)
                            players[1].send(255)
                            isPlaying = true
                        }
                    }
                }
                else -> TODO("undefined code")
            }
        } catch (_: Exception) {
            TODO("property 'opponent' is null")
        }
    }
}