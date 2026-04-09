package quest.DSA.arr2.one365

// sorted[i] > > while sorted[i+1]  once we land on the final repeating sorted[i] we return it's index - size
// how many element it's bigger than
// or use normal sort
// then store that in a hashMap then loop over the original array and replacing each element with o(1)
// look up with it's value

class Solution {
    fun smallerNumbersThanCurrent(nums: IntArray): IntArray {
        if (nums.isEmpty()) return nums
        if (nums.size == 1) return intArrayOf(0)

        val sorted = nums.sorted()


        val map = buildMap {
            for (i in sorted.indices) {
                // This only runs the lambda and "puts" if the key doesn't exist
                getOrPut(sorted[i]) { i }
            }
        }

        return IntArray(nums.size) { i -> map[nums[i]] ?: 0 }
//        return nums.map { map[it] ?: 0 }.toIntArray()
    }
}

fun main() {
    Solution().smallerNumbersThanCurrent(intArrayOf(8, 1, 2, 2, 3))
        .also { println(it.contentToString()) }

//    Output: [4,0,1,1,3]

}
