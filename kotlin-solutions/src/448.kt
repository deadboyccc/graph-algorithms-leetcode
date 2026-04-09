package four48

class Solution {
    fun findDisappearedNumbers(nums: IntArray): List<Int> {
        // Mark every value that exists in the array
        val seen = nums.toHashSet()

        // Collect every number in [1..n] not marked
        return buildList {
            for (i in 1..nums.size) {
                if (i !in seen) add(i)
            }
        }
    }
}
