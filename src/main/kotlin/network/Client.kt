package network

import util.U
import java.io.IOException
import java.net.Socket

class Client(val socket: Socket) {

    private val `in` by lazy { socket.getInputStream() }
    private val out by lazy { socket.getOutputStream() }

    private val name by lazy {
        val byte = ByteArray(1024)
        when (val cnt = `in`.read(byte)) {
            -1 -> throw IOException()
            else -> byte.decodeToString(0, cnt)
        }
    }

    var opponent: Client? = null
    var ready = false
    var isPlaying = false
    var total = -1

    init {
        with (U) {
            try {
                display("$name${socket.inetAddress}와 연결되었습니다.")
                pool.submit {
                    try {
                        while (true) {
                            when (val int = `in`.read()) {
                                -1 -> throw IOException()
                                else -> {
                                    display(int, name)
                                    handle(this@Client, int)
                                }
                            }
                        }
                    } catch (_: Exception) {
                        display("오류가 발생하여 연결을 종료합니다.", name)
                        close()
//                        handle(this@Client, 201)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun initialize() {
        opponent = null
        ready = false
        isPlaying = false
        total = -1
    }

    fun send(i: Int) = U.pool.submit { out.write(i) }

    fun close() = U.pool.submit {
        `in`.close()
        out.close()
    }
}