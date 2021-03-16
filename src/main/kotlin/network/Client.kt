package network

import util.U
import java.net.Socket

class Client(val socket: Socket) {

    private val `in` by lazy { socket.getInputStream() }
    private val out by lazy { socket.getOutputStream() }

    val name by lazy {
        val byte = ByteArray(1024)
        when (val cnt = `in`.read(byte)) {
            -1 -> throw Exception()
            else -> byte.decodeToString(0, cnt)
        }
    }

    var match = -1
    var player = -1
    var ready = false

    init {
        with (U) {
            display("$name${socket.inetAddress}와 연결되었습니다.")
            pool.submit {
                try {
                    while (true) {
                        when (val int = `in`.read()) {
                            -1 -> throw Exception()
                            else -> {
                                display(int, name)
                                handle(match, player, int)
                            }
                        }
                    }
                } catch (_: Exception) {
                    close().get()
                    display("오류가 발생하여 연결을 종료합니다.", name)
                }
            }
        }
    }

    fun initialize() {
        match = -1
        player = -1
        ready = false
    }

    fun send(i: Int, str: String? = null) = U.pool.submit {
        out.write(i)
        if (str != null) out.write(str.encodeToByteArray())
    }

    fun close() = U.pool.submit {
        U.connections.remove(this)
        U.waitQ.remove(this)
        U.count()
        `in`.close()
        out.close()
    }
}