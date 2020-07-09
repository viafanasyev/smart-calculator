package operators

import java.math.BigInteger

abstract class AbstractBinaryOperator(val priority: Int) {
    abstract fun apply(left: BigInteger, right: BigInteger): BigInteger
}

object PlusOperator : AbstractBinaryOperator(1) {
    override fun apply(left: BigInteger, right: BigInteger) = left + right
}

object MinusOperator : AbstractBinaryOperator(1) {
    override fun apply(left: BigInteger, right: BigInteger) = left - right
}

object MultiplyOperator : AbstractBinaryOperator(2) {
    override fun apply(left: BigInteger, right: BigInteger) = left * right
}

object DivisionOperator : AbstractBinaryOperator(2) {
    override fun apply(left: BigInteger, right: BigInteger) = left / right
}

object PowerOperator : AbstractBinaryOperator(3) {
    override fun apply(left: BigInteger, right: BigInteger): BigInteger {
        if (right == BigInteger.ZERO) return BigInteger.ONE
        return if (right % 2.toBigInteger() == BigInteger.ONE) left * apply(left, right - BigInteger.ONE)
        else apply(left * left, right / 2.toBigInteger())
    }
}