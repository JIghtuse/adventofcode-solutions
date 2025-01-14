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

            val c = left.first { letter -> letter in right }
            return toPriority(c)
        }
        return input.map(::rucksackToPriority).sum()
    }

    fun part2(input: List<String>): Int {
        fun threeRucksacksToPriority(threeLines: List<String>): Int {
            val c = threeLines[0].first { letter -> letter in threeLines[1] && letter in threeLines[2] }
            return toPriority(c)
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

/*
class Day03(private val input: List<String>) {
fun solvePart1() = input.map { it.take(it.length / 2) to it.substring(it.length / 2) }
.map { (it.first.toSet() intersect it.second.toSet()).single() }
.sumOf(Char::getPriority)

fun solvePart2() = input.map(String::toSet)
.chunked(3)
.map { it.reduce(Set<Char>::intersect).single() }
.sumOf(Char::getPriority)
}

private fun Char.getPriority() = (('a'..'z') + ('A'..'Z')).indexOf(this) + 1
 */