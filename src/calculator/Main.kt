package calculator

import java.lang.NumberFormatException

fun getTokens(line: String) = line.split(" ").filter { it.isNotEmpty() }

// operations

fun isPlusOperation(token: String): Boolean {
    val isPlusByMinuses = token.length % 2 == 0 && token.all { it == '-' }
    val isPlusChars = token.all { it == '+' }
    return isPlusChars || isPlusByMinuses
}

fun isMinusOperation(token: String): Boolean = token.all { it == '-' }

fun getOperation(token: String): Int.(Int) -> Int {
    if (isPlusOperation(token)) {
        return Int::plus
    } else if (isMinusOperation(token)) {
        return Int::minus
    } else {
        throw Exception("Invalid expression")
    }
}

// variables

fun isAssignment(s: String) = '=' in s

fun isValidVariableName(s: String) = s.all{ it.isLetter() }

fun parseAndAssign(line: String, variables: MutableMap<String, Int>) {
    val parts = line.split('=')
    if (parts.size != 2) throw Exception("Invalid assignment")

    val namePart = parts[0].trim()
    if (!isValidVariableName(namePart)) throw Exception("Invalid identifier")

    variables[namePart] = getValue(variables, parts[1].trim(), "Invalid assignment")
}

fun getVariableValue(activeVariables: MutableMap<String, Int>, variableName: String): Int {
    val rightHandSideValue = activeVariables[variableName]
    if (rightHandSideValue != null) {
        return rightHandSideValue
    } else {
        throw Exception("Unknown variable")
    }
}

fun getValue(activeVariables: MutableMap<String, Int>, expression: String, invalidNameMessage: String): Int {
    return if (isValidVariableName(expression)) {
        getVariableValue(activeVariables, expression)
    } else if (expression.any { it.isLetter() }) {
        throw Exception(invalidNameMessage)
    } else {
        expression.toInt()
    }
}

// calculating single expression

fun parseAndCalculate(line: String, activeVariables: MutableMap<String, Int>): Int {
    val tokens = getTokens(line)

    var result = 0
    var activeOperation: Int.(Int) -> Int = Int::plus

    for (tokenIndex in tokens.indices) {
        val isValueIndex = tokenIndex % 2 == 0
        val token = tokens[tokenIndex]

        if (isValueIndex) {
            val value = getValue(activeVariables, token, "Invalid expression")
            result = activeOperation(result, value)
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
    val activeVariables = mutableMapOf<String, Int>()

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
                if (isAssignment(line)) {
                    parseAndAssign(line, activeVariables)
                } else {
                    val result = parseAndCalculate(line, activeVariables)
                    println(result)
                }
            } catch(e: NumberFormatException) {
                println("Invalid expression")
            } catch (e: java.lang.Exception) {
                println(e.message)
            }
        }
    }

    println("Bye!")
}
