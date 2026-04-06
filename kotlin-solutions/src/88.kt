package eight8

class Solution {
    fun merge(nums1: IntArray, m: Int, nums2: IntArray, n: Int): Unit {
        val leftIterator = nums1.toList().listIterator()
        val rightIterator = nums2.toList().listIterator()

        val res = mutableListOf<Int>()


        while (leftIterator.hasNext() && rightIterator.hasNext()) {
            val (left, right) = leftIterator.next() to rightIterator.next()
            if (left < right) {
                res.add(left)
                rightIterator.previous()
            } else {
                res.add(right)
                leftIterator.previous()
            }


        }
        while (leftIterator.hasNext()) {
            res.add(leftIterator.next())
        }
        while (rightIterator.hasNext()) {
            res.add(rightIterator.next())
        }
        nums1.forEachIndexed { index, _ ->
            nums1[index] = res[index]
        }

    }
}