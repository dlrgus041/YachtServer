package util

class Match: Thread() {
    override fun run() = with (U) {
        while (true) {
            while (waitQ.size > 1) {
                val p1 = waitQ.poll()
                val p2 = waitQ.poll()

                p1.opponent = p2
                p2.opponent = p1

                p1.send(2)
                p2.send(3)
            }
        }
    }
}