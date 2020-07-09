package lexer

import operators.AbstractBinaryOperator
import java.math.BigInteger

open class Token(val type: TokenType)

enum class TokenType {
    NUMBER, OPERATOR, PARENTHESIS, ASSIGNMENT, VARIABLE
}

data class Operator(val op: AbstractBinaryOperator) : Token(TokenType.OPERATOR)

data class Number(val value: BigInteger) : Token(TokenType.NUMBER)

data class Parenthesis(val left: Boolean) : Token(TokenType.PARENTHESIS)

class Assignment : Token(TokenType.ASSIGNMENT)

data class Variable(val name: String) : Token(TokenType.VARIABLE)