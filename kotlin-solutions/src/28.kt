package two8;
/*
 * LeetCode 28: Find Index of First Occurrence in String
 * Find first index of `needle` in `haystack`, or -1 if not found.
 *
 * Example: haystack = "sadbutsad", needle = "sad" → 0
 *
 * Approach: Slide a window of size needle.length across haystack.
 * At each position, check if substring matches needle. Return on first match.
 * Time: O(m*n), Space: O(1)
 */

class Solution {
    fun strStr(haystack: String, needle: String): Int {
        var index = 0
        haystack.windowedSequence(needle.length).forEach {
            if (it == needle) return index
            index++
        }
        return -1
    }
}

class SolutionFP {
    fun strStr(haystack: String, needle: String): Int {
        return haystack.windowed(needle.length).indexOfFirst { it == needle }
    }

}

fun main() {
    // Example: haystack = "sadbutsad", needle = "sad" → 0
    val haystack = "sadbutsad"
    val needle = "sad"
    Solution().strStr(haystack, needle)
        .also { println(haystack.slice(it until it + needle.length)) }

}
