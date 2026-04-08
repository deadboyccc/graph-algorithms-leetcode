package nine12;

import kotlin.random.Random

class Solution {
    // quickSort Impl
    fun sortArray(nums: IntArray): IntArray {
        quickSort(nums, 0, nums.lastIndex)
        return nums
    }

    fun quickSort(nums: IntArray, left: Int, right: Int) {
        if (left >= right) return

        val pivotIndex = partition(nums, left, right)
        quickSort(nums, left, pivotIndex - 1)
        quickSort(nums, pivotIndex + 1, right)
    }

    fun partition(nums: IntArray, startingIndex: Int, endingIndex: Int): Int {
        // get random index
        val randomIndex = Random.nextInt(startingIndex, endingIndex + 1)

        // swap random index with endingIndex ( value wise)
        nums[randomIndex] = nums[endingIndex].also { nums[endingIndex] = nums[randomIndex] }

        // i = the wall between less than pivot and greater than pivot
        var i = startingIndex

        for (j in startingIndex until endingIndex) {
            if (nums[j] < nums[endingIndex]) {
                nums[j] = nums[i].also { nums[i] = nums[j] }
                i++
            }
        }
        nums[i] = nums[endingIndex].also { nums[endingIndex] = nums[i] }
        return i
    }
}

fun main() {
    Solution().sortArray(intArrayOf(1, 2, 3, 4))
}