package search

import java.io.File
import java.lang.IllegalArgumentException

typealias Dataset = List<String>
typealias InvertedIndex = Map<String, List<Int>>

fun toLowercaseWords(s: String) = s.split(" ").map(String::lowercase)

fun ask(prompt: String): String {
    println(prompt)
    return readln()
}

fun scanInputFile(filePath: String): Pair<Dataset, InvertedIndex> {
    val lines = mutableListOf<String>()
    val invertedIndex = mutableMapOf<String, MutableList<Int>>()

    var lineIndex = 0
    val file = File(filePath)
    file.forEachLine {
        lines.add(it)
        for (word in toLowercaseWords(it).filter(String::isNotEmpty)) {
            val positions = invertedIndex.getOrDefault(word, mutableListOf())
            positions.add(lineIndex)
            invertedIndex[word] = positions
        }
        lineIndex += 1
    }

    return lines to invertedIndex
}

fun printPeople(dataset: Dataset) {
    println("=== List of people ===")
    dataset.forEach(::println)
}

fun reportResult(matchedItems: Dataset) {
    if (matchedItems.isNotEmpty()) {
        println("${matchedItems.size} persons found:")
        printPeople(matchedItems)
    } else {
        println("No matching people found.")
    }
}

enum class MatchOption {
    ALL,
    ANY,
    NONE,
}

fun toMatchOption(s: String): MatchOption {
    return MatchOption.valueOf(s.uppercase())
}

class Searcher(private val dataset: Dataset, private val invertedIndex: InvertedIndex) {
    fun search(query: String, matchOption: MatchOption): Dataset {
        return when (matchOption) {
            MatchOption.ALL -> ::searchAll
            MatchOption.ANY -> ::searchAny
            MatchOption.NONE -> ::searchNone
        }(toLowercaseWords(query))
    }

    private fun searchAny(queryWords: List<String>): Dataset {
        return queryWords.flatMap { word ->
            invertedIndex
                .getOrDefault(word, mutableListOf())
                .map { dataset[it] }
        }
    }

    private fun searchAll(queryWords: List<String>): Dataset {
        if (queryWords.isEmpty()) return listOf()

        return queryWords
            .map { word ->
                invertedIndex
                    .getOrDefault(word, mutableListOf())
                    .toSet()
            }
            .reduce { acc, indices -> acc.intersect(indices) }
            .map { dataset[it] }
    }

    private fun searchNone(queryWords: List<String>): Dataset {
        if (queryWords.isEmpty()) return dataset

        val allIndices = (0..dataset.lastIndex)
        val anyIndices = queryWords
            .flatMap { word ->
                invertedIndex
                    .getOrDefault(word, mutableListOf())
            }
            .toSet()

        return allIndices
            .subtract(anyIndices)
            .map { dataset[it] }
    }
}

fun createSearcherAndDataset(dataFilePath: String): Pair<Searcher, Dataset> {
    val (dataset, invertedIndex) = scanInputFile(dataFilePath)
    return Searcher(dataset, invertedIndex) to dataset
}

fun matchOptionToQueryPrompt(matchOption: MatchOption): String {
    return when (matchOption) {
        MatchOption.NONE -> "Enter a name or email to search none matching people."
        MatchOption.ANY -> "Enter a name or email to search any matching people."
        MatchOption.ALL -> "Enter a name or email to search all matching people."
    }
}

fun searchAndReportResult(searcher: Searcher) {
    val matchOption = toMatchOption(ask("Select a matching strategy: ALL, ANY, NONE"))
    val query = ask(matchOptionToQueryPrompt(matchOption))

    val matchedItems = searcher.search(query, matchOption)
    reportResult(matchedItems)
}

data class MenuItem(val name: String, val action: () -> Unit)

class Menu(exitItemNumber: Int, private val items: Map<Int, MenuItem>) {
    private val exitItem = exitItemNumber.toString()

    init {
        require(!items.containsKey(exitItemNumber))
    }

    val prompt = buildString {
        append("=== Menu ===")
        append("\n")
        items
            .entries
            .forEach { command ->
                append("${command.key}. ${command.value.name}")
                append("\n")
            }
        append("$exitItem. Exit")
        append("\n")
    }

    fun run(userChoice: String): Boolean {
        if (userChoice == exitItem) {
            return false
        }

        try {
            val menuIndex = userChoice.toInt()
            require(items.containsKey(menuIndex))

            items[menuIndex]!!.action()
        } catch (e: IllegalArgumentException) {
            println("Incorrect option! Try again.")
        }

        return true
    }
}

fun main(args: Array<String>) {
    require(args.size == 2)
    require(args[0] == "--data")

    val (searcher, dataset) = createSearcherAndDataset(args[1])

    val menu = Menu(
        0,
        mapOf(
            1 to MenuItem("Find a person") { searchAndReportResult(searcher) },
            2 to MenuItem("Print all people") { printPeople(dataset) })
    )

    while (true) {
        val moreToRun = menu.run(ask(menu.prompt))
        if (!moreToRun) break
    }

    println("Bye!")
}
