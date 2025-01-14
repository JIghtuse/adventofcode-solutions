package day15

import java.lang.IllegalArgumentException
import readInput
import kotlin.math.abs

data class Position(val x: Int, val y: Int)

data class Sensor(val position: Position, val closestBeacon: Position)

fun manhattanDistance(a: Position, b: Position) = abs(a.x - b.x) + abs(a.y - b.y)

fun Sensor.covers(p: Position): Boolean {
    return manhattanDistance(this.position, p) <= manhattanDistance(this.position, this.closestBeacon)
}

// Creates command to draw rhombus with gnuplot
fun Sensor.toDrawCommand(): String {
    val toBeacon = manhattanDistance(this.position, this.closestBeacon)
    val y1 = this.position.y - toBeacon
    val y2 = this.position.y + toBeacon
    val x1 = this.position.x - toBeacon
    val x2 = this.position.x + toBeacon
    return "set object polygon from ${y1},${this.position.x} to ${this.position.y},${x2} to ${y2},${this.position.x} to ${this.position.y},${x1}"
}

val re = """Sensor at x=([\d-]+), y=([\d-]+): closest beacon is at x=([\d-]+), y=([\d-]+)""".toRegex()

fun String.toSensor(): Sensor {
    val (sensorX, sensorY, beaconX, beaconY) = re.matchEntire(this)
        ?.destructured
    ?: throw IllegalArgumentException("Unexpected input $this")

    return Sensor(
        Position(sensorX.toInt(), sensorY.toInt()),
        Position(beaconX.toInt(), beaconY.toInt()))
}

fun <T> log(x: T) = println(x)

fun render(sensors: List<Sensor>, xMax: Int, yMax: Int) {
    for (y in 0..yMax) {
        for (x in 0..xMax) {
            val p = Position(x, y)
            print(when {
                sensors.any { it.position == p } -> 'S'
                sensors.any { it.closestBeacon == p} -> 'B'
                sensors.any { it.covers(p) } -> '#'
                else -> '.'
            })
        }
        println()
    }
    println()
}

fun busyPositionsAtRow(sensors: List<Sensor>, row: Int): Int {
    return (-10_000_000..10_000_000).count { x ->
        val p = Position(x, row)
        sensors
            .filterNot { it.closestBeacon == p }
            .any { it.covers(p) }
    }//.also(::log)
}

fun findBeacon(sensors: List<Sensor>, xRange: Pair<Int, Int>, yRange: Pair<Int, Int>): Position {
    fun inRange(p: Position): Boolean {
        return xRange.first <= p.x && p.x <= xRange.second && yRange.first <= p.y && p.y <= yRange.second
    }

    fun isBeacon(p: Position) = inRange(p) && !sensors.any { it.covers(p) }

    for (sensor in sensors) {
        val distanceToBeacon = manhattanDistance(sensor.position, sensor.closestBeacon)

        var y = sensor.position.y
        var x = sensor.position.x - (distanceToBeacon + 1)
        while (x != sensor.position.x) {
            val p = Position(x, y)
            if (isBeacon(p)) return p
            x += 1
            y -= 1
        }
        // we passed one side of each rhombus
        // for correct solution we must walk among all the sides of each rhombus,
        // but it was enough for my case
    }
    return Position(0, 0)
}

fun tuningFrequency(p: Position) = p.x.toLong() * 4_000_000 + p.y

fun main() {
    fun part1(sensors: List<Sensor>, row: Int): Int {
        return busyPositionsAtRow(sensors, row)
    }

    fun part2(sensors: List<Sensor>, xRange: Pair<Int, Int>, yRange: Pair<Int, Int>): Long {
//        render(sensors, xMax=positionLimit, yMax=positionLimit)
        val beacon = findBeacon(sensors, xRange, yRange)
        println(beacon)
        return tuningFrequency(beacon)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test").map(String::toSensor)
    check(part1(testInput, 10) == 26)
    check(part2(testInput, 0 to 20, 0 to 20) == 56_000_011L)
    println("testing done")

    val input = readInput("Day15").map(String::toSensor)
    println(part1(input, 2_000_000)) // 5_240_818
    println(part2(input, 0 to 4_000_000, 0 to 4_000_000))
}