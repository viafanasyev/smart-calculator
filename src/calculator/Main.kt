package calculator

fun main() {
    val calculator = Calculator()

    var command: String
    while (true) {
        command = readLine()!!
        if (command.isNotEmpty()) {
            if (command.first() == '/') {
                if (command == "/exit") {
                    break
                } else if (command == "/help") {
                    println("The program calculates mathematical expressions")
                } else {
                    println("Unknown command")
                }
            } else {
                calculator.processCommand(command)
            }
        }
    }
    println("Bye!")
}
