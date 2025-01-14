import java.lang.IllegalStateException

enum class Shape {
    Rock,
    Paper,
    Scissors
}

fun opponentInputToShape(s: String): Shape =
    when (s) {
        "A" -> Shape.Rock
        "B" -> Shape.Paper
        "C" -> Shape.Scissors
        else -> throw IllegalStateException("invalid opponent input $s")
    }

fun ourInputToShape(s: String): Shape =
    when (s) {
        "X" -> Shape.Rock
        "Y" -> Shape.Paper
        "Z" -> Shape.Scissors
        else -> throw IllegalStateException("invalid our input $s")
    }

fun scoreForShape(shape: Shape) =
    when (shape) {
        Shape.Rock -> 1
        Shape.Paper -> 2
        Shape.Scissors -> 3
    }

fun scoreForRound(me: Shape, opponent: Shape): Int {
    if (me == opponent) {
        return 3 // draw
    }

    return when (me to opponent) {
        (Shape.Rock to Shape.Scissors) -> 6 // win
        (Shape.Scissors to Shape.Paper) -> 6 // win
        (Shape.Paper to Shape.Rock) -> 6 // win
        else -> 0 // lost
    }
}

fun main() {
    fun scoreForTwoShapes(me: Shape, opponent: Shape) =
        scoreForShape(me) + scoreForRound(me, opponent)

    fun part1(input: List<String>): Int =
        input.sumOf {
            val (s, t) = it.split(" ")
            val ourShape = ourInputToShape(t)
            val opponentShape = opponentInputToShape(s)

            scoreForTwoShapes(ourShape, opponentShape)
        }

    fun determineShape(opponent: Shape, expectedOutcome: String) =
        when (opponent to expectedOutcome) {
            Shape.Rock to "Z" -> Shape.Paper
            Shape.Scissors to "Z" -> Shape.Rock
            Shape.Paper to "Z" -> Shape.Scissors
            Shape.Rock to "X" -> Shape.Scissors
            Shape.Scissors to "X" -> Shape.Paper
            Shape.Paper to "X" -> Shape.Rock
            else -> opponent
    }

    fun part2(input: List<String>): Int =
        input.sumOf {
            val (s, t) = it.split(" ")
            val opponentShape = opponentInputToShape(s)
            val ourShape = determineShape(opponentShape, t)

            scoreForTwoShapes(ourShape, opponentShape)
        }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
