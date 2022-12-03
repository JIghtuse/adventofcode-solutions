import java.lang.IllegalStateException

fun main() {
    fun toPriority(c: Char): Int =
        when (c) {
            in 'a'..'z' -> c.code - 'a'.code + 1
            in 'A'..'Z' -> c.code - 'A'.code + 27
            else -> throw IllegalStateException("unexpected input $c")
        }

    fun part1(input: List<String>): Int {
        fun rucksackToPriority(s: String): Int {
            val left = s.substring(0, s.length / 2)
            val right = s.substring(s.length / 2)

            val c = left.filter { letter -> letter in right }
            return toPriority(c.first())
        }
        return input.map(::rucksackToPriority).sum()
    }

    fun part2(input: List<String>): Int {
        fun threeRucksacksToPriority(threeLines: List<String>): Int {
            val c = threeLines[0].filter { letter -> letter in threeLines[1] && letter in threeLines[2] }
            return toPriority(c.first())
        }

        return input
            .windowed(3, 3)
            .map(::threeRucksacksToPriority)
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
