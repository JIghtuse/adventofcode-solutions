import java.io.File
import java.util.Stack

data class Movement(val amount: Int, val stackFrom: Int, val stackTo: Int)

fun String.toMovement(): Movement {
    val parts = this.split(" ")
    return Movement(parts[1].toInt(), parts[3].toInt() - 1, parts[5].toInt() - 1)
}

typealias Stacks = List<Stack<Char>>
typealias Movements = List<Movement>

fun parseStacksAndMovements(name: String): Pair<Stacks, Movements> {
    val (stacksString, movementsString) = File("src", "$name.txt")
        .readText()
        .split("\n\n")

    val stackLines = stacksString.split("\n")

    val numberOfStacks = stackLines.last().split("  ").size

    val stacks = List(numberOfStacks) { Stack<Char>() }
    for (row in stackLines.subList(0, toIndex = stackLines.lastIndex).reversed()) {
        for (itemIndex in 1 until row.length step 4) {
            val letter = row[itemIndex]
            if (letter == ' ') continue

            stacks[(itemIndex - 1) / 4].push(letter)
        }
    }

    val movements = movementsString.lines().map(String::toMovement)

    return stacks to movements
}

fun joinTops(stacks: Stacks) =
    stacks.map{ it.last() }.joinToString("")

// some sketch of simple visualization
fun display(stacks: Stacks) {
    repeat(5) {
        println()
    }
    val maxHeight = stacks.maxOf { it.size }

    for (i in maxHeight downTo 1) {
        for (stack in stacks) {
            if (stack.size < i) {
                print("    ")
            } else {
                print("[${stack[i - 1]}] ")
            }
        }
        println()
    }
}


fun main() {
    fun part1(input: Pair<Stacks, Movements>): String {
        val (stacks, movements) = input

        movements.forEach {
            for (i in 1..it.amount) {
                val x = stacks[it.stackFrom].pop()
                stacks[it.stackTo].push(x)
            }
        }

        return joinTops(stacks)
    }

    fun part2(input: Pair<Stacks, Movements>): String {
        val (stacks, movements) = input

        movements.forEach {
            val reversingStack = Stack<Char>()
            for (i in 1..it.amount) {
                val x = stacks[it.stackFrom].pop()
                reversingStack.push(x)
            }
            while (reversingStack.isNotEmpty()) {
                stacks[it.stackTo].push(reversingStack.pop())
            }
        }

        return joinTops(stacks)
    }

    // test if implementation meets criteria from the description, like:
    check(part1(parseStacksAndMovements("Day05_test")) == "CMZ")
    check(part2(parseStacksAndMovements("Day05_test")) == "MCD")

    println(part1(parseStacksAndMovements("Day05")))
    println(part2(parseStacksAndMovements("Day05")))
}