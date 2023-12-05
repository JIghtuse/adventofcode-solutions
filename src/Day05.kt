
fun main() {
    fun part1(input: String): Long {
        val parts = input.split("\n\n")

        val seeds = toLongs(parts[0].substringAfter(": "))

        val source = seeds.toMutableList()

        for (converterBlock in parts.slice(1..parts.lastIndex)) {
            val lines = converterBlock.lines()
            // val header = lines[0]

            val ranges = lines
                .slice(1..lines.lastIndex)
                .map { it.split(" ").map(String::toLong) }
                .map {
                    val (destinationStart, sourceStart, length) = it
                    destinationStart..destinationStart + length to sourceStart..sourceStart + length
                }

            for (i in source.indices) {
                val convertRanges = ranges.firstOrNull { (_, sourceRange) ->
                    source[i] in sourceRange
                } ?: continue

                val (destinationRange, sourceRange) = convertRanges
                source[i] = destinationRange.first + (source[i] - sourceRange.first)
            }
        }

        return source.min()
    }

    fun part2(input: String): Long {

        val parts = input.split("\n\n")

        val seeds = toLongs(parts[0].substringAfter(": ")).windowed(2, 2)
        println("Seeds: $seeds")

        val seedRanges = seeds.map {(start, length) ->
            start..start + length
        }.sortedBy { it.first }

        println("seed ranges: $seedRanges")

        val converterLines = parts[1].lines()

        val converterRanges = converterLines
            .slice(1..converterLines.lastIndex)
            .map { it.split(" ").map(String::toLong) }
            .map {
                val (destinationStart, sourceStart, length) = it
                destinationStart..destinationStart + length to sourceStart..sourceStart + length
            }
            .sortedBy { it.second.first }

        println("converter ranges $converterRanges")

        // TODO: min of locations
        return 42L
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readToString("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readToString("Day05")
    part1(input).println()
    part2(input).println()
}
