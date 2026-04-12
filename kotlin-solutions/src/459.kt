package four59

class Solution {
    fun repeatedSubstringPattern(s: String): Boolean {
        val doubled = (s + s).substring(1, s.length * 2 - 1)
        return s in doubled
    }
}

fun main() {
    Solution().repeatedSubstringPattern("abcdefghijklmnop")
}