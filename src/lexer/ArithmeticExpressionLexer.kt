package lexer

import exceptions.SyntaxError
import operators.*
import java.math.BigInteger

object ArithmeticExpressionLexer {
    fun tokenize(text: String): MutableList<Token> {
        var pos = 0
        val tokens = mutableListOf<Token>()
        val newText = text.replace(" ", "") + ';' // ';' is a stop sign for parser

        while (pos < newText.length) pos = scanNextToken(newText, pos, tokens)

        return tokens
    }

    private fun scanNextToken(text: String, pos: Int, tokens: MutableList<Token>): Int {
        if (text[pos] == ';') return pos + 1

        var tmpPos = pos

        when (text[tmpPos]) {
            in '0'..'9' -> {
                tmpPos = scanNextNumber(text, tmpPos, tokens)
            }
            '-', '+' -> {
                if (tokens.isEmpty()
                        || tokens.last() is Parenthesis && (tokens.last() as Parenthesis).left
                        || tokens.last() is Assignment) {
                    tokens.add(Number(BigInteger.ZERO)) // Dirty hack for unary plus/minus
                }

                var sign = 1
                do {
                    if (text[tmpPos] == '-') sign *= -1
                    ++tmpPos
                } while (text[tmpPos] == '-' || text[tmpPos] == '+')

                tokens.add(Operator(if (sign > 0) PlusOperator else MinusOperator))
            }
            '*' -> {
                tokens.add(Operator(MultiplyOperator))
                ++tmpPos
            }
            '/' -> {
                tokens.add(Operator(DivisionOperator))
                ++tmpPos
            }
            '^' -> {
                tokens.add(Operator(PowerOperator))
                ++tmpPos
            }
            '(', ')' -> {
                tokens.add(Parenthesis(text[tmpPos] == '('))
                ++tmpPos
            }
            '=' -> {
                tokens.add(Assignment())
                ++tmpPos
            }
            in 'a'..'z', in 'A'..'Z' -> {
                tmpPos = scanNextVariable(text, tmpPos, tokens)
            }
            else -> throw SyntaxError("Invalid expression")
        }

        return tmpPos
    }

    private fun scanNextNumber(text: String, pos: Int, tokens: MutableList<Token>): Int {
        var tmpPos = pos

        if (text[tmpPos].isDigit()) {
            var value = ""
            do {
                value += text[tmpPos]
                ++tmpPos
            } while (text[tmpPos].isDigit())
            tokens.add(Number(BigInteger(value)))
        }

        return tmpPos
    }

    private fun scanNextVariable(text: String, pos: Int, tokens: MutableList<Token>): Int {
        var tmpPos = pos

        if (text[tmpPos] in 'a'..'z' || text[tmpPos] in 'A'..'Z') {
            var name = ""
            do {
                name += text[tmpPos]
                ++tmpPos
            } while (text[tmpPos] in 'a'..'z' || text[tmpPos] in 'A'..'Z')
            tokens.add(Variable(name))
        }

        return tmpPos
    }
}