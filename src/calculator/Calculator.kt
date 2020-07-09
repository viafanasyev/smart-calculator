package calculator

import exceptions.SyntaxError
import exceptions.VariableError
import lexer.*
import lexer.Number
import java.math.BigInteger

fun <T> MutableList<T>.removeLast(): T {
    return removeAt(lastIndex)
}

class Calculator {
    private val variables = mutableMapOf<String, BigInteger>()

    companion object {
        fun isCorrectIdentifier(name: String): Boolean {
            for (c in name) if (c !in 'a'..'z' && c !in 'A'..'Z') return false
            return true
        }
    }

    private fun calculate(command: String): BigInteger {
        val tokens = ArithmeticExpressionLexer.tokenize(command)
        return calculateReversePolishNotation(toReversePolishNotation(tokens))
    }

    private fun toReversePolishNotation(tokens: MutableList<Token>): MutableList<Token> {
        val rpnTokens = mutableListOf<Token>()
        val stack = mutableListOf<Token>()
        for (token in tokens) {
            if (token is Number || token is Variable) {
                rpnTokens.add(token)
            } else {
                if (token is Parenthesis) {
                    if (token.left) {
                        stack.add(token)
                    } else {
                        while (!(stack.last() is Parenthesis && (stack.last() as Parenthesis).left)) {
                            rpnTokens.add(stack.last())
                            stack.removeLast()

                            if (stack.isEmpty()) throw SyntaxError("Invalid expression")
                        }
                        stack.removeLast()
                    }
                } else if (stack.isEmpty() || stack.last() is Parenthesis && (stack.last() as Parenthesis).left) {
                        stack.add(token)
                } else if (token is Operator && token.op.priority > (stack.last() as Operator).op.priority) {
                    stack.add(token)
                } else if (token is Operator && token.op.priority <= (stack.last() as Operator).op.priority) {
                    do {
                        rpnTokens.add(stack.last())
                        stack.removeLast()
                    } while (!(stack.isEmpty() || stack.last() is Parenthesis && (stack.last() as Parenthesis).left || token.op.priority > (stack.last() as Operator).op.priority))
                    stack.add(token)
                } else {
                    throw SyntaxError("Invalid expression")
                }
            }
        }

        while (stack.isNotEmpty()) {
            if (stack.last() is Parenthesis) throw SyntaxError("Invalid expression")

            rpnTokens.add(stack.last())
            stack.removeLast()
        }

        return rpnTokens
    }

    private fun calculateReversePolishNotation(tokens: MutableList<Token>): BigInteger {
        try {
            val stack = mutableListOf<BigInteger>()
            for (token in tokens) {
                when (token) {
                    is Number -> stack.add(token.value)
                    is Variable -> {
                        if (variables.contains(token.name)) {
                            stack.add(variables[token.name]!!)
                        } else {
                            throw VariableError("Unknown variable")
                        }
                    }
                    is Operator -> {
                        val right = stack.removeLast()
                        val left = stack.removeLast()
                        stack.add(token.op.apply(left, right))
                    }
                    else -> throw SyntaxError("Invalid expression")
                }
            }
            return stack.removeLast()
        } catch (e: IndexOutOfBoundsException) {
            throw SyntaxError("Invalid expression")
        }
    }

    private fun processAssignment(command: String) {
        var pos = 0
        while (pos < command.length && command[pos] != '=')
            ++pos
        if (pos == command.length)
            throw SyntaxError("Not an assignment")

        val name = command.substring(0, pos).trim()
        if (!isCorrectIdentifier(name)) throw VariableError("Invalid identifier")

        try {
            variables[name] = calculate(command.substring(pos + 1))
        } catch (e: SyntaxError) {
            throw SyntaxError("Invalid assignment")
        } catch (e: VariableError) {
            throw SyntaxError("Invalid assignment")
        }
    }

    fun processCommand(command: String) {
        try {
            if (command.contains('=')) processAssignment(command) else println(calculate(command))
        } catch (e: IllegalArgumentException) {
            println(e.message)
        }
    }
}