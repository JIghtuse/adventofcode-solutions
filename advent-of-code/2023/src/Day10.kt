data class Point(val y: Int, val x: Int)

data class Offset(val y: Int, val x: Int)

fun Point.shift(offset: Offset) = Point(this.y + offset.y, this.x + offset.x)

typealias Field = List<String>


fun Field.at(point: Point) = this[point.y][point.x]

enum class Direction {
    Up,
    Right,
    Down,
    Left,
}

fun directionChange(currentDirection: Direction, symbol: Char) = when {
    currentDirection == Direction.Up && symbol == '|' -> Direction.Up
    currentDirection == Direction.Up && symbol == '7' -> Direction.Left
    currentDirection == Direction.Up && symbol == 'F' -> Direction.Right
    currentDirection == Direction.Down && symbol == 'L' -> Direction.Right
    currentDirection == Direction.Down && symbol == 'J' -> Direction.Left
    currentDirection == Direction.Down && symbol == '|' -> Direction.Down
    currentDirection == Direction.Right && symbol == '-' -> Direction.Right
    currentDirection == Direction.Right && symbol == '7' -> Direction.Down
    currentDirection == Direction.Right && symbol == 'J' -> Direction.Up
    currentDirection == Direction.Left && symbol == '-' ->  Direction.Left
    currentDirection == Direction.Left && symbol == 'F' -> Direction.Down
    currentDirection == Direction.Left && symbol == 'L' -> Direction.Up
    else -> null
}

val neighbourDiffs = mapOf(
    Direction.Up to Offset(-1, 0),
    Direction.Down to Offset(1, 0),
    Direction.Left to Offset(0, -1),
    Direction.Right to Offset(0, 1),
)

class Cursor(private var point: Point, private var direction: Direction, var distanceFromStart: Int, field: Field) {
    init {
        point = point.shift(neighbourDiffs[direction]!!)
        direction = directionChange(direction, field.at(point))!!
    }

    override fun toString(): String {
        return "Cursor($point, $direction, $distanceFromStart)"
    }

    fun move(field: Field): Point {
        val offset = neighbourDiffs[direction]!!

        point = point.shift(offset)
        direction = directionChange(direction, field.at(point))!!
        distanceFromStart += 1

        return point
    }
}


fun main() {
    fun findStart(input: Field): Point {
        val startLine = input.withIndex().first { (_, line) ->
            line.contains('S')
        }
        val startCol = startLine.value.indexOf('S')
        return Point(startLine.index, startCol)
    }

    fun part1(field: Field, initialDirectionA: Direction, initialDirectionB: Direction): Int {
        val start = findStart(field)

        var pointA: Point
        var pointB = start
        val cursorA = Cursor(start, initialDirectionA, 1, field)
        val cursorB = Cursor(start, initialDirectionB, 1, field)

        while (true) {
            pointA = cursorA.move(field)
            if (pointA == pointB) {
                return maxOf(cursorA.distanceFromStart, cursorB.distanceFromStart)
            }
            pointB = cursorB.move(field)
            if (pointA == pointB) {
                return maxOf(cursorA.distanceFromStart, cursorB.distanceFromStart)
            }
        }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    val testInput2 = readInput("Day10_test2")
    check(part1(testInput, Direction.Right, Direction.Down) == 8)
    check(part1(testInput2, Direction.Right, Direction.Down) == 4)

    val input = readInput("Day10")
    part1(input, Direction.Up, Direction.Left).println()
    part2(input).println()
}
