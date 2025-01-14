import java.lang.IllegalStateException

typealias WorryLevel = Long
typealias MonkeyIndex = Int

fun reduceOnRelief(level: WorryLevel): WorryLevel = level / 3

fun toOperation(s: String, modulo: Long): (WorryLevel) -> WorryLevel {
    val parts = s
        .substringAfter(" = ")
        .split(" ")

    val op = parts[1]

    return when (parts[0] to parts[2]) {
        "old" to "old" -> when (op) {
            "+" -> { x: WorryLevel -> (x % modulo) + (x % modulo) }
            "*" -> { x: WorryLevel -> (x % modulo) * (x % modulo) }
            else -> throw IllegalStateException("unknown op $op")
        }

        "old" to parts[2] -> when (op) {
            "+" -> { x: WorryLevel -> (x % modulo) + parts[2].toInt() }
            "*" -> { x: WorryLevel -> (x % modulo) * parts[2].toInt() }
            else -> throw IllegalStateException("unknown op $op")
        }

        else -> when (op) {
            "+" -> { x: WorryLevel -> parts[0].toInt() + (x % modulo) }
            "*" -> { x: WorryLevel -> parts[0].toInt() * (x % modulo) }
            else -> throw IllegalStateException("unknown op $op")
        }
    }
}

fun parseLastWordNumber(s: String) = s.split(" ").last().toInt()

fun toTest(monkeyData: List<String>): (WorryLevel) -> MonkeyIndex {
    val divisibleBy = parseLastWordNumber(monkeyData[3])
    val trueIndex = parseLastWordNumber(monkeyData[4])
    val falseIndex = parseLastWordNumber(monkeyData[5])

    return { x: WorryLevel -> if (x % divisibleBy == 0L) trueIndex else falseIndex }
}

class Monkey(
    var items: MutableList<WorryLevel>,
    val operation: (WorryLevel) -> WorryLevel,
    val test: (WorryLevel) -> MonkeyIndex,
    private val modulo: Long) {
    var itemsInspected = 0

    fun makeTurnWithOperationPostprocess(
        monkeys: List<Monkey>,
        postprocess: (WorryLevel) -> WorryLevel) {
        items.forEach {
            itemsInspected++
            val item = postprocess(operation(it))
            val throwTo = test(item)
            monkeys[throwTo].items.add(item)
        }
        items.clear()
    }

    fun makeTurn(monkeys: List<Monkey>) =
        makeTurnWithOperationPostprocess(monkeys, ::reduceOnRelief)

    fun makeTurnNoRelief(monkeys: List<Monkey>) =
        makeTurnWithOperationPostprocess(monkeys) { it % modulo }
}

fun monkeyBusiness(monkeys: List<Monkey>): Long {
    return monkeys
        .toMutableList()
        .sortedBy { it.itemsInspected }
        .takeLast(2)
        .map { it.itemsInspected.toLong() }
        .reduce(Long::times)
}

fun runRounds(
    monkeys: List<Monkey>,
    roundsCount: Int,
    turn: (Monkey, List<Monkey>) -> Unit): Long {
    repeat(roundsCount) {
        for (monkey in monkeys) {
            turn(monkey, monkeys)
        }
    }
    return monkeyBusiness(monkeys)
}

fun listItems(monkeys: List<Monkey>): String {
    return monkeys
        .flatMap { it.items }
        .joinToString()
}

fun listInspections(monkeys: List<Monkey>): String {
    return monkeys
        .map { it.itemsInspected }
        .joinToString()
}

fun parseMonkeys(input: String, modulo: Long): List<Monkey> {
    return input
        .split("\n\n")
        .map { monkeyString ->
            val lines = monkeyString.split("\n")
            val items = lines[1]
                .substringAfter(": ")
                .split(", ")
                .map { it.toLong() }
            Monkey(
                items.toMutableList(),
                toOperation(lines[2], modulo),
                toTest(lines),
                modulo)
        }
}

fun main() {
    fun part1(input: String, modulo: Long): Long {
        val monkeys = parseMonkeys(input, modulo)
        return runRounds(monkeys, 20, Monkey::makeTurn)
    }

    fun part2(input: String, modulo: Long): Long {
        val monkeys = parseMonkeys(input, modulo)
        return runRounds(monkeys, 10_000, Monkey::makeTurnNoRelief)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readText("Day11_test")

    check(part1(testInput, 96577L) == 10605L)
    check(part2(testInput, 96577L) == 2713310158)

    val input = readText("Day11")
    println(part1(input, 9699690L))
    println(part2(input, 9699690L))
}