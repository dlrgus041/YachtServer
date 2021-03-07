package util

import network.Client
import network.Match
import java.net.ServerSocket
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object U {

    lateinit var pool: ExecutorService
    lateinit var serverSocket: ServerSocket
    val connections = Vector<Client>()
    val arena = Vector<Match>()

    fun startServer() {
        try {
            pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
            serverSocket = ServerSocket(52196)

            pool.submit {
                try {
                    while (true) {
                        display("연결 대기 중입니다.")
                        val client = Client(serverSocket.accept(), arena.size, connections.size % 2)
                        if (connections.size % 2 == 1) arena.add(Match(connections.lastElement(), client))
                        connections.add(client)
                        count()
                    }
                } catch (_: Exception) {
                    if (!serverSocket.isClosed) {
                        stopServer()
                        display("오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
                    }
                }
            }
        } catch (_: Exception) {
            if (!serverSocket.isClosed) {
                stopServer()
                display("오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
            }
        }
    }

    fun stopServer() {
        for (client in connections) client.close()
        connections.clear()
        arena.clear()
        serverSocket.close()
        pool.shutdown()
    }

// fun help() { for (cmd in util.Command.values()) println(cmd.name + " :\t " + cmd.ex)}

    fun list() { for (client in connections) display(client.socket.inetAddress) }

    fun count() = display("현재 ${connections.size}대 연결되었습니다.")

    fun display(str: Any) = println("[${LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss"))}] $str")

    fun handle(num: Int, player: Int, int: Int) = with (arena[num]) {
        when (int) {
            in 1..59 -> {
                if (player == 0) P2.send(int) else P1.send(int)
            }
            in 205..230 -> {
                score[player] = int - 200
                if (score[0] * score[1] > 0) {
                    java.lang.Thread.sleep(2000)
                    var p1 = 65
                    var p2 = 69
                    if (score[0] > score[1]) {
                        p1 = p2.also { p2 = p1 }
                        whoseTurn = true
                    }
                    P1.send(p1)
                    P2.send(p2)
                    clear(true)
                } else return@with
            }
            250 -> {
                ready[player] = true
                if (ready[0] && ready[1]) {
                    java.lang.Thread.sleep(2000)
                    P1.send(250)
                    P2.send(250)
                    clear(false)
                } else return@with
            }
            255 -> {
                if (whoseTurn) P2.send(255)
                else P1.send(255)
                whoseTurn = !whoseTurn
            }
            else -> TODO("error message")
        }
    }
}