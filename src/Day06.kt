
fun main() {
    fun endOfUniqueSequenceIndex(s: String, sequenceLength: Int): Int {
        return s
            .windowed(sequenceLength, 1)
            .indexOfFirst {
                it.toSet().size == sequenceLength
            } + sequenceLength
    }

    fun part1(input: String) = endOfUniqueSequenceIndex(input, 4)

    fun part2(input: String) = endOfUniqueSequenceIndex(input, 14)

    // test if implementation meets criteria from the description, like:
    val testInput = readLine("Day06_test")
    check(part1(testInput) == 7)
    check(part1("bvwbjplbgvbhsrlpgdmjqwftvncz") == 5)
    check(part1("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 10)
    check(part1("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") == 11)

    check(part2(testInput) == 19)
    check(part2("bvwbjplbgvbhsrlpgdmjqwftvncz") == 23)
    check(part2("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 29)
    check(part2("nppdvjthqldpwncqszvftbrmjlhg") == 23)
    check(part2("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") == 26)


    val input = readLine("Day06")
    println(part1(input))
    println(part2(input))
}