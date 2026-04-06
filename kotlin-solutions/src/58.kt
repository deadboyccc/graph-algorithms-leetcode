package five8;

class Solution {
    fun lengthOfLastWord(s: String): Int {

        if (s.isEmpty()) return 0
        var length = 0

        val trimmedStirng = s.trimIndent().trimEnd { it == ' ' }

        for (i in trimmedStirng.length - 1 downTo 0) {
            if (trimmedStirng[i] == ' ') {
                break
            }

            length++

        }

        return length
    }
}

fun main() {
    val s =
        "   fly me   to   the moon  "
    Solution().lengthOfLastWord(s).let(::println)
}