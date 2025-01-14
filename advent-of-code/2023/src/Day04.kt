import kotlin.math.pow

fun numberOfMatches(line: String): Int {
    val numberParts = line.substringAfter(": ")
    val winningNumbers = numberParts
        .substringBefore(" | ")
        .split(" ")
        .filter(String::isNotEmpty)
        .map(String::toInt)
        .toSet()
    val ourNumbers = numberParts
        .substringAfter(" | ")
        .split(" ")
        .filter(String::isNotEmpty)
        .map(String::toInt)

    return ourNumbers.count { it in winningNumbers }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            2.0.pow(numberOfMatches(line) - 1).toInt()
        }
    }

    fun part2(input: List<String>): Int {
        val quantities = MutableList(input.size) { 1 }

        for (i in quantities.indices) {
            val matches = numberOfMatches(input[i])

            for (j in 1..matches) {
                quantities[i + j] += quantities[i]
            }
        }

        return quantities.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
