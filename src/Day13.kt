import java.io.File
import java.lang.IllegalStateException

/*
sealed class Packet {
    data class Integer(val n: Int): Packet()

    data class PacketList(val items: List<Packet>): Packet()
}

typealias PacketPair = Pair<Packet, Packet>

fun integerToSingleItemList(i: Packet.Integer) =
    Packet.PacketList(listOf(i))
    */

enum class Ordering {
    Right,
    NotRight,
    Undecided,
}

data class OrderResult(val ordering: Ordering, val endReached: Boolean)

/*
fun determineOrder(x: List<Packet>, y: List<Packet>): OrderResult {
    for (i in 0 until minOf(x.size, y.size)) {
        when (val order = determineOrder(x[i] to y[i])) {
            OrderResult.Undecided -> {}
            else -> return order
        }
    }

    return when {
        x.size < y.size -> OrderResult.Right
        x.size > y.size -> OrderResult.NotRight
        else -> OrderResult.Undecided
    }
}

fun determineOrder(packets: PacketPair): OrderResult {
    val (left, right) = packets
    return when (left) {
        is Packet.Integer -> when (right) {
            is Packet.Integer -> when {
                left.n < right.n -> OrderResult.Right
                left.n > right.n -> OrderResult.NotRight
                else -> OrderResult.Undecided
            }
            is Packet.PacketList -> determineOrder(integerToSingleItemList(left) to right)
        }
        is Packet.PacketList -> when (right) {
            is Packet.Integer -> determineOrder(left to integerToSingleItemList(right))
            is Packet.PacketList -> determineOrder(left.items, right.items)
        }
    }
}
*/

fun toPacketString(s: String): String {
    return s.replace("]", ")")
        .replace("[", "listOf(")
        .replace("(\\d+)".toRegex()) { it.value[0].toString() }
}

fun toPackets(text: String): String {
    return text
        .split("\n\n").joinToString("\n\n") { s: String ->
            val lines = s.split("\n")
            toPacketString(lines[0]) + " to\n" + toPacketString(lines[1]) + ",\n"
        }
}

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

/*
fun properOrder(strings: Pair<String, String>): Boolean {
    val (s, t) = strings
//    var nestLevelLeft = 0
//    var nestLevelRight = 0
    var leftNumber = 0
    var rightNumber = 0

    var i = 0
    var j = 0
    while (i != s.length && j != t.length) {
        var leftState = toState(s[i])
        var rightState = toState(t[j])
        println("${s[i]} $leftState")
        println("${t[j]} $rightState")

        while (leftState == State.ReadingNumber) {
            leftNumber = leftNumber * 10 + toDigit(s[i])
            i++
            leftState = toState(s[i])
        }
        println("left number read: $leftNumber")
        leftNumber = 0

        while (rightState == State.ReadingNumber) {
            rightNumber = rightNumber * 10 + toDigit(t[j])
            j++
            rightState = toState(t[j])
        }
        println("right number read: $rightNumber")
        rightNumber = 0

        println("${s[i]} $leftState (end iteration)")
        println("${t[j]} $rightState (end iteration)")


        i++
        j++
    }
    /*
    when {
        s[i] == '[' -> { nestLevelLeft++ }
        s[i] == ']' -> { nestLevelLeft-- }
        else -> { println("reading number at left") }
    }

    when {
        t[i] == '[' -> { nestLevelRight++ }
        t[i] == ']' -> { nestLevelRight-- }
        else -> { println("reading number at right") }
    }

    println("${s[i]} vs ${t[i]} nest levels: $nestLevelLeft $nestLevelRight")

*/
    println()

    /*
    for (i in 0 until minOf(s.length, t.length)) {
        when {
            s[i] == t[i] -> {}
            else -> when {
                s[i].isDigit() && t[i].isDigit()
//                println("${s[i]} ${t[i]}")
            }
        }
    }
    */

    return s == t
}
*/

fun format(tokens: List<Token>) =
    tokens.joinToString(" ") {
        when (it) {
            is Token.Number -> it.n.toString()
            is Token.Close -> "]"
            is Token.Open -> "["
        }
    }

