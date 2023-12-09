
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
        //println("Seeds: $seeds")

        val seedRanges = seeds.map {(start, length) ->
            start..start + length
        }.sortedBy { it.first }

        //println("seed ranges: $seedRanges")

        // TODO: lisce parts
        // val converterLines = parts[1].lines()

        // 0..Max 0

        // 50 98 2   (50 - 98 = -48)
        // 52 50 48  (52 - 50 = 2)

        // 0..49 0
        // 50..97 +2
        // 98..99 -48
        // 100..Max 0

        // 0 0
        // 50 2
        // 98 -48
        // 100 0

        // 0 0
        // 15 -15
        // 50 -13
        // 52 2
        // 98 -48
        // 100 0

        // 0 0
        // 15 -15
        // 50 -13
        // 52 -13
        // 54 2
        // 98 -48
        // 100 0

        val converterPoints = mutableListOf(0L to 0L)

        for (converterPart in parts.slice(1..parts.lastIndex)) {
            val converterLines = converterPart.lines()
            for (line in converterLines.slice(1..converterLines.lastIndex)) {
                val (dst, src, length) = line.split(" ").map(String::toLong)

                val diff = dst - src

                val oldRangeStart = converterPoints.withIndex().last { it.value.first <= src }
                //val oldRangeEnd = converterPoints.withIndex().last { it.value.first <= src + length - 1 }
                //println("oldRangeStart: $oldRangeStart; oldRangeEnd: $oldRangeEnd")
               // println("oldRangeStart: $oldRangeStart")

                var i = oldRangeStart.index

                var lastRewritten: Long? = null

                if (src == oldRangeStart.value.first) {
                    lastRewritten = oldRangeStart.value.second
                    converterPoints[i] = src to oldRangeStart.value.second + diff
                } else {
                    // converterPoints.removeAt(i)
                    converterPoints.add(i + 1, src to oldRangeStart.value.second + diff)
                    i += 1
                }
                i += 1
                //println("begin processed; i: $i; points: $converterPoints")


                while (i in converterPoints.indices && converterPoints[i].first <= src + length - 1) {
                    lastRewritten = converterPoints[i].second
                    converterPoints[i] = converterPoints[i].first to converterPoints[i].second + diff
                    i += 1
                }
               // println("middle processed; i: $i; points: $converterPoints")

                if (converterPoints.size == i) {
                    converterPoints.add(i, src + length to oldRangeStart.value.first)
                } else if (converterPoints[i].first != src + length) {
                    converterPoints.add(i, src + length to (lastRewritten ?: 99999999999L))
                }

                println("Incorporated range ${src..<src + length}: $converterPoints")
            }
        }
        println("converter points after converters loop: $converterPoints")

        //val converterRanges = mutableListOf(0..Long.MAX_VALUE to 0L)

        /*
        fun addConverterRange(initialAddIndex: Int, range: Pair<LongRange, Long>) {
            var addIndex = initialAddIndex
            converterRanges.add(addIndex, range)
            addIndex += 1
        }

         */

        /*
       // println("converter rules")
        for (converterPart in parts.slice(1..2)) {
            val converterLines = converterPart.lines()
            for (line in converterLines.slice(1..converterLines.lastIndex)) {
                val (dst, src, length) = line.split(" ").map(String::toLong)

                val converterRangeWithSrcStart = converterRanges.withIndex().first {
                    src in it.value.first
                }

                // considering only one case for now: entire new range fits into old one
                // TODO: other cases
                val newRange =
                    converterRangeWithSrcStart.value.first.first..<src to converterRangeWithSrcStart.value.second


                var addIndex = converterRangeWithSrcStart.index


                val diff = dst - src
                //println("${0..src} has 0 diff")
                println("Found new range ${src..<src + length} has $diff diff")
                converterRanges.removeAt(converterRangeWithSrcStart.index)

                // new first part of previously-existed range
                if (converterRangeWithSrcStart.value.first.first < src) {
                    converterRanges.add(addIndex, newRange)
                    addIndex += 1
                }
                //  if (src + length - 1 !in converterRangeWithSrcStart.value.first) {
                if (src + length > src) {
                    converterRanges.add(addIndex, src..<src + length to diff)
                    addIndex += 1
                }
                //

                if (src + length < converterRangeWithSrcStart.value.first.last) {
                    converterRanges.add(
                        addIndex,
                        src + length..converterRangeWithSrcStart.value.first.last to converterRangeWithSrcStart.value.second
                    )
                }
                addIndex += 1
                println("now converter ranges: $converterRanges")
                //println("${src + length..Long.MAX_VALUE} has 0 diff")
            }
        }
        // converterRanges = converterRanges.sortedBy { it.first.first }.toMutableList()
        println("converter ranges after converters loop: $converterRanges")
        println()


         */
        /*
        for (line in parts[2].lines().slice(1..converterLines.lastIndex)) {
            val (dst, src, length) = line.split(" ").map(String::toLong)

            val diff = dst - src
            //println("${0..src} has 0 diff")
            // println("${src..<src + length} has $diff diff")
            println("Found new range ${src..<src + length} has $diff diff (skipping it for now)")
            //converterRanges.add(src..<src + length to diff)
            //println("${src + length..Long.MAX_VALUE} has 0 diff")
        }
        println(converterRanges)
         */
        println()

        /*
        val converterRanges = converterLines
            .slice(1..converterLines.lastIndex)
            .map { it.split(" ").map(String::toLong) }
            .map {
                val (destinationStart, sourceStart, length) = it
                destinationStart..destinationStart + length to sourceStart..sourceStart + length
            }
            .sortedBy { it.second.first }
        */

        //println("converter ranges $converterRanges")

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
