import util.Match
import util.U

fun main() = with (U) {
    Match().also { it.isDaemon = true }.start()
    while (true) {
        print(">> ")
        when (readLine()?.toLowerCase()) {
            "start" -> startServer()
            "stop" -> stopServer()
            "count" -> count()
            "list" -> list()
            "" -> { }
            else -> println("잘못 입력하셨습니다. 'help'를 입력하면 도움말을 볼 수 있습니다.")
//            "help" -> help()
//       }
//           try {
//            util.Command.valueOf(readLine()!!.toUpperCase()).`fun`
//        } catch (_: Exception) {
//            println("잘못 입력하셨습니다. 'help'를 입력하면 도움말을 볼 수 있습니다.")
        }
    }
}