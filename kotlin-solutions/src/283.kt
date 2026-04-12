package two83

// too much brute force
class Solution {
    fun moveZeroes(nums: IntArray): Unit {
        if (nums[0] == 0 && nums.size == 1) return
        val arr = mutableListOf<Int>()
        var zeroCount = 0
        nums.forEach {
            if (it != 0) {
                arr.add(it)
                return@forEach
            }
            zeroCount++
        }
        (0..zeroCount).forEach { arr.add(0) }


        for (i in 0 until nums.size) {
            nums[i] = arr[i]
        }
    }
}

class improvedSolution

fun main() {
    Solution().moveZeroes(intArrayOf(8, 10, 0, 20, 0, 50, 0))
}