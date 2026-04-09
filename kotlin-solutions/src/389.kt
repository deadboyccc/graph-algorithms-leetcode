package three89;

class Solution {
    fun findTheDifference(s: String, t: String): Char {
        // Create a mutable map to track counts
        val countMap = mutableMapOf<Char, Int>()

        for (char in s) {
            countMap[char] = countMap.getOrDefault(char, 0) + 1
        }

        for (char in t) {
            val count = countMap.getOrDefault(char, 0)
            if (count == 0) {
                // If the character isn't in the map or count is exhausted,
                // this is our added character.
                return char
            }
            countMap[char] = count - 1
        }

        throw IllegalArgumentException("No difference found")
    }
}

fun main() {
    Solution().findTheDifference("abcd", "abcde").let { println(it) }
}