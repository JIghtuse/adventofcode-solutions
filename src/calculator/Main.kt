package calculator

import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)

    while (true) {
        val line = scanner.nextLine()

        if (line == "/exit") {
            break
        }

        if (line.isEmpty()) {
            continue
        }

        var sum = 0
        for (i in line.split(" ")) {
            sum += i.toInt()
        }

        println(sum)
    }

    println("Bye!")
}
