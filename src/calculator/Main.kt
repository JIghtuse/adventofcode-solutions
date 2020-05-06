package calculator

import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)
    val x = scanner.nextInt()
    val y = scanner.nextInt()

    val sum = x + y

    println(sum)
}