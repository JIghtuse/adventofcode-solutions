import java.io.File
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import kotlin.math.abs
import kotlin.math.sqrt

data class Position(val y: Int, val x: Int)

data class RopeSection(val head: Position, val tail: Position)

typealias Rope = Array<Position>

data class Direction(val dy: Int, val dx: Int)
data class Movement(val direction: Direction, val steps: Int)

val CLOSE_THRESHOLD: Double = sqrt(2.0)

fun toDirection(a: Position, b: Position) =
    Direction(b.y - a.y, b.x - a.x)

fun distance(a: Position, b: Position) =
    sqrt((a.x - b.x).toDouble() * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))

fun movePoint(p: Position, movementDirection: Direction) =
    Position(p.y + movementDirection.dy, p.x + movementDirection.dx)

fun adjust(ropeSection: RopeSection, movementDirection: Direction): RopeSection {
    val (h, t) = ropeSection

    return if (distance(h, t) <= CLOSE_THRESHOLD) {
        ropeSection
    } else {
        ropeSection.copy(tail = when {
            t == h -> t
            t.x == h.x -> t.copy(y = t.y + movementDirection.dy)
            t.y == h.y -> t.copy(x = t.x + movementDirection.dx)
            else -> when (abs(t.x - h.x) to abs(t.y - h.y)) {
                1 to 2 -> Position((h.y + t.y) / 2, h.x)
                2 to 1 -> Position(h.y, (t.x + h.x) / 2)
                2 to 2 -> Position((h.y + t.y) / 2, (h.x + t.x) / 2)
                else -> throw IllegalStateException("unreachable")
            }
        })
    }
}

fun move(rope: Rope, m: Movement): Pair<Rope, List<Position>> {
    val tailPositions = mutableListOf(rope.last())

    repeat(m.steps) {
        var direction = m.direction
        rope[0] = movePoint(rope[0], direction)
        val headSection = adjust(
            RopeSection(rope[0], rope[1]), direction)

        direction = toDirection(rope[1], headSection.tail)
        rope[1] = headSection.tail

        for (i in 2 until rope.lastIndex) {
            val section = adjust(
                RopeSection(rope[i - 1], rope[i]), direction)
            direction = toDirection(rope[i], section.tail)
            rope[i] = section.tail
        }
        val section = adjust(
            RopeSection(rope[rope.lastIndex - 1], rope[rope.lastIndex]), direction)
        rope[rope.lastIndex] = section.tail
        tailPositions.add(rope.last())
    }
    return rope to tailPositions
}

fun parseMovement(line: String): Movement {
    val parts = line.split(" ")
    val steps = parts[1].toInt()
    return when (parts[0]) {
        "R" -> Movement(Direction(0, 1), steps)
        "L" -> Movement(Direction(0, -1), steps)
        "U" -> Movement(Direction(-1, 0), steps)
        "D" -> Movement(Direction(1, 0), steps)
        else -> throw IllegalArgumentException("unknown movement $line")
    }
}

fun parseMovements(name: String): List<Movement> =
    File("src", "$name.txt")
        .readLines()
        .map(::parseMovement)

fun main() {
    fun moveRope(ropeLength: Int, movements: List<Movement>): Int {
        val startRope = Array(ropeLength) { Position(0, 0) }
        val tailPositions = mutableSetOf<Position>()
        movements.fold(startRope)
        { r, movement ->
            val (newRope, doneTailPositions) = move(r, movement)
            tailPositions.addAll(doneTailPositions)
            newRope
        }
        return tailPositions.size
    }

    fun part1(movements: List<Movement>): Int {
        return moveRope(2, movements)
    }

    fun part2(movements: List<Movement>): Int {
        return moveRope(10, movements)
    }

    // test if implementation meets criteria from the description, like:
    val testMovements = parseMovements("Day09_test")
    check(part1(testMovements) == 13)
    check(part2(testMovements) == 1)
    check(part2(parseMovements("Day09_test_02")) == 36)

    val movements = parseMovements("Day09")
    println(part1(movements))
    println(part2(movements))
}