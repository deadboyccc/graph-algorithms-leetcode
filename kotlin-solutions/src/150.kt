package one50;

class Solution {
    fun evalRPN(tokens: Array<String>): Int {
        val stack = ArrayDeque<Int>()
        val operators = setOf("+", "-", "*", "/")

        tokens.forEach { token ->
            if (token in operators) {
                val right = stack.removeLast()
                val left = stack.removeLast()
                val res = when (token) {
                    "+" -> left + right
                    "-" -> left - right
                    "*" -> left * right
                    else -> left / right
                }
                stack.addLast(res)
            } else {
                stack.addLast(token.toInt())
            }
        }

        return stack.last()
    }
}