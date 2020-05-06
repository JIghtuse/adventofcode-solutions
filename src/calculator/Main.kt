package calculator

fun getTokens(line: String) = line.split(" ").filter { it.isNotEmpty() }

fun isPlus(token: String): Boolean {
    val isPlusByMinuses = token.length % 2 == 0 && token.all { it == '-' }
    val isPlusChars = token.all { it == '+' }
    return isPlusChars || isPlusByMinuses
}

fun isMinus(token: String): Boolean = token.all { it == '-' }

fun getOperation(token: String): Int.(Int) -> Int {
    if (isPlus(token)) {
        return Int::plus
    } else if (isMinus(token)) {
        return Int::minus
    } else {
        throw Exception("Invalid expression")
    }
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
    while (true) {
        val line = readLine()!!

        if (line.isEmpty()) continue

        if (line.first() == '/') {
            val command = line.substring(1)
            if (command == "exit") break
            if (command == "help") printHelp()
            println("Unknown command")
        } else {
            try {
                println(parseAndCalculate(line))
            } catch (_: java.lang.Exception) {
                println("Invalid expression")
            }
        }
    }

    println("Bye!")
}
