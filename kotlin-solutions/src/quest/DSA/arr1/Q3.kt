package quest.DSA.q3

class Solution {
    fun findMaxConsecutiveOnes(nums: IntArray): Int {
        var currCount = 0
        var maxCount = 0
        nums.forEach {
            when (it) {
                1 -> maxCount = maxOf(++currCount, maxCount)
                else -> currCount = 0
            }
        }
        return maxCount
    }

}

fun main() {
    Solution().findMaxConsecutiveOnes(intArrayOf(1, 3, 4, 5, 1, 1, 1, 3, 2)).also(::println)
}
