const val strengthValues: String = "--23456789TJQKA"

const val strengthValuesWithJoker: String = "-J23456789TQKA"

val jokerStrength = strengthValuesWithJoker.indexOf('J')

enum class HandType {
    HighCard,
    OnePair,
    TwoPair,
    ThreeOfAKind,
    FullHouse,
    FourOfAKind,
    FiveOfAKind,
}

data class Hand(val cards: IntArray, val type: HandType, val bid: Int)

fun handTypePart1(cards: IntArray): HandType {
    val groups = cards
        .toSet()
        .map { x ->
            cards.count { it == x } to x
        }
        .sortedWith(compareBy({ it.first }, { it.second }))
        .asReversed()

    val groupCount = groups.size
    val firstGroupCount = groups.first().first

    return when {
        groupCount == 1 && firstGroupCount == 5 -> HandType.FiveOfAKind
        groupCount == 2 && firstGroupCount == 4 -> HandType.FourOfAKind
        groupCount == 2 && firstGroupCount == 3 && groups[1].first == 2 -> HandType.FullHouse
        groupCount == 3 && firstGroupCount == 3 -> HandType.ThreeOfAKind
        groupCount == 3 && firstGroupCount == 2 && groups[1].first == 2 -> HandType.TwoPair
        groupCount == 4 && firstGroupCount == 2 -> HandType.OnePair
        else -> HandType.HighCard
    }
}

fun handTypePart2(cards: IntArray): HandType {
    val hasJoker = jokerStrength in cards

    if (!hasJoker) return handTypePart1(cards)

    val groups = cards
        .toSet()
        .map { x ->
            cards.count { it == x } to x
        }
        .sortedWith(compareBy({ it.first }, { it.second }))
        .asReversed()

    val groupCount = groups.size
    val firstGroupCount = groups.first().first

    return when {
        groupCount == 1 && firstGroupCount == 5 -> HandType.FiveOfAKind
        groupCount == 2 && firstGroupCount == 4 -> HandType.FiveOfAKind
        groupCount == 2 && firstGroupCount == 3 && groups[1].first == 2 -> HandType.FiveOfAKind
        groupCount == 3 && firstGroupCount == 3 -> HandType.FourOfAKind
        groupCount == 3 && firstGroupCount == 2 && groups[1].first == 2 && groups.last().second == jokerStrength -> HandType.FullHouse
        groupCount == 3 && firstGroupCount == 2 && groups[1].first == 2 -> HandType.FourOfAKind
        groupCount == 4 && firstGroupCount == 2 -> HandType.ThreeOfAKind
        else -> HandType.OnePair
    }
}

fun totalWinnings(hands: List<Hand>): Int {
    return hands
        .sortedWith(
            compareBy(
                { it.type.ordinal },
                { it.cards[0] },
                { it.cards[1] },
                { it.cards[2] },
                { it.cards[3] },
                { it.cards[4] })
        )
        .withIndex()
        .sumOf { (index, hand) ->
            (index + 1) * hand.bid
        }
}

fun main() {
    fun part1(input: List<String>): Int {
        val hands = input.map{ line ->
            val cards = line
                .substringBefore(" ")
                .map{ strengthValues.indexOf(it) }
                .toIntArray()
            val bid = line.substringAfter(" ").toInt()
            Hand(cards, handTypePart1(cards), bid)
        }

        return totalWinnings(hands)
    }

    fun part2(input: List<String>): Int {
        val hands = input.map{ line ->
            val cards = line
                .substringBefore(" ")
                .map{ strengthValuesWithJoker.indexOf(it) }
                .toIntArray()
            val bid = line.substringAfter(" ").toInt()
            Hand(cards, handTypePart2(cards), bid)
        }

        return totalWinnings(hands)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
