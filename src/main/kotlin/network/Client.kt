package network

import util.U
import java.io.IOException
import java.net.Socket

class Client(val socket: Socket, private val num: Int, private val player: Int) {

    private val `in` by lazy { socket.getInputStream() }
    private val out by lazy { socket.getOutputStream() }

    private val name by lazy {
        val byte = ByteArray(1024)
        when (val cnt = `in`.read(byte)) {
            -1 -> throw IOException()
            else -> byte.decodeToString(0, cnt)
        }
    }

    init {
        try {
            U.display("$name${socket.inetAddress}와 연결되었습니다.")
            U.pool.submit {
                try {
                    while (true) {
                        when (val int = `in`.read()) {
                            -1 -> throw IOException()
                            else -> {
                                U.display("$name : $int")
                                U.handle(num, player, int)
                            }
                        }
                    }
                } catch (_: Exception) {
                    TODO("error dialog")
                }
            }
        } catch (_: Exception) {
            if (!U.serverSocket.isClosed) close()
        }
    }

    fun send(i: Int) = U.pool.submit { out.write(i) }

    fun close() = U.pool.submit {
        `in`.close()
        out.close()
    }
}