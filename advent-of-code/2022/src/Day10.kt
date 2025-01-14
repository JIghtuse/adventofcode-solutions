import java.io.File

sealed class Instruction {
    object Noop: Instruction()

    data class Addx(val value: Int): Instruction()
}

fun parseInstruction(line: String) =
    when (line) {
        "noop" -> Instruction.Noop
        else -> Instruction.Addx(line.substringAfter(" ").toInt())
    }

fun parseInstructions(name: String) =
    File("src", "$name.txt")
        .readLines()
        .map(::parseInstruction)

fun execute(instructions: List<Instruction>) {
    val lastValue = instructions.fold(1) { x, instruction ->
        when (instruction) {
            is Instruction.Noop -> x
            is Instruction.Addx -> x + instruction.value
        }
    }
    println("last value is $lastValue")
}

fun sumSignalStrengthValues(
    instructions: List<Instruction>,
    interestingCycles: List<Int>): Int {
    var x = 1

    var interestingSignalValue = 0
    fun updateOnInterestingCycle(c: Int) {
        if (c in interestingCycles) {
            interestingSignalValue += c * x
        }
    }

    var cycle = 0
    instructions.forEach {
        cycle += 1
        when (it) {
            is Instruction.Noop -> {
                updateOnInterestingCycle(cycle)
            }
            is Instruction.Addx -> {
                updateOnInterestingCycle(cycle)
                cycle += 1
                updateOnInterestingCycle(cycle)
                x += it.value
            }
        }
    }

    return interestingSignalValue
}

data class Sprite(val start: Int, val end: Int)

fun isCurrentlyDrawn(sprite: Sprite, cycle: Int) =
    cycle >= sprite.start && cycle <= sprite.end

fun toSprite(xRegister: Int) =
    Sprite(xRegister - 1, xRegister + 1)

fun render(instructions: List<Instruction>) {
    var x = 1
    var cycle = 0

    fun drawPixel() {
        print(if (isCurrentlyDrawn(toSprite(x), cycle % 40)) "#" else ".")
    }

    instructions.forEach {
        if (cycle % 40 == 0) println()
        drawPixel()
        cycle += 1

        when (it) {
            is Instruction.Noop -> {}
            is Instruction.Addx -> {
                if (cycle % 40 == 0) println()
                drawPixel()
                cycle += 1
                x += it.value
            }
        }
    }
    println()
}

fun main() {
    fun part1(input: List<Instruction>): Int {
        return sumSignalStrengthValues(
            input,
            listOf(20, 60, 100, 140, 180, 220)
        )
    }

    // test if implementation meets criteria from the description, like:
    val testInput = parseInstructions("Day10_test")
    check(part1(testInput) == 13140)
    render(testInput)

    val input = parseInstructions("Day10")
    println(part1(input))
    render(input)
}