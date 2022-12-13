import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

fun readText(name: String) = File("src", "$name.txt")
    .readText()

fun readLine(name: String) = File("src", "$name.txt")
    .readLines().first()

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

fun read2dArray(name: String): Array<IntArray> = File("src", "$name.txt")
    .readLines()
    .map { it.chars().map { it - '0'.code }.toArray() }
    .toTypedArray()

fun readBundledNumbers(name: String) = File("src", "$name.txt")
    .readText()
    .split("\n\n")
    .map { bundle -> bundle.split("\n") }
    .map { bundle -> bundle.map { s -> s.toInt() } }

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')
