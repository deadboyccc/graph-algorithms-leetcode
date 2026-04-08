package six3;

/*
we wanna reach top left to bottom right

 0 0 0
 0 x 0
 0 0 1

 */
class Solution {
    fun uniquePathsWithObstacles(obstacleGrid: Array<IntArray>): Int {
        val dp = IntArray(obstacleGrid[0].size) { 0 }.apply { this[lastIndex] = 1 }
        for (row in obstacleGrid.size - 1 downTo 0) {
            for (col in dp.size - 1 downTo 0) {
                when {
                    obstacleGrid[row][col] == 1 -> dp[col] = 0
                    col + 1 < dp.size -> dp[col] = dp[col] + dp[col + 1]
//                    col+1>=dp.size -> dp[col] = dp[col]
                }
            }
        }
        return dp[0]
    }
}

fun main() {
    val obstacleGrid: Array<IntArray> = arrayOf(
        intArrayOf(0, 0, 0), // Row 1
        intArrayOf(0, 1, 0), // Row 2 (This is the one with the obstacle)
        intArrayOf(0, 0, 0)  // Row 3
    )

    // Optional: Print the grid to verify it
    println(obstacleGrid.contentDeepToString())

    Solution().uniquePathsWithObstacles(obstacleGrid).also { println(it) }
}