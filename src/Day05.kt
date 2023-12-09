
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

    fun buildConverterPoints(parts: List<String>): List<ConverterPoint> {
        val converterPoints = mutableListOf(ConverterPoint(0, 0))

        fun processConverterLines(converterLines: List<String>) {
            for (line in converterLines) {
                val (dst, src, length) = line.split(" ").map(String::toLong)

                val diff = dst - src
                println("Discovered range ${src..<src + length} diff $diff")

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
                // println("begin processed;    i: $i; points: $converterPoints")

                while (i in converterPoints.indices && converterPoints[i].start <= src + length - 1) {
                    leftOver = converterPoints[i].diff
                    converterPoints[i] = ConverterPoint(converterPoints[i].start, converterPoints[i].diff + diff)
                    i += 1
                }
                // println("middle processed;   i: $i; points: $converterPoints")

                if (converterPoints.size <= i) {
                    converterPoints.add(i, ConverterPoint(src + length, oldRangeStart.value.start))
                } else if (converterPoints[i].start != src + length) {
                    converterPoints.add(i, ConverterPoint(src + length, leftOver))
                }

                //println("Incorporated range ${src..<src + length} diff $diff; points: $converterPoints")
            }
        }

        for (converterPart in parts) {
            val converterLines = converterPart.lines()

            processConverterLines(converterLines.slice(1..converterLines.lastIndex))
        }

        return converterPoints
    }

    fun part2(input: String): Long {

        val parts = input.split("\n\n")

        val seeds = toLongs(parts[0].substringAfter(": ")).windowed(2, 2)

        val seedRanges = seeds.map {(start, length) ->
            start..<start + length
        }.sortedBy { it.first }

        // println("seed ranges: $seedRanges")

        val converterPoints = buildConverterPoints(parts.slice(1..parts.lastIndex))

        //println("converter points size: ${converterPoints.size}")
        //println("converter points after converters loop: $converterPoints")

        return seedRanges.minOf { seedRange ->
            //println("working with range $seedRange")
            var minLocation = Long.MAX_VALUE

            val firstValue = seedRange.first
            //println("current value: $firstValue")

            val firstPoint = converterPoints.withIndex().last { point ->
                point.value.start <= firstValue
            }

            minLocation = minOf(minLocation, firstValue + firstPoint.value.diff)
           //println("FOUND first point: $firstPoint; minLocation is $minLocation")

            for (j in firstPoint.index + 1..converterPoints.lastIndex) {
                if (converterPoints[j].start > seedRange.last) break

                val currentValue = converterPoints[j].start

                minLocation = minOf(minLocation, currentValue + converterPoints[j].diff)
               //println("used value $currentValue; minLocation is $minLocation")
            }

            minLocation
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readToString("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readToString("Day05")
    part1(input).println()
    // -6855347786 is not the right answer
    part2(input).println()
}
