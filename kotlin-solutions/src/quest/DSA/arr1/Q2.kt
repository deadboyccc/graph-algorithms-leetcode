package quest.DSA.q2

class Solution {
    // nums length is even ( 2n )
    fun shuffle(nums: IntArray, n: Int): IntArray {

        val res = IntArray(nums.size)
        var ptr = 0
        for ((x, y) in (0 until n).zip(n until nums.size)) {
            res[ptr++] = nums[x]
            res[ptr++] = nums[y]
        }

        return res
    }
}

class OptimizedPrim() {
    class Solution {
        fun shuffle(nums: IntArray, n: Int): IntArray {
            // pointers to x and y
            var x = 0
            var y = n
            return IntArray(nums.size) { i ->
                when (i % 2) {
                    0 -> nums[x++]
                    else -> nums[y++]
                }

            }
        }
    }
}

fun main() {
    Solution().shuffle(intArrayOf(1, 2, 3, 4), 2).also { println(it.contentToString()) }
}