fun lookAtTokens(left: List<Token>, right: List<Token>): OrderResult {
//    println("lookingat tokens")
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
//        println("starting with")
//        println(format(left.subList(i, left.lastIndex)))
//        println(format(right.subList(j, right.lastIndex)))

        when {
            leftToken is Token.Number && rightToken is Token.Number -> when {
                leftToken.n < rightToken.n -> return OrderResult(Ordering.Right, false)
                leftToken.n > rightToken.n -> return OrderResult(Ordering.NotRight, false)
            }

            leftToken is Token.Close && (rightToken is Token.Open || rightToken is Token.Number) ->
                return OrderResult(Ordering.Right, false)

            (leftToken is Token.Open || leftToken is Token.Number) && rightToken is Token.Close ->
                return OrderResult(Ordering.NotRight, false)

            leftToken is Token.Number && rightToken is Token.Open -> {
                val nestedLeft = numberToSingleItemList(leftToken)
                val nestedRight = right.subList(j, right.lastIndex)
                val nestedOrder = lookAtTokens(nestedLeft, nestedRight)

                if (!nestedOrder.endReached) return nestedOrder
                println("nested not decided for ${format(nestedLeft)} ${format(nestedRight)}: $nestedOrder, going further")
            }


            leftToken is Token.Open && rightToken is Token.Number -> {
                val nestedLeft = left.subList(i, left.lastIndex)
                val nestedRight = numberToSingleItemList(rightToken)
                val nestedOrder = lookAtTokens(nestedLeft, nestedRight)

                if (!nestedOrder.endReached) return nestedOrder
                println("nested not decided for ${format(nestedLeft)} ${format(nestedRight)}: $nestedOrder, going further")
            }
            /*
            leftToken is Token.Number && rightToken is Token.Open -> {
                val nestedLeft = listOf(Token.Open, leftToken, Token.Close)
                val nestedRight = right.subList(i, right.lastIndex)
                val nestedOrder = lookAtTokens(nestedLeft, nestedRight)

                println("nested (right sublist): ${format(nestedLeft)}\n${format(nestedRight)}\n: $nestedOrder")
                if (!nestedOrder.endReached) return nestedOrder
                println("nested not decided for $left $right, going further")
            }

            leftToken is Token.Open && rightToken is Token.Number -> {
                val nestedLeft = left.subList(i, left.lastIndex)
                val nestedRight = listOf(Token.Open, rightToken, Token.Close)
                val nestedOrder = lookAtTokens(nestedLeft, nestedRight)

                println("nested (left sublist): ${format(nestedLeft)}\n${format(nestedRight)}\n: $nestedOrder")
                if (!nestedOrder.endReached) return nestedOrder
//                println("nested not decided for $left $right, going further")
                println("nested not decided going further to ${format(left.subList(i + 3, left.lastIndex))}\n${format(right.subList(j, right.lastIndex))}")
                i += 3
            }
            */

            else -> {
                throw IllegalStateException("need to analyze situation more: ${left[i]} vs ${right[j]}")
            }
        }
        break
    }

    // [[[[4],0,[4,6],5],[],[3,4]],[1,7,[8],6],[[1,0,[],[2,1,7,5,7],7],[6,10],[],6,[8,[9,9,9,5],2,1]],[7,[],5,[]],[7,[]]]
    // [[[4,1,[10,0]]]]


    // [[[[4],0,[4,6],5],[],[3,4]],[1,7,[8],6],[[1,0,[],[2,1,7,5,7],7],[6,10],[],6,[8,[9,9,9,5],2,1]],[7,[],5,[]],[7,[]]]
    // [[[[4],1,[10,0]]]]

    return when {
        i == left.size && left.size < right.size -> {
            println("right tail: ${format(right.subList(j, right.lastIndex))}")
            println("returning Order.Right")
            println()
            println()
            OrderResult(Ordering.Right, true)
        }
        j == right.size && left.size > right.size -> {
            println("left tail: ${format(left.subList(i, left.lastIndex))}")
            println("returning Order.NotRight")
            println()
            println()
            OrderResult(Ordering.NotRight, true)
        }
        else -> {
            println("tails: ${format(left.subList(i, left.lastIndex))} ${format(right.subList(j, right.lastIndex))}")
            println("returning undecided")
            OrderResult(Ordering.Undecided, true)
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
//                println()
//                println()
//                println()
                lookAtTokens(
                    tokenize(lines[0]),
                    tokenize(lines[1])).ordering == Ordering.Right
            }
//            .also { println("got index: $it") }
            .sumOf { (index, _) -> index + 1 }
//            .also { println("index sum: $it") }
    }
    /*
    fun part1(pairs: List<PacketPair>): Int {
        return pairs
            .withIndex()
            .filter {
                determineOrder(it.value) == OrderResult.Right
            }
            .sumOf { it.index + 1 }
    }
    */

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:

    val testInput = readText("Day13_test")
//    check(part1(testInput) == 13)
//    println(toPackets(readText("Day13_test")))
    check(part1(testInput) == 1)
    check(part2(listOf()) == 0)
    println("tests done")

//    File("out_day13.txt").writeText(toPackets(readText("Day13")))

//    val realInput = mutableListOf<PacketPair>()
//    realInput.addAll(realInput1())
//    println("appended realInput1: ${realInput.size}")
//    realInput.addAll(realInput2())
//    println("appended realInput2: ${realInput.size}")
//    realInput.addAll(realInput3())
//    println("appended realInput3: ${realInput.size}")
//    realInput.addAll(realInput4())
//    println("appended realInput4: ${realInput.size}")
    val realInput = readText("Day13")
    // wrong: 6038 is too low
    println(part1(realInput))
//    println(part2(listOf()))
}