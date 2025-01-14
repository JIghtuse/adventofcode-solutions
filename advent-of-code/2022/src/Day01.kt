fun main() {
    fun mostCaloriesCarriedBySingleElf(input: List<List<Int>>) =
        input.maxOfOrNull { bundle -> bundle.sum() }

    fun mostCaloriesCarriedByThreeElves(input: List<List<Int>>): Int {
        val caloriesPerElf = input
            .map { bundle -> bundle.sum() }
            .sorted()
        
        return caloriesPerElf.slice(caloriesPerElf.lastIndex - 2..caloriesPerElf.lastIndex).sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readBundledNumbers("Day01_test")
    check(mostCaloriesCarriedBySingleElf(testInput) == 24000)

    val input = readBundledNumbers("Day01")
    println(mostCaloriesCarriedBySingleElf(input))
    println(mostCaloriesCarriedByThreeElves(input))
}
