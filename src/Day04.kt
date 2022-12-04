import java.lang.IllegalArgumentException

fun main() {
    val inputLineRegex = """(\d+)-(\d+),(\d+)-(\d+)""".toRegex()

    fun parseRanges(line: String): Pair<IntRange, IntRange> {
        val (aStart, aEnd, bStart, bEnd) = inputLineRegex
            .matchEntire(line)
            ?.destructured
                ?: throw IllegalArgumentException("Incorrect input line $line")

        val a = (aStart.toInt()..aEnd.toInt())
        val b = (bStart.toInt()..bEnd.toInt())
        return a to b
    }

    fun part1(input: List<String>): Int {
        return input.map(::parseRanges).filter { (a, b) ->
            a.all { it in b } || b.all { it in a }
        }.size
    }

    fun part2(input: List<String>): Int {
        return input.map(::parseRanges).filter { (a, b) ->
            a.any { it in b } || b.any { it in a }
        }.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}