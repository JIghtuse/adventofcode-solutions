data class File(val size: Int)

typealias Files = List<File>

data class Directory(
    val path: String,
    val nestedDirectories: List<Directory>,
    val files: Files)

typealias Directories = List<Directory>

typealias FilesAndDirs = Pair<Files, Directories>


fun joinPath(absolutePath: String, component: String) =
    absolutePath +
        if (absolutePath.last() == '/') component
        else "/$component"

fun dropLastComponent(absolutePath: String) =
    absolutePath.substringBeforeLast('/').ifEmpty { "/" }


fun lsDir(currentPath: String, lsOutput: List<String>): Directory {
    val (files, dirs) = lsOutput
        .fold(listOf<File>() to listOf())
        { acc: FilesAndDirs, x: String ->
            val parts = x.split(" ")
            when (parts[0]) {
                "dir" -> acc.copy(
                    second = acc.second.plus(
                        Directory(joinPath(currentPath, parts[1]), listOf(), listOf())))
                else -> acc.copy(first = acc.first.plus(File(parts[0].toInt())))
            }
        }

    return Directory(currentPath, dirs, files)
}

fun fill(dirWithNoData: Directory, dirWithData: Directory): Directory {
    if (dirWithNoData.path == dirWithData.path) {
        return dirWithData
    }

    val subdirIndex = dirWithNoData.nestedDirectories.indexOfFirst {
        dirWithData.path.startsWith(it.path)
    }

    return dirWithNoData.copy(
        nestedDirectories =
        dirWithNoData.nestedDirectories
            .take(subdirIndex)
            .plus(fill(dirWithNoData.nestedDirectories[subdirIndex], dirWithData))
            .plus(dirWithNoData.nestedDirectories.drop(subdirIndex + 1)))
}


fun buildDirectory(input: List<String>): Directory {
    var i = 0

    var currentPath = "/"
    var root = Directory(currentPath, listOf(), listOf())

    while (i != input.size) {
        val parts = input[i].split(" ")

        when (parts[1]) {
            "ls" -> {
                val start = i + 1

                i += 1
                while (i != input.size && input[i].first() != '$') {
                    i += 1
                }
                root = fill(root, lsDir(currentPath, input.subList(start, i)))
            }

            "cd" -> {
                i += 1
                currentPath = when (parts[2]) {
                    ".." -> dropLastComponent(currentPath)
                    "/" -> "/"
                    else -> joinPath(currentPath, parts[2])
                }
            }
        }
    }

    return root
}

fun display(dir: Directory, level: Int = 0) {
    println("${" ".repeat(level)}${dir.path}")
    dir.files.forEach {
        println("${" ".repeat(level + 2)}$it")
    }
    dir.nestedDirectories.forEach {
        display(it, level + 2)
    }
}

fun directorySize(dir: Directory): Int {
    return dir.files.sumOf { it.size } +
        dir.nestedDirectories.sumOf { directorySize(it) }
}

fun sumDirSizesLessThanOrEqual(dir: Directory, limit: Int): Int {
    fun suitableDir(d: Directory): Boolean {
        return directorySize(d) <= limit
    }

    val nestedSum: Int = dir.nestedDirectories
        .sumOf { d -> sumDirSizesLessThanOrEqual(d, limit) }

    return nestedSum + if (suitableDir(dir)) {
        directorySize(dir)
    } else {
        0
    }
}

fun smallestDirectoryWithSizeUpTo(dir: Directory, limit: Int): Int {
    val size = directorySize(dir)
    val smallestNestedDirectory = dir.nestedDirectories.minOfOrNull {
        smallestDirectoryWithSizeUpTo(it, limit)
    } ?: Int.MAX_VALUE

    return minOf(
        if (size >= limit) size else Int.MAX_VALUE,
        smallestNestedDirectory)
}

fun main() {
    fun part1(input: List<String>): Int {
        val root = buildDirectory(input)

        return sumDirSizesLessThanOrEqual(root, 100_000)
    }

    fun part2(input: List<String>): Int {
        val root = buildDirectory(input)

        val totalDiskSpace = 70_000_000
        val spaceNeededForUpdate = 30_000_000
        val unusedSpace = totalDiskSpace - directorySize(root)
        val needToFree = spaceNeededForUpdate - unusedSpace

        return smallestDirectoryWithSizeUpTo(root, needToFree)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}