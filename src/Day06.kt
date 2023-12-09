fun countWaysToWin(bestResult: Pair<Long, Long>): Int {
    val (bestTime, bestDistance) = bestResult

    return (1..<bestTime).count { holdTime ->
        val rideTime = bestTime - holdTime
        val speed = holdTime
        val distance = rideTime * speed
        distance > bestDistance
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val bestTimes = toLongs(input.first().substringAfter(":"))
        val bestDistances = toLongs(input.last().substringAfter(": "))

        return bestTimes
            .zip(bestDistances)
            .map(::countWaysToWin)
            .reduce { acc, x -> acc * x }
    }

    fun buildNumber(line: String): Long {
        return line.split(" ").joinToString("").toLong()
    }

    fun part2(input: List<String>): Int {
        val bestTime = buildNumber(input.first().substringAfter(":"))
        val bestDistance = buildNumber(input.last().substringAfter(":"))

        return countWaysToWin(bestTime to bestDistance)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
