package seven8;

class Solution {
    fun subsets(nums: IntArray): List<List<Int>> {
        if (nums.isEmpty()) return listOf(emptyList())
        val subsets = mutableListOf<List<Int>>()
        val currSet = mutableListOf<Int>()

        backtrack(nums, 0, currSet, subsets)

        return subsets
    }
}

private fun backtrack(
    nums: IntArray,
    i: Int,
    currSet: MutableList<Int>,
    subsets: MutableList<List<Int>>
) {
    if (i >= nums.size) {
        subsets.add(currSet.toList())
        return
    }
    // Option 1: Exclude first
    backtrack(nums, i + 1, currSet, subsets) // No modification needed

    // Option 2: Include
    currSet.add(nums[i])
    backtrack(nums, i + 1, currSet, subsets)
    currSet.removeLast()


}
