package processor

fun toIntList(s: String): List<Int> {
    return s.split(" ").filter { it.isNotEmpty() }.map { it.toInt() }
}

class Matrix(val items: Array<List<Int>>) {
    val rows: Int = items.size
    val columns: Int = if (items.isEmpty()) 0 else items[0].size

    operator fun plus(other: Matrix): Matrix {
        if (rows != other.rows) throw Exception("Could not sum matrix of different dimensions (rows)")
        if (columns != other.columns) throw Exception("Could not sum matrix of different dimensions (columns)")

        return Matrix(Array(rows) { items[it].zip(other.items[it]) { x, y -> x + y } })
    }

    operator fun times(scalar: Int): Matrix {
        return Matrix(Array(rows) { i -> List(columns) { j -> items[i][j] * scalar } })
    }

    override fun toString(): String {
        return items.joinToString("\n") { it.joinToString(" ") }
    }

    companion object {
        fun readFromStdin(): Matrix {
            val (rows, columns) = toIntList(readLine()!!)

            return Matrix(Array(rows) { toIntList(readLine()!!) })
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