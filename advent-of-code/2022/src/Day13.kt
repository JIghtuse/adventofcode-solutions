import java.lang.IllegalStateException

data class OrderResult(val ordering: Int, val endReached: Boolean)

enum class State {
    Inc,
    Dec,
    ReadingNumber,
    WaitingForItem,
}

fun toState(c: Char) =
    when (c) {
        '[' -> State.Inc
        ']' -> State.Dec
        ',' -> State.WaitingForItem
        else -> State.ReadingNumber
    }

fun toDigit(c: Char) = c.code - '0'.code

sealed class Token {
    object Open: Token()
    object Close: Token()
    data class Number(val n: Int): Token()
}

fun numberToSingleItemList(n: Token.Number) =
    listOf(Token.Open, n, Token.Close)


fun removeWrappedNumbers(tokens: MutableList<Token>): List<Token> {
    var hasWrappedNumbers = true
    while (hasWrappedNumbers) {
        hasWrappedNumbers = false
        for (j in 1 until tokens.lastIndex) {
            when {
                tokens[j] is Token.Number && tokens[j - 1] == Token.Open && tokens[j + 1] == Token.Close ->
                    {
                        hasWrappedNumbers = true
                        tokens.removeAt(j + 1)
                        tokens.removeAt(j - 1)
                        break
                    }
            }
        }
    }
    return tokens
}

fun tokenize(s: String): List<Token> {
    val tokens = mutableListOf<Token>()

    var i = 0
    while (i != s.length) {

        when (s[i]) {
            '[' -> {
                tokens.add(Token.Open)
                i += 1
            }
            ']' -> {
                tokens.add(Token.Close)
                i += 1
            }
            ',' -> { i += 1 }
            ' ' -> { i += 1 }
            else -> {
                var x = 0
                while (s[i].isDigit()) {
                    x = x * 10 + toDigit(s[i])
                    i += 1
                }
                tokens.add(Token.Number(x))
            }
        }
    }

    return removeWrappedNumbers(tokens)
}

fun format(tokens: List<Token>) =
    tokens.joinToString(" ") {
        when (it) {
            is Token.Number -> it.n.toString()
            is Token.Close -> "]"
            is Token.Open -> "["
        }
    }

fun comparePackets(left: List<Token>, right: List<Token>): OrderResult {
//    println("Comparing ${format(left)}")
//    println("With      ${format(right)}")
    var i = 0
    var j = 0
    while (i != left.size && j != right.size) {
        if (left[i] == right[j]) {
            i++
            j++
            continue
        }
        val leftToken = left[i]
        val rightToken = right[j]

        when {
            leftToken is Token.Number && rightToken is Token.Number -> when {
                leftToken.n < rightToken.n -> return OrderResult(-1, false)
                leftToken.n > rightToken.n -> return OrderResult(1, false)
            }

            leftToken is Token.Close && (rightToken is Token.Open || rightToken is Token.Number) ->
                return OrderResult(-1, false)

            (leftToken is Token.Open || leftToken is Token.Number) && rightToken is Token.Close ->
                return OrderResult(1, false)

            leftToken is Token.Number && rightToken is Token.Open -> {
                val nestedLeft = numberToSingleItemList(leftToken)
                val nestedRight = right.subList(j, right.lastIndex)
                val nestedOrder = comparePackets(nestedLeft, nestedRight)

                if (!nestedOrder.endReached) return nestedOrder
                println("nested not decided for right list ${format(nestedLeft)} VS ${format(nestedRight)}: $nestedOrder, going further")
            }


            leftToken is Token.Open && rightToken is Token.Number -> {
                val nestedLeft = left.subList(i, left.lastIndex)
                val nestedRight = numberToSingleItemList(rightToken)
                val nestedOrder = comparePackets(nestedLeft, nestedRight)

                if (!nestedOrder.endReached) return nestedOrder
                println("nested not decided for left list ${format(nestedLeft)} VS ${format(nestedRight)}: $nestedOrder, going further")
                return OrderResult(-1, true)
            }

            else -> {
                throw IllegalStateException("need to analyze situation more: ${left[i]} vs ${right[j]}")
            }
        }
        break
    }

    return when {
        i == left.size && left.size < right.size -> {
            OrderResult(-1, true)
        }
        j == right.size && left.size > right.size -> {
            OrderResult(1, true)
        }
        else -> {
            OrderResult(0, true)
        }
    }
}


fun main() {
    fun part1(input: String): Int {
        return input
            .split("\n\n")
            .withIndex()
            .filter { (_, packetPair) ->
                val lines = packetPair.split("\n")
                comparePackets(
                    tokenize(lines[0]),
                    tokenize(lines[1])).ordering == -1
            }
            .sumOf { (index, _) -> index + 1 }
    }

    fun part2(input: String): Int {
        val sortedPackets = input
            .split("\n")
            .filterNot { it.isEmpty() }
            .sortedWith { x, y ->
                comparePackets(
                    tokenize(x),
                    tokenize(y)).ordering
            }

        val divider1 = sortedPackets.indexOf("[[2]]") + 1
        val divider2 = sortedPackets.indexOf("[[6]]") + 1
        return divider1 * divider2
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readText("Day13_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)
    println("tests done")

    val realInput = readText("Day13")
    println(part1(realInput))
    println(part2(realInput))
}