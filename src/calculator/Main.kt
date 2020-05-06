package calculator

import java.lang.NumberFormatException
import java.math.BigInteger
import kotlin.math.pow

enum class Token(val defaultChar: Char) {
    Power('^'),
    Multiply('*'),
    Divide('/'),
    Minus('-'),
    Plus('+'),
    Assignment('='),
    Number('0'),
    Identifier('a'),
    LParen('('),
    RParen(')');

    companion object {
        fun fromDefaultChar(c: Char): Token {
            for (dc in values()) {
                if (dc.defaultChar == c) {
                    return dc
                }
            }
            throw Exception("Invalid expression")
        }
    }
}

fun isParenthesis(t: Token) = t in arrayListOf(Token.LParen, Token.RParen)

fun isAssignment(tokens: List<Pair<Token, String>>) = tokens.size > 1 && tokens[1].first == Token.Assignment

fun precedence(t: Token): Int {
    return when (t) {
        Token.Power -> 4
        Token.Multiply, Token.Divide -> 3
        Token.Minus, Token.Plus -> 2
        Token.Assignment -> 1
        else -> throw Exception("Not an operator: $t")
    }
}

// collapses sequence of +- into a single "winning" character, reporting number of consumed characters
fun collapseSign(s: String): Pair<String, Int> {
    var charsEaten = 0
    var sign = +1
    for (c in s) {
        if (c == '-') {
            sign *= -1
        } else if (c != '+') {
            break
        }
        charsEaten += 1
    }
    return Pair(if (sign == -1) "-" else "+", charsEaten)
}

fun makePlusOrMinusToken(s: String): Pair<Pair<Token, String>, Int> {
    val (sign, charsEaten) = collapseSign(s)
    if (sign == "-") return Pair(Pair(Token.Minus, "-"), charsEaten)
    return Pair(Pair(Token.Plus, "+"), charsEaten)
}

fun makeTokenWithSign(token: Token, value: String): Pair<Pair<Token, String>, Int> {
    val (sign, charsEaten) = collapseSign(value)
    val valuePart = value.drop(charsEaten)
    return Pair(Pair(token, sign + valuePart), value.length)
}

fun makeNumberToken(value: String): Pair<Pair<Token, String>, Int> {
    return makeTokenWithSign(Token.Number, value)
}

fun makeIdentifierToken(value: String): Pair<Pair<Token, String>, Int> {
    return makeTokenWithSign(Token.Identifier, value)
}

fun makeOperatorToken(c: Char): Pair<Pair<Token, String>, Int> {
    fun makeTokenValue(token: Token) = Pair(Pair(token, c.toString()), 1)

    if (c in "^*/+-()=") return makeTokenValue(Token.fromDefaultChar(c))

    throw Exception("Invalid expression")
}

// scans for a few tokens in input string and appends them to input list
// returns number of characters eaten from input string
fun appendTokens(s: String, tokens: MutableList<Pair<Token, String>>): Int {
    val plusMinusRe = "^[-+]+".toRegex()
    val numberRe = "^[-+]*\\d+".toRegex()
    val identifierRe = "^[-+]*[a-zA-Z]+".toRegex()

    fun append(tokenAndCharsEaten: Pair<Pair<Token, String>, Int>): Int {
        val (token, charsEaten) = tokenAndCharsEaten
        tokens.add(token)
        return charsEaten
    }

    fun appendTokenWithSign(tokenAndCharsEaten: Pair<Pair<Token, String>, Int>): Int {
        // one of:
        //  2+2
        //  b+2
        //  )+2
        // we need to prepend such token with +, because + in expression was glued to token
        fun isEndExpressionForPlusMinus(t: Token) = t in arrayListOf(Token.RParen, Token.Identifier, Token.Number)

        if (tokens.isNotEmpty() && isEndExpressionForPlusMinus(tokens.last().first)) {
            append(makeOperatorToken('+'))
        }
        return append(tokenAndCharsEaten)
    }

    val number = numberRe.find(s)
    if (number != null) return appendTokenWithSign(makeNumberToken(number.value))

    val identifier = identifierRe.find(s)
    if (identifier != null) return appendTokenWithSign(makeIdentifierToken(identifier.value))

    val plusOrMinus = plusMinusRe.find(s)
    if (plusOrMinus != null) return append(makePlusOrMinusToken(plusOrMinus.value))

    if (s.first() in "^*/()=") return append(makeOperatorToken(s.first()))

    throw Exception("Invalid expression")
}

fun tokenize(s: String): List<Pair<Token, String>> {
    val tokens = mutableListOf<Pair<Token, String>>()

    var stringToParse = s.trim()

    while (stringToParse.isNotEmpty()) {
        val charsEaten = appendTokens(stringToParse, tokens)
        stringToParse = stringToParse.drop(charsEaten).trimStart()
    }

    return tokens
}

