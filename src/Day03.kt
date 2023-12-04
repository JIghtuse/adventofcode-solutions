val numberRegex = """(\d+)""".toRegex()

fun Char.isSymbol(): Boolean {
    return this != '.' && this !in "0123456789"
}

typealias NumberRanges = List<Pair<Int, IntRange>>

val positionDiff = listOf(
    -1 to -1,
    -1 to 0,
    -1 to 1,
    0 to -1,
    0 to 1,
    1 to -1,
    1 to 0,
    1 to 1
)

fun main() {
    fun part1(input: List<String>): Int {
        val yRange = input.indices
        val xRange = input[0].indices

        fun hasAdjacentSymbol(y: Int, x: Int): Boolean {
            return positionDiff.any { (dy, dx) ->
                val ny = y + dy
                val nx = x + dx
                ny in yRange && nx in xRange && input[ny][nx].isSymbol()
            }
        }

        var partSum = 0

        for ((y, line) in input.withIndex()) {
            val numbers = numberRegex.findAll(line)

            for (number in numbers) {
                if (number.range.any { x -> hasAdjacentSymbol(y, x) }) {
                    partSum += number.value.toInt()
                }
            }
        }

        return partSum
    }

    fun part2(input: List<String>): Int {
        val yRange = input.indices
        val xRange = input[0].indices

        fun expandRangeLeft(line: String, range: IntRange): IntRange {
            var expandedRange = range
            for (i in range.first downTo 0) {
                if (!line[i].isDigit()) break

                expandedRange = i..expandedRange.last
            }
            return expandedRange
        }

        fun expandRangeRight(line: String, range: IntRange): IntRange {
            var expandedRange = range
            for (j in range.last..xRange.last) {
                if (!line[j].isDigit()) break

                expandedRange = expandedRange.first..j
            }
            return expandedRange
        }

        fun expandRange(line: String, range: IntRange) = expandRangeRight(line, expandRangeLeft(line, range))

        fun findAdjacentNumbersTopOrBottom(y: Int, range: IntRange): NumberRanges {
            val line = input[y]
            val s = line.subSequence(range)

            return when {
                // "..."
                s.none(Char::isDigit) -> listOf()
                // "123"
                s.all(Char::isDigit) -> listOf(y to expandRange(line, range))

                // ".5", "#2" etc
                s.length == 2 && s.last().isDigit() -> listOf(y to expandRangeRight(line, range.last..range.last))
                // "5.", "2@" etc
                s.length == 2 && s.first().isDigit() -> listOf(y to expandRangeLeft(line, range.first..range.first))

                // "23."
                s[1].isDigit() && s[0].isDigit() -> listOf(y to expandRangeLeft(line, range.first..<range.last))
                // "@12"
                s[1].isDigit() && s[2].isDigit() -> listOf(y to expandRangeRight(line, range.first + 1..range.last))
                // ".5."
                s[1].isDigit() -> listOf(y to range.first + 1..range.first + 1)

                // "1.5"
                s[0].isDigit() && s[2].isDigit() -> listOf(
                    y to expandRangeLeft(line, range.first..range.first),
                    y to expandRangeRight(line, range.last..range.last))
                // "2.."
                s[0].isDigit() -> listOf(y to expandRangeLeft(line, range.first..range.first))
                // "..3"
                s[2].isDigit() -> listOf(y to expandRangeRight(line, range.last..range.last))

                // satisfying compiler with necessary branch
                else -> listOf()
            }
        }

        fun buildTopOrBottomXRange(x: Int): IntRange {
            val start = maxOf(0, x - 1)
            val end = minOf(xRange.last, x + 1)

            return start..end
        }

        fun findAdjacentNumbers(y: Int, x: Int): NumberRanges {
            val numbers = mutableListOf<Pair<Int, IntRange>>()

            // left and right are somewhat trivial: expand to one side
            if (x - 1 in xRange && input[y][x - 1].isDigit()) {
                numbers.add(y to expandRangeLeft(input[y], x - 1..<x))
            }
            if (x + 1 in xRange && input[y][x + 1].isDigit()) {
                numbers.add(y to expandRangeRight(input[y], x + 1..x + 1))
            }

            // top and bottom has a few edge cases
            if (y - 1 in yRange) {
                numbers.addAll(findAdjacentNumbersTopOrBottom(y - 1, buildTopOrBottomXRange(x)))
            }
            if (y + 1 in yRange) {
                numbers.addAll(findAdjacentNumbersTopOrBottom(y + 1, buildTopOrBottomXRange(x)))
            }

            return numbers
        }

        fun gearRatio(adjacentNumbers: NumberRanges): Int {
            return adjacentNumbers.map { (y, range) ->
                input[y].substring(range).toInt()
            }.reduce { acc, i -> acc * i }
        }

        var ratioSum = 0
        for ((y, line) in input.withIndex()) {
            for ((x, character) in line.withIndex()) {
                if (character == '*') {
                    val adjacentNumbers = findAdjacentNumbers(y, x)
                    if (adjacentNumbers.size == 2) {
                        ratioSum += gearRatio(adjacentNumbers)
                    }
                }
            }
        }
        return ratioSum
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
