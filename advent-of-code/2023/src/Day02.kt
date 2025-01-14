fun main() {
    fun roundColor(roundPart: String) =
        roundPart.substringBefore(" ").toInt() to roundPart.substringAfter(" ")

    fun part1(input: List<String>): Int {
        val constraints = mapOf(
            "red" to 12,
            "green" to 13,
            "blue" to 14,
        )

        fun possibleRound(roundDescription: String): Boolean {
            val colors = roundDescription.split(", ")
            return colors.all {
                val (quantity, color) = roundColor(it)
                quantity <= constraints[color]!!
            }
        }

        return input.sumOf { gameLine ->
            val gameNumber = gameLine.substringBefore(": ").substringAfter("Game ").toInt()
            val rounds = gameLine.substringAfter(": ").split("; ")

            if (rounds.all { possibleRound(it) }) {
                gameNumber
            } else {
                0
            }
        }
    }

    fun part2(input: List<String>): Int {
        fun power(gameLine: String): Int {
            val rounds = gameLine.substringAfter(": ").split("; ")

            val fewestNumberOfCubes = mutableMapOf(
                "red" to 0,
                "green" to 0,
                "blue" to 0,
            )

            rounds.forEach{ round ->
                val colors = round.split(", ")
                colors.forEach{
                    val (quantity, color) = roundColor(it)

                    fewestNumberOfCubes[color] = maxOf(fewestNumberOfCubes[color]!!, quantity)
                }
            }

            return fewestNumberOfCubes.map {
                it.value
            }.reduce { acc, n
                ->
                acc * n
            }
        }

        return input.sumOf {
            power(it)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
