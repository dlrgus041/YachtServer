package util

import network.Match

class FindGame: Thread() {
    override fun run() = with (U) {
        while (true) {
            while (waitQ.size > 1) {

                val key = ++id
                val p1 = waitQ.poll().apply { match = key }
                val p2 = waitQ.poll().apply { match = key }

                arena[key] = Match(arrayOf(p1, p2))
            }
        }
    }
}