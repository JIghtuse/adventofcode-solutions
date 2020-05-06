package calculator

import java.util.Scanner

fun getTokens(line: String) = line.split(" ").filter { it.isNotEmpty() }

fun isPlus(token: String): Boolean {
    val isPlusByMinuses = token.length % 2 == 0 && token.all { it == '-' }
    val isPlusChars = token.all { it == '+' }
    return isPlusChars || isPlusByMinuses
}

fun getOperation(token: String): Int.(Int) -> Int {
    return if (isPlus(token)) Int::plus else Int::minus
}

fun parseAndCalculate(line: String): Int {
    val tokens = getTokens(line)

    var result = 0
    var activeOperation: Int.(Int) -> Int = Int::plus

    for (tokenIndex in tokens.indices) {
        val isNumberIndex = tokenIndex % 2 == 0
        val token = tokens[tokenIndex]
        if (isNumberIndex) {
            val n = token.toInt()
            result = activeOperation(result, n)
        } else {
            activeOperation = getOperation(token)
        }
    }
    return result
}

fun printHelp() {
    println("The program calculates simple operations on numbers")
    println("You can input numbers with + and - between them which you want to add/subtract")
}

fun main() {
    val scanner = Scanner(System.`in`)

    while (true) {
        val line = scanner.nextLine()

        if (line.isEmpty()) continue

        if (line == "/exit") break

        if (line == "/help") printHelp()

        println(parseAndCalculate(line))
    }

    println("Bye!")
}
