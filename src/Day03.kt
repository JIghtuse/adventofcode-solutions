val numberRegex = """(\d+)""".toRegex()

fun Char.isSymbol(): Boolean {
    return this != '.' && this !in "0123456789"
}

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

    // 467..114..
    // ...*......
    // ..35..633.

    // 1. Find adjacent numbers to each gear
    //     How? Let's enumerate adjacent cells to a gear:
    //          [(0, 2), (0, 3), (0, 4), (1, 2), (1, 4), (2, 2), (2, 3), (2, 4)]
    //     Now we can look how many numbers we have in these adjacent cells
    //          [(1, 2), (1, 4)] are special: when you have digit, you just count it as adjacent number
    //          [(0, 2), (0, 3), (0, 4)] [(2, 2), (2, 3), (2, 4)] require separate treatment
    //              ... -> 0
    //              1.2 -> 2
    //              123 -> 1
    //              4.. -> 1
    //              ..4 -> 1
    //              .4. -> 1
    //              12. -> 1
    //              .12 -> 1

    //     Swell, we also have REAL edge cases where top and bottom are in corners
    //              *.  -> 0
    //
    //              ..  -> 0
    //              *.
    //
    //              1.  -> 1
    //              *.
    //
    //              .5  -> 1
    //              *.
    //
    //              24 -> 1
    //              *.

    // 2. Count adjacent numbers. If count==2, sum up adjacent numbers. Otherwise, skip the gear
    fun part2(input: List<String>): Int {
        val yRange = input.indices
        val xRange = input[0].indices

        fun countAdjacentNumbersTopOrBottom(s: CharSequence): Int {
            return when {
                s.length == 2 && s.any(Char::isDigit) -> 1
                s.length == 2 -> 0
                s == "..." -> 0
                s.all(Char::isDigit) -> 1
                s.first().isDigit() && s.last().isDigit() && s[1] == '.' -> 2
                else -> 1
            }
        }

        fun countAdjacentNumbers(y: Int, x: Int): Int {
            var count = 0

            // left and right are somewhat trivial: add 1 when we have digit
            if (x - 1 in xRange && input[y][x - 1].isDigit()) {
                count += 1
            }
            if (x + 1 in xRange && input[y][x + 1].isDigit()) {
                count += 1
            }

            // top and bottom has a few edge cases
            if (y - 1 in yRange) {
                val topStart = maxOf(0, x - 1)
                val topEnd = minOf(xRange.last, x + 1)

                val top = input[y - 1].subSequence(topStart, topEnd + 1)
                count += countAdjacentNumbersTopOrBottom(top)
            }
            if (y + 1 in yRange) {
                val bottomStart = maxOf(0, x - 1)
                val bottomEnd = minOf(xRange.last, x + 1)

                val bottom = input[y + 1].subSequence(bottomStart, bottomEnd + 1)
                count += countAdjacentNumbersTopOrBottom(bottom)
            }

            return count
        }

        fun gearRatio(y: Int, x: Int): Int {
            val nums = mutableListOf<Int>()

            if (x - 1 in xRange) {
                var i = x - 1
                while (i > 0 && input[y][i].isDigit()) {
                    i -= 1
                }
                nums.add(input[y].subSequence(i, x).toString().toInt())
            }
            if (x + 1 in xRange) {
                var i = x + 1
                while (i < xRange.last && input[y][i].isDigit()) {
                    i += 1
                }
                nums.add(input[y].subSequence(x, i + 1).toString().toInt())
            }

            return nums.reduce{ acc, n -> acc * n }
        }

        var ratioSum = 0
        for ((y, line) in input.withIndex()) {
            for ((x, character) in line.withIndex()) {
                if (character == '*') {
                    val adjacentNumbersCount = countAdjacentNumbers(y, x)
                    if (adjacentNumbersCount == 2) {
                        println("gear found at ($y, $x). Need to sum up its neighbours")
                        ratioSum += gearRatio(y, x)
                    } else {
                        println("($y, $x) is not a gear: has $adjacentNumbersCount neighbours")
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
