package day14

import readText
data class Position(val x: Int, val y: Int)

val SandSource = Position(500, 0)

fun String.toPosition(): Position {
    val parts = this.split(",")
    return Position(parts[0].toInt(), parts[1].toInt())
}

fun String.toPositionList() =
    this
    .split(" -> ")
    .map(String::toPosition)
    .toList()

fun toLinePositions(a: Position, b: Position): Set<Position> {
    return when {
        a.x == b.x -> {
            val yRange = (minOf(a.y, b.y)..maxOf(a.y, b.y))
            yRange
                .map { Position(a.x, it) }
                .toSet()
        }
        a.y == b.y -> {
            val xRange = (minOf(a.x, b.x)..maxOf(a.x, b.x))
            xRange
                .map { Position(it, a.y) }
                .toSet()
        }
        else -> setOf()
    }
}

fun toLinesPositions(positions: List<List<Position>>): MutableSet<Position> {
    return positions.flatMap {
        it.windowed(2)
        .flatMap { (start, end) -> toLinePositions(start, end) }
        .toSet()
    }.toMutableSet()
}

fun display(
    xs: Pair<Int, Int>,
    ys: Pair<Int, Int>,
    walls: Set<Position>,
    sand: Set<Position>)
{
    for (y in ys.first .. ys.second) {
        for (x in xs.first .. xs.second) {
            val p = Position(x, y)
            when {
                sand.contains(p) -> print("o")
                p == SandSource -> print("+")
                walls.contains(p) -> print("#")
                else -> print(".")
            }
        }
        println()
    }
}

fun simulateSand(walls: Set<Position>, hasFloor: Boolean): Int {
    val sand = mutableSetOf<Position>()

    val xMin = walls.minBy { it.x }.x
    val xMax = walls.maxBy { it.x }.x
    val yMax = walls.maxBy { it.y }.y

    val abyssLine = yMax + 1

    fun display() = display(xMin to xMax, 0 to yMax + 3, walls, sand)

    fun isFinalPosition(p: Position) = hasFloor && p == SandSource || p.y == abyssLine

    fun busy(p: Position) = sand.contains(p) || walls.contains(p)

    fun nextPosition(grain: Position) =
        when {
            !busy(grain.copy(y = grain.y + 1)) -> {
                grain.copy(y = grain.y + 1)
            }
            !busy(Position(grain.x - 1, grain.y + 1)) -> {
                Position(grain.x - 1, grain.y + 1)
            }
            !busy(Position(grain.x + 1, grain.y + 1)) -> {
                Position(grain.x + 1, grain.y + 1)
            }
            else -> grain
        }

    fun dropGrain(): Position? {
        var grain = SandSource

        var nextPos = nextPosition(grain)
        while (nextPos != grain) {
            grain = nextPos
            nextPos = nextPosition(grain)

            if (isFinalPosition(grain)) return null
        }

        return grain
    }

//    display()
    while (true) {
        val grain = dropGrain() ?: break
        sand.add(grain)
        if (isFinalPosition(grain)) break
//        display()
    }
    display()

    return sand.size
}


fun main() {
    fun part1(inputText: String): Int {
        val wallSections = inputText
            .split("\n")
            .map(String::toPositionList)

        val walls = toLinesPositions(wallSections)
        return simulateSand(walls, hasFloor = false)
    }

    fun part2(inputText: String): Int {
        val wallSections = inputText
            .split("\n")
            .map(String::toPositionList)

        val walls = toLinesPositions(wallSections)

        val floorLevel = walls.maxBy { it.y }.y + 2
        val floor = ((SandSource.x - (floorLevel + 1))..(SandSource.x + floorLevel + 1))
            .map { Position(it, floorLevel) }
        walls.addAll(floor)

        return simulateSand(walls, hasFloor = true)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readText("Day14_test")
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)
    println("tests done")

    val input = readText("Day14")
    println(part1(input))
    println(part2(input))
}