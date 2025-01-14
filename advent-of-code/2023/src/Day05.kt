
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

    data class ConverterPoint(val start: Long, val diff: Long)

    fun buildConverterPoints(parts: List<String>): List<List<ConverterPoint>> {
        val allConverterPoints = mutableListOf<List<ConverterPoint>>()

        fun processConverterLines(converterLines: List<String>): List<ConverterPoint> {
            val converterPoints = mutableListOf(ConverterPoint(0, 0))

            for (line in converterLines) {
                val (dst, src, length) = line.split(" ").map(String::toLong)

                val diff = dst - src

                val oldRangeStart = converterPoints.withIndex().last { it.value.start <= src }

                var i = oldRangeStart.index

                var leftOver = oldRangeStart.value.diff

                if (src == oldRangeStart.value.start) {
                    converterPoints[i] = ConverterPoint(src, oldRangeStart.value.diff + diff)
                } else {
                    converterPoints.add(i + 1, ConverterPoint(src, oldRangeStart.value.diff + diff))
                    i += 1
                }
                i += 1

                while (i in converterPoints.indices && converterPoints[i].start <= src + length - 1) {
                    leftOver = converterPoints[i].diff
                    converterPoints[i] = ConverterPoint(converterPoints[i].start, converterPoints[i].diff + diff)
                    i += 1
                }
                if (converterPoints.size <= i) {
                    converterPoints.add(i, ConverterPoint(src + length, oldRangeStart.value.diff))
                } else if (converterPoints[i].start != src + length) {
                    converterPoints.add(i, ConverterPoint(src + length, leftOver))
                }
            }
            return converterPoints
        }

        for (converterPart in parts) {
            val converterLines = converterPart.lines()

            allConverterPoints.add(
                processConverterLines(converterLines.slice(1..converterLines.lastIndex)))
        }

        return allConverterPoints
    }

    fun convertRange(range: LongRange, converterPoint: ConverterPoint) =
        range.first + converterPoint.diff..range.last + converterPoint.diff

    fun part2(input: String): Long {

        val parts = input.split("\n\n")

        val seeds = toLongs(parts[0].substringAfter(": ")).windowed(2, 2)

        val allSeedRanges = seeds.map {(start, length) ->
            start..<start + length
        }.sortedBy { it.first }

        val allConverterPoints = buildConverterPoints(parts.slice(1..parts.lastIndex))

        return allSeedRanges.minOf { initialSeedRange ->
            var currentRanges = listOf(initialSeedRange)

            for (converterPoints in allConverterPoints) {
                val newRanges = mutableListOf<LongRange>()

                for (seedRange in currentRanges) {
                    val firstValue = seedRange.first

                    val firstPoint = converterPoints.withIndex().last { point ->
                        point.value.start <= firstValue
                    }

                    val endsHere =
                        firstPoint.index == converterPoints.lastIndex || seedRange.last < converterPoints[firstPoint.index + 1].start

                    if (endsHere) {
                        newRanges.add(convertRange(seedRange, firstPoint.value))
                    } else {
                        // we cover multiple converter ranges
                        newRanges.add(
                            convertRange(
                                firstValue..<converterPoints[firstPoint.index + 1].start,
                                firstPoint.value
                            )
                        )

                        var currentValue = converterPoints[firstPoint.index + 1].start

                        for (j in firstPoint.index + 1..<converterPoints.lastIndex) {
                            if (converterPoints[j].start > seedRange.last) break

                            newRanges.add(convertRange(currentValue..<converterPoints[j].start, converterPoints[j]))
                            currentValue = converterPoints[j].start
                        }
                        newRanges.add(convertRange(currentValue..seedRange.last, converterPoints.last()))
                    }
                }
                currentRanges = newRanges
            }

            currentRanges.minOf { it.first }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readToString("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readToString("Day05")
    part1(input).println()
    part2(input).println()
}
