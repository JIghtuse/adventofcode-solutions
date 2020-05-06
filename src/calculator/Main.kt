package calculator

import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)

    while (true) {
        val line = scanner.nextLine()

        if (line.isEmpty()) continue

        if (line == "/exit") break

        if (line == "/help") println("The program calculates the sum of numbers")

        val numbers = line.split(" ")
        println(numbers.map { it.toInt() }.sum())
    }

    println("Bye!")
}
