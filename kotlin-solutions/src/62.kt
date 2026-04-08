package six2

/**
 * Problem: Unique Paths
 * Finds the number of possible unique paths from the top-left corner to the bottom-right
 * corner of an m x n grid, moving only down or right.
 */
class Solution {

    fun uniquePaths(rows: Int, cols: Int): Int {
        // Initialize memoization table with 0.
        // arr[r][c] stores the number of unique paths from (r, c) to the destination.
        val memo = Array(rows) { IntArray(cols) { 0 } }

        return calculatePaths(0, 0, rows, cols, memo)
    }

    private fun calculatePaths(
        r: Int,
        c: Int,
        rows: Int,
        cols: Int,
        memo: Array<IntArray>
    ): Int {
        // 1. Boundary Check: If we step out of the grid, there are 0 paths.
        if (r >= rows || c >= cols) {
            return 0
        }

        // 2. Base Case: If we reach the bottom-right corner, we found 1 valid path.
        if (r == rows - 1 && c == cols - 1) {
            return 1
        }

        // 3. Memoization Lookup: Return cached result if already calculated.
        if (memo[r][c] != 0) {
            return memo[r][c]
        }

        // 4. Recursive Step: The paths from current cell = (Paths Moving Right) + (Paths Moving Down).
        // Store the result in the memo table before returning (Top-Down DP).
        memo[r][c] = calculatePaths(r, c + 1, rows, cols, memo) +
                calculatePaths(r + 1, c, rows, cols, memo)

        return memo[r][c]
    }
}

fun main() {
    val result = Solution().uniquePaths(3, 7)
    println("Unique Paths: $result")
}
