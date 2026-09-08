package one39

/**
 * LeetCode 139. Word Break
 * https://leetcode.com/problems/word-break/
 *
 * Given a string `s` and a dictionary of strings `wordDict`, determine if `s`
 * can be segmented into a space-separated sequence of one or more dictionary words.
 */

/**
 * Brute Force — pure recursion, no memoization.
 *
 * Idea: try every possible first word starting at `start`. If it's in the
 * dictionary, recurse on the remainder. Re-solves overlapping subproblems
 * many times over (exponential blowup).
 *
 * Time:  O(2^n) worst case
 * Space: O(n) recursion depth
 */
class WordBreakBruteForce {
    fun wordBreak(s: String, wordDict: List<String>): Boolean {
        val wordSet = wordDict.toHashSet()
        return solve(s, 0, wordSet)
    }

    private fun solve(s: String, start: Int, wordSet: Set<String>): Boolean {
        if (start == s.length) return true

        for (end in start + 1..s.length) {
            val prefix = s.substring(start, end)
            if (wordSet.contains(prefix) && solve(s, end, wordSet)) {
                return true
            }
        }

        return false
    }
}

/**
 * DP — Top-Down (Memoization).
 *
 * Idea: same recursion as brute force, but cache the answer for each `start`
 * index so overlapping subproblems are solved only once.
 *
 * Time:  O(n^2) states * O(n) substring work ~= O(n^3)
 *        (can drop to O(n^2) by capping inner loop at max word length)
 * Space: O(n) recursion depth + O(n) memo
 */
class WordBreakMemoization {
    fun wordBreak(s: String, wordDict: List<String>): Boolean {
        val wordSet = wordDict.toHashSet()
        // memo[i] = whether s.substring(i) is breakable; absent = not yet computed
        val memo = HashMap<Int, Boolean>()
        return dfs(s, 0, wordSet, memo)
    }

    private fun dfs(s: String, start: Int, wordSet: Set<String>, memo: HashMap<Int, Boolean>): Boolean {
        if (start == s.length) return true
        memo[start]?.let { return it }

        for (end in start + 1..s.length) {
            val prefix = s.substring(start, end)
            if (wordSet.contains(prefix) && dfs(s, end, wordSet, memo)) {
                memo[start] = true
                return true
            }
        }

        memo[start] = false
        return false
    }
}

/**
 * DP — Bottom-Up (Tabulation).
 *
 * Idea: dp[i] = true if s.substring(0, i) can be fully segmented. Build up
 * from dp[0] = true (empty prefix) by checking every split point j < i.
 *
 * Time:  O(n^2) split checks * O(n) substring creation ~= O(n^3)
 *        (same optimization applies as above)
 * Space: O(n) for the dp array, no recursion stack
 */
class WordBreakTabulation {
    fun wordBreak(s: String, wordDict: List<String>): Boolean {
        val wordSet = wordDict.toHashSet()
        val n = s.length
        val dp = BooleanArray(n + 1)
        dp[0] = true // empty prefix is trivially breakable

        for (i in 1..n) {
            for (j in 0 until i) {
                if (dp[j] && wordSet.contains(s.substring(j, i))) {
                    dp[i] = true
                    break
                }
            }
        }

        return dp[n]
    }
}

fun main() {
    val s = "leetcode"
    val wordDict = listOf("leet", "code")

    println("Brute Force : ${WordBreakBruteForce().wordBreak(s, wordDict)}")
    println("Memoization : ${WordBreakMemoization().wordBreak(s, wordDict)}")
    println("Tabulation  : ${WordBreakTabulation().wordBreak(s, wordDict)}")
}
