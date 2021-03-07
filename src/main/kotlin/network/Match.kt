package network

data class Match(val P1: Client, val P2: Client) {

    init {
        P1.send(0)
        P2.send(0)
    }

    val ready = Array(2) {false}
    val score = Array(2) {0}
    var whoseTurn = false // true = P1 turn, false = P2 turn

    fun clear(flag: Boolean) {
        if (flag) {
            score[0] = 0
            score[1] = 0
        } else {
            ready[0] = false
            ready[1] = false
        }
    }
}