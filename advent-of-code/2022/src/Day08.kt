fun main() {
    fun isVisibleTree(grid: Array<IntArray>, row: Int, col: Int): Boolean {
        val treeHeight = grid[row][col]

        return (0 until row).all { grid[it][col] < treeHeight }
        || (row + 1..grid.lastIndex).all { grid[it][col] < treeHeight }
        || (0 until col).all { grid[row][it] < treeHeight }
        || (col + 1..grid.first().lastIndex).all { grid[row][it] < treeHeight }
    }

    fun countInteriorVisibleTrees(grid: Array<IntArray>): Int {
        val rows = 1 until grid.lastIndex
        val cols = 1 until grid.first().lastIndex
        var visibleTrees = 0

        for (row in rows) {
            for (col in cols) {
                if (isVisibleTree(grid, row, col)) {
                    visibleTrees += 1
                }
            }
        }

        return visibleTrees
    }

    fun countVisibleTrees(grid: Array<IntArray>): Int {
        val edge = 2 * (grid.size + grid.first().size - 2)

        return edge + countInteriorVisibleTrees(grid)
    }

    fun part1(grid: Array<IntArray>): Int {
        return countVisibleTrees(grid)
    }

    fun scenicScore(grid: Array<IntArray>, row: Int, col: Int): Int {
        val treeHeight = grid[row][col]

        fun colMax(range: IntProgression): Int {
            val totalLength = range.asIterable().count { true }

            val scenePartCount = range.takeWhile {
                grid[it][col] < treeHeight
            }.count()

            return scenePartCount + if (scenePartCount == totalLength) {
                0
            } else {
                1
            }
        }

        fun rowMax(range: IntProgression): Int {
            val totalLength = range.asIterable().count { true }

            val scenePartCount = range.takeWhile {
                grid[row][it] < treeHeight
            }.count()

            return scenePartCount + if (scenePartCount == totalLength) {
                0
            } else {
                1
            }
        }

        val up = colMax((0 until row).reversed())
        val down = colMax(row + 1..grid.lastIndex)

        val left = rowMax((0 until col).reversed())
        val right = rowMax(col + 1..grid.first().lastIndex)

        return left * right * up * down
    }

    fun highestScenicScore(grid: Array<IntArray>): Int {
        var maxScore = scenicScore(grid, 1, 1)
        for (row in 1 until grid.lastIndex) {
            for (col in 1 until grid.first().lastIndex) {
                val score = scenicScore(grid, row, col)
                maxScore = maxOf(maxScore, score)
            }
        }
        return maxScore
    }

    fun part2(grid: Array<IntArray>): Int {
        return highestScenicScore(grid)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = read2dArray("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = read2dArray("Day08")
    println(part1(input))
    println(part2(input))
}