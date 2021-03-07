package util

enum class Command(val ex: String, val `fun`: Any?) {
    START("서버를 시작합니다.", U.startServer()),
    STOP("서버를 종료합니다.", U.stopServer()),
//    HELP("명령어들과 그 설명을 보여줍니다.", help()),
    LIST("현재 연결되어있는 클라이언트를 모두 보여줍니다.", U.list()),
    COUNT("현재 연결되어있는 클라이언트의 수를 보여줍니다.", U.count())
}