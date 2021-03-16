package network

class Match(val players: Array<Client>) {

    init {
        for (i in 0 .. 1) {
            with(players[i]) {
                send(101 + i, players[(i + 1) % 2].name)
                player = i
            }
        }
    }

    val score = Array(2) {0}
    var isPlaying = false
    var turn = 0
    val rematch = Array(2) {0}

}