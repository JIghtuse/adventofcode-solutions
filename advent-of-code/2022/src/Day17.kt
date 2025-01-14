package day17

import readText
import java.lang.IllegalStateException

data class Position(val x: Int, val y: Int)

val rocks = listOf(
    listOf("####"),

    listOf(
        " # ",
        "###",
        " # "),

    listOf(
        "  #",
        "  #",
        "###"),

    listOf(
        "#",
        "#",
        "#",
        "#"),

    listOf(
        "##",
        "##")
)

fun toDimensions(rockIndex: Int) =
    when (rockIndex) {
        0 -> 4 to 1
        1 -> 3 to 3
        2 -> 3 to 3
        3 -> 1 to 4
        4 -> 2 to 2
        else -> throw IllegalStateException("invalid rock index $rockIndex")
    }

fun toPositionsList(rockIndex: Int, topLeft: Position): List<Position> {
    return when (rockIndex) {
        0 -> listOf(
            topLeft,
            topLeft.copy(x = topLeft.x + 1),
            topLeft.copy(x = topLeft.x + 2),
            topLeft.copy(x = topLeft.x + 3))
        1 -> listOf(
            topLeft.copy(x = topLeft.x + 1),
            Position(topLeft.x, topLeft.y - 1),
            Position(topLeft.x + 1, topLeft.y - 1),
            Position(topLeft.x + 2, topLeft.y - 1),
            Position(topLeft.x + 1, topLeft.y - 2))
        2 -> listOf(
            topLeft.copy(x = topLeft.x + 2),
            Position(topLeft.x + 2, topLeft.y - 1),
            Position(topLeft.x, topLeft.y - 2),
            Position(topLeft.x + 1, topLeft.y - 2),
            Position(topLeft.x + 2, topLeft.y - 2))
        3 -> listOf(
            topLeft,
            topLeft.copy(y = topLeft.y - 1),
            topLeft.copy(y = topLeft.y - 2),
            topLeft.copy(y = topLeft.y - 3))
        4 -> listOf(
            topLeft,
            topLeft.copy(x = topLeft.x + 1),
            topLeft.copy(y = topLeft.y - 1),
            Position(topLeft.x + 1, topLeft.y - 1))
        else -> throw IllegalStateException("unkown rock $rockIndex")
    }
}

const val CHAMBER_WIDTH = 7

const val BEGIN_OFFSET_Y = 3

fun isWall(p: Position, walls: Set<Position>) =
    p.y <= 0 || p.x <= 0 || p.x >= 8 || walls.contains(p)

fun draw() {
    val emptyLine = "#${" ".repeat(CHAMBER_WIDTH)}#"
    val floorPositionY = 0
    val sceneHeight = 10

    val rockIndex = 2
    val rock = rocks[rockIndex]
    val (rockWidth, rockHeight) = toDimensions(rockIndex)
    val initialSpacing = 2

    var rockPosition = Position(x = 1 + initialSpacing, y = floorPositionY + BEGIN_OFFSET_Y + rockHeight)

    fun renderScene() {
        repeat (sceneHeight - rockPosition.y) {
            println(emptyLine)
        }

        for (element in rock) {
            println("#  ${element}${" ".repeat(rockPosition.x - 1)}#")
        }

        repeat (rockPosition.y - rockHeight) {
            println(emptyLine)
        }
        // next print all the figures below

        println("#########")
    }

    while (rockPosition.y != floorPositionY + rockHeight) {
        renderScene()
        
        rockPosition = rockPosition.copy(y = rockPosition.y - 1)
    }
    renderScene()
}

fun main() {
    fun part1(jetPatterns: String): Int {
        draw()
        return jetPatterns.length
    }

    fun part2(jetPatterns: String): Int {
        return jetPatterns.length
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readText("Day17_test")
    check(part1(testInput) == 40)
    check(part2(testInput) == 40)
    println("*** testing done ***")

    val input = readText("Day17")
    println(part1(input))
    println(part2(input))
}