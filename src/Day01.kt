fun CharSequence.startingDigit(): Int? = when {
    this.startsWith("one") -> 1
    this.startsWith("two") -> 2
    this.startsWith("three") -> 3
    this.startsWith("four") -> 4
    this.startsWith("five") -> 5
    this.startsWith("six") -> 6
    this.startsWith("seven") -> 7
    this.startsWith("eight") -> 8
    this.startsWith("nine") -> 9
    this.first().isDigit() -> this.first().digitToInt()
    else -> null
}

fun String.firstDigit(): Int {
    for (i in 0..this.lastIndex) {
        val slice = this.subSequence(i, this.length)

        val digit = slice.startingDigit()
        if (digit != null) {
            return digit
        }
    }
    error("Failed to find digit it $this")
}

fun String.lastDigit(): Int {
    for (i in this.lastIndex downTo 0) {
        val slice = this.subSequence(i, this.length)

        val digit = slice.startingDigit()
        if (digit != null) {
            return digit
        }
    }
    error("Failed to find digit in $this")
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf {
            it.first(Char::isDigit).digitToInt() * 10 + it.last(Char::isDigit).digitToInt()
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf {
            it.firstDigit() * 10 + it.lastDigit()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part2(testInput) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
