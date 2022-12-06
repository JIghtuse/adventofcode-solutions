import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

fun readLine(name: String) = File("src", "$name.txt")
    .readLines().first()

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

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
