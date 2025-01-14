data class OutgoingNode(val left: String, val right: String)

fun parseNetwork(nodeLines: List<String>) = buildMap {
    for (nodeLine in nodeLines) {
        val name = nodeLine.substringBefore(" = ")
        val outgoingNodes = nodeLine.substringAfter("(").substringBefore(")")
        val (left, right) = outgoingNodes.split(", ")

        put(name, OutgoingNode(left, right))
    }
}

fun countSteps(instructions: String, network: Map<String, OutgoingNode>, startNode: String, endCondition: (String) -> Boolean): Long
{
    var stepCount = 0L
    var node = startNode
    while (!endCondition(node)) {
        val outgoingNode = network[node]!!
        node = if (instructions[stepCount.toInt() % instructions.length] == 'R') {
            outgoingNode.right
        } else {
            outgoingNode.left
        }
        stepCount += 1
    }

    return stepCount
}

fun gcd(a: Long, b: Long): Long {
    return if (b == 0L) a else gcd(b, a % b)
}

fun lcm(a: Long, b: Long) = a * b / gcd(a, b)

fun main() {
    fun part1(input: String): Long {
        val instructions = input.substringBefore("\n")

        val lines = input.lines()
        val network = parseNetwork(lines.slice(2..lines.lastIndex))

        return countSteps(instructions, network, "AAA") { it == "ZZZ" }
    }

    fun part2(input: String): Long {
        val instructions = input.substringBefore("\n")

        val lines = input.lines()
        val network = parseNetwork(lines.slice(2..lines.lastIndex))

        val nodes = network.keys.filter { it.endsWith('A') }

        val cyclePeriod = nodes.map { initialNode ->
            countSteps(instructions, network, initialNode) { it.last() == 'Z' }
        }
        return cyclePeriod.reduce { acc, i ->
            lcm(acc, i)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readToString("Day08_test")
    val testInput2 = readToString("Day08_test2")
    val testInput3 = readToString("Day08_test3")
    check(part1(testInput) == 2L)
    check(part1(testInput2) == 6L)
    check(part2(testInput3) == 6L)

    val input = readToString("Day08")
    part1(input).println()
    part2(input).println()
}
