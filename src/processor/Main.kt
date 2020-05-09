package processor

fun toIntList(s: String): List<Int> {
    return s.split(" ").filter { it.isNotEmpty() }.map { it.toInt() }
}

fun toArrayList(xs: List<Int>): ArrayList<Int> {
    val arrayList = arrayListOf<Int>()

    for (item in xs) {
        arrayList.add(item)
    }

    return arrayList
}

class Matrix(val items: ArrayList<ArrayList<Int>>) {
    val rows: Int = items.size
    val columns: Int = if (items.isEmpty()) 0 else items[0].size

    operator fun plus(other: Matrix): Matrix {
        if (rows != other.rows) throw Exception("Could not sum matrix of different dimensions (rows)")
        if (columns != other.columns) throw Exception("Could not sum matrix of different dimensions (columns)")

        val sumMatrixItems = arrayListOf<ArrayList<Int>>()
        repeat(rows) {
            val row = arrayListOf<Int>()
            repeat(columns) { jt ->
                row.add(getItem(it, jt) + other.getItem(it, jt))
            }
            sumMatrixItems.add(row)
        }

        return Matrix(sumMatrixItems)
    }

    operator fun times(scalar: Int): Matrix {
        val newItems = arrayListOf<ArrayList<Int>>()
        for (row in items) {
            val newRow = arrayListOf<Int>()
            row.forEach { newRow.add(it * scalar) }
            newItems.add(newRow)
        }
        return Matrix(newItems)
    }

    override fun toString(): String {
        return items.joinToString("\n") { it.joinToString(" ") }
    }

    private fun getItem(row: Int, col: Int): Int {
        if (row >= rows) throw Exception("Asked for row $row, matrix has only $rows rows.")
        if (col >= columns) throw Exception("Asked for column $col, matrix has only $columns columns.")
        return items[row][col]
    }

    companion object {
        fun readFromStdin(): Matrix {
            val dimensions = toIntList(readLine()!!)
            assert(dimensions.size == 2)

            val items = arrayListOf<ArrayList<Int>>()
            repeat (dimensions[0]) {
                val row = toArrayList(toIntList(readLine()!!))
                assert(row.size == dimensions[1])

                items.add(row)
            }
            return Matrix(items)
        }
    }
}


fun main() {
    val a = Matrix.readFromStdin()
    val scalar = readLine()!!.trim().toInt()

    try {
        println(a * scalar)
    } catch (_: java.lang.Exception) {
        println("ERROR")
    }
}
