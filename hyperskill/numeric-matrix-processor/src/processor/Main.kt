package processor

fun toIntList(s: String): List<Int> {
    return s.split(" ").filter { it.isNotEmpty() }.map { it.toInt() }
}

fun toDoubleList(s: String): List<Double> {
    return s.split(" ").filter { it.isNotEmpty() }.map { it.toDouble() }
}

class Matrix(val items: Array<List<Double>>) {
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

    operator fun times(other: Matrix): Matrix {
        if (columns != other.rows) throw Exception("Invalid matrix dimensions")
        return Matrix(Array(rows) { i -> List(other.columns) { j -> dotProduct(other.items, i, j) } })
    }

    private fun dotProduct(otherItems: Array<List<Double>>, thisRow: Int, otherColumn: Int): Double {
        var p = 0.0
        for (k in 0 until columns) {
            p += items[thisRow][k] * otherItems[k][otherColumn]
        }
        return p
    }

    override fun toString(): String {
        return items.joinToString("\n") { it.joinToString(" ") }
    }

    companion object {
        fun readFromStdin(name: String): Matrix {
            print("Enter size of $name matrix: ")
            val (rows, columns) = toIntList(readLine()!!)

            println("Enter $name matrix:")
            return Matrix(Array(rows) { toDoubleList(readLine()!!) })
        }
    }
}

fun main() {
    val actions = arrayListOf(
            "Exit",
            "Add matrices",
            "Multiply matrix to a constant",
            "Multiply matrices")

    val fs = arrayListOf(
            fun() {
                val a = Matrix.readFromStdin("first")
                val b = Matrix.readFromStdin("second")
                println("The sum result is: ")
                println(a + b)
            },
            fun() {
                val a = Matrix.readFromStdin("a")
                print("Enter a constant: ")
                val c = readLine()!!.toInt()
                println("The multiplication result is: ")
                println(a * c)
            },
            fun() {
                val a = Matrix.readFromStdin("first")
                val b = Matrix.readFromStdin("second")
                println("The multiplication result is: ")
                println(a * b)
            }
    )

    fun printActions() {
        for (i in 1..actions.lastIndex) {
            println("$i. ${actions[i]}")
        }
        println("0. ${actions[0]}")
    }

    runLoop@ while (true) {
        printActions()
        val action = readLine()!!.toInt()

        try {
            when (action) {
                in 1..3 -> fs[action - 1]()
                0 -> break@runLoop
                else -> throw Exception("Unknown action $action")
            }
        } catch (e: java.lang.Exception) {
            println("Error: ${e.message}")
        }
    }
}
