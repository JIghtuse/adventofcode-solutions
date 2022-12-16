package day12.aoc

import readInput

typealias Position = Pair<Int, Int>


fun List<String>.at(p: Position): Char? {
    if (p.first < 0 || p.first > this.lastIndex) return null
    if (p.second < 0 || p.second > this[0].lastIndex) return null
    return this[p.first][p.second]
}

fun findPosition(input: List<String>, needle: Char): Position {
    val row = input.indexOfFirst {
        it.indexOf(needle) != -1
    }
    val column = input[row].indexOf(needle)

    return row to column
}

fun findPositions(input: List<String>, needle: Char): Set<Position> {
    return input.withIndex()
        .filter { (_, line) ->
            line.indexOf(needle) != -1
        }
        .flatMap { (r, line) ->
            line.withIndex()
                .filter { (_, c) -> c == needle }
                .map { (col, _) ->
                    r to col
                }
        }.toSet()
}

fun neighbourPositions(p: Position): List<Position> =
    listOf(p.copy(first = p.first + 1),
        p.copy(first = p.first - 1),
        p.copy(second = p.second - 1),
        p.copy(second = p.second + 1))

fun lookAtNeighbours(input: List<String>, pos: Position, ok: (Position) -> Boolean): List<Position> {
    fun existingOkNeighbour(neighbourPosition: Position) =
        input.getOrNull(neighbourPosition.first)?.getOrNull(neighbourPosition.second)?.let {
            ok(neighbourPosition)
        } ?: false

    return neighbourPositions(pos)
        .filter(::existingOkNeighbour)
}

data class PositionPlusSteps(val pos: Position, val steps: Int)

fun draw(input: List<String>, w: Int, h: Int, visited: Set<Position>) {
    for (r in 0 until w) {
        for (c in 0 until h) {
            print(if (visited.contains(Position(r, c))) "#" else input.at(Position(r, c)))
        }
        println()
    }
}

fun countSteps(
    input: List<String>,
    startPos: Position,
    endPos: List<Position>,
    charAt: (Position) -> Char,
    reachable: (Char, Char) -> Boolean): Int {

    val visited = mutableSetOf<Position>()

    var toVisit = listOf(PositionPlusSteps(startPos, 0))

    while (toVisit.isNotEmpty() && !endPos.any { visited.contains(it) }) {
        toVisit = toVisit.flatMap {
            lookAtNeighbours(input, it.pos) { dst ->
                !visited.contains(it.pos) && reachable(charAt(it.pos), charAt(dst))
            }.map { neighbourPosition ->
                PositionPlusSteps(neighbourPosition, it.steps + 1)
            }.also { _ ->
                if (it.pos in endPos) {
                    return it.steps
                }
                visited.add(it.pos)
            }
        }
    }
    return 0 // TODO
}


fun main() {
    fun part1(input: List<String>): Int {
        val startPos = findPosition(input, 'S')
        val endPos = findPosition(input, 'E')

        return countSteps(
            input,
            startPos,
            listOf(endPos),
            {
                when (it) {
                    startPos -> 'a'
                    endPos -> 'z'
                    else -> input.at(it)!!
                }
            },
            { src, dst -> src + 1 >= dst })
    }

    fun part2(input: List<String>): Int {
        val startPos = findPosition(input, 'E')
        val endPos = findPositions(input, 'a').plus(findPositions(input, 'S')).toList()

        return countSteps(
            input,
            startPos,
            endPos,
            {
                when (it) {
                    startPos -> 'z'
                    in endPos -> 'a'
                    else -> input.at(it)!!
                }
            },
            { src, dst -> src - 1 <= dst })
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)
    println("tests done")

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}