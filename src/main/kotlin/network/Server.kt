package network

import util.U
import java.net.SocketException

class Server: Thread() {
    override fun run() = with (U) {
        try {
            while (true) {
                display("연결 대기 중입니다...")
                val client = Client(serverSocket.accept())
                connections.add(client)
                waitQ.put(client)
                count()
            }
        } catch (_: SocketException){
            if (!serverSocket.isClosed) stopServer()
            display("서버가 종료되었습니다.")
        } catch (_: Exception) {
            if (!serverSocket.isClosed) stopServer()
            display("오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
    }
}