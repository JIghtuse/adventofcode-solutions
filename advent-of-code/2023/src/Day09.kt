fun diffSeries(values: List<Int>) =
    values.windowed(2).map{ it[1] - it[0] }

fun nextValue(values: List<Int>): Int {
    val allSeries = mutableListOf(values)

    var currentValues = values
    while (!currentValues.all{ it == 0 }) {
        currentValues = diffSeries(currentValues)
        allSeries.add(currentValues)
    }

    return allSeries.reversed().sumOf { it.last() }
}

fun previousValue(values: List<Int>): Int {
    val allSeries = mutableListOf(values)

    var currentValues = values
    while (!currentValues.all{ it == 0 }) {
        currentValues = diffSeries(currentValues)
        allSeries.add(currentValues)
    }

    return allSeries.reversed().map{ it.first() }.reduce { acc, i -> i - acc }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map(::toInts)
            .sumOf(::nextValue)
    }

    fun part2(input: List<String>): Int {
        return input
            .map(::toInts)
            .sumOf(::previousValue)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
