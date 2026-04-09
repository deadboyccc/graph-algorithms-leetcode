package quest.DSA.arr2.q1

// 1,2,2,4 = > 2 is repeated, 3 is missing
// 1. Loop through the array at index of i = 2 if 3 is not found that means the means it's the element that is
// missing
// if that element at index ii=2 ( which supposed to be three) is equal to the left or right = the element in
// repeat

class Solution {
    fun findErrorNums(nums: IntArray): IntArray {

        if (nums.isEmpty()) return IntArray(0)
        if (nums.size == 1) return IntArray(0)

        val res = IntArray(2)

        for ((i, element) in nums.withIndex()) {

            if (element != i + 1) {
                res[0] =
                    if (i + 1 < nums.size && nums[i + 1] == element) nums[i + 1] else if (i > 0) nums[i - 1] else element
                res[1] = i + 1
            }

        }

        return res
    }
}

// res[0] = repeated
// res [1] = missing
fun main() {
    println(Solution().findErrorNums(intArrayOf(1, 2, 2, 4)).contentToString())
}