fun intoPostfixNotation(infixTokens: List<Pair<Token, String>>): List<Pair<Token, String>> {
    val result = mutableListOf<Pair<Token, String>>()
    val stack = mutableListOf<Pair<Token, String>>()

    fun lastToken() = stack.last().first
    fun putResultFromStack() = result.add(stack.removeAt(stack.lastIndex))

    for (token in infixTokens) {
        val type = token.first

        if (type == Token.Identifier || type == Token.Number) result.add(token)
        else if (isParenthesis(type)) {
            if (type == Token.LParen) stack.add(token)
            else if (type == Token.RParen) {
                while (stack.isNotEmpty() && lastToken() != Token.LParen) {
                    putResultFromStack()
                }
                if (stack.isEmpty() || lastToken() != Token.LParen) throw Exception("Invalid expression")
                stack.removeAt(stack.lastIndex)
            }
        }
        // t is an operator token, all other types processed earlier
        else if (stack.isEmpty()) stack.add(token)
        else if (lastToken() == Token.LParen) stack.add(token)
        else if (precedence(lastToken()) < precedence(type)) stack.add(token)
        else {
            while (stack.isNotEmpty() && lastToken() != Token.LParen && precedence(lastToken()) >= precedence(type)) {
                putResultFromStack()
            }
            stack.add(token)
        }
    }
    while (stack.isNotEmpty()) {
        if (isParenthesis(lastToken())) throw Exception("Invalid expression")
        putResultFromStack()
    }
    return result
}

fun evaluatePostfixExpression(postfixTokens: List<Pair<Token, String>>, activeVariables: Map<String, BigInteger>): BigInteger {
    if (postfixTokens.isEmpty()) throw Exception("Invalid expression")

    val stack = mutableListOf<BigInteger>()

    fun extractBinaryOperands(): Pair<BigInteger, BigInteger> {
        if (stack.size < 2) throw Exception("Invalid expression")
        val y = stack.removeAt(stack.lastIndex)
        val x = stack.removeAt(stack.lastIndex)
        return Pair(x, y)
    }

    fun performBinaryOperation(operation: BigInteger.(BigInteger) -> BigInteger) {
        val (x, y) = extractBinaryOperands()
        stack.add(operation(x, y))
    }

    for ((tokenType, tokenValue) in postfixTokens) {
        when (tokenType) {
            Token.Number -> stack.add(tokenValue.toBigInteger())
            Token.Identifier -> stack.add(getVariableValue(activeVariables, tokenValue))

            Token.Plus -> performBinaryOperation(BigInteger::plus)
            Token.Minus -> performBinaryOperation(BigInteger::minus)
            Token.Divide -> {
                val (x, y) = extractBinaryOperands()
                if (y == BigInteger.ZERO) throw Exception("Invalid expression")
                stack.add(x / y)
            }
            Token.Multiply -> performBinaryOperation(BigInteger::times)
            Token.Power -> {
                val (x, y) = extractBinaryOperands()
                stack.add(x.toDouble().pow(y.toInt()).toLong().toBigInteger())
            }

            // all other valid types processed in other place (parenthesis, assignment)
            else -> throw Exception("Invalid expression")
        }
    }
    return stack.last()
}

fun evaluateInfixExpression(tokens: List<Pair<Token, String>>, activeVariables: Map<String, BigInteger>): BigInteger {
    return evaluatePostfixExpression(intoPostfixNotation(tokens), activeVariables)
}

fun breakOn(s: String, index: Int) = Pair(s.substring(0, index), s.substring(index))

fun signToNumber(s: String): BigInteger = if (s == "-") -BigInteger.ONE else BigInteger.ONE

fun getVariableValue(activeVariables: Map<String, BigInteger>, signedName: String): BigInteger {
    val (sign, name) = breakOn(signedName, 1)

    val rightHandSideValue = activeVariables[name]
    if (rightHandSideValue != null) {
        return signToNumber(sign) * rightHandSideValue
    } else {
        throw Exception("Unknown variable")
    }
}

fun printHelp() {
    println("This program is a calculator")
    println("You can input numbers with operands + - * / between them to calculate some expression")
    println("Parenthesis is supported: ()")
    println("You can define variables with syntax \"varName = value\" and use them in expressions")
}

fun main() {
    val activeVariables = mutableMapOf<String, BigInteger>()

    while (true) {
        val line = readLine()!!

        if (line.isEmpty()) continue

        if (line.first() == '/') {
            val command = line.drop(1)
            if (command == "exit") break
            if (command == "help") {
                printHelp()
                continue
            }
            println("Unknown command")
            continue
        }
        try {
            val tokens = tokenize(line)
            if (isAssignment(tokens)) {
                val (tokenType, tokenValue) = tokens.first()
                if (tokenType != Token.Identifier) throw Exception("Invalid identifier")

                val value = evaluateInfixExpression(tokens.drop(2), activeVariables)
                activeVariables[tokenValue.drop(1)] = value
            } else {
                val result = evaluateInfixExpression(tokens, activeVariables)
                println(result)
            }
        } catch (e: NumberFormatException) {
            println("Invalid expression")
        } catch (e: java.lang.Exception) {
            println(e.message)
        }
    }

    println("Bye!")
}
