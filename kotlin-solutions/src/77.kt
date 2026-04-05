// assume n = 4 ( we have 4 choices ) go to each then recurse on n-1 other choices until base case
// then since it's dfs-like, we pop and recurse the 2nd i+1 -> i+1+1 ( as not to include the first)
package seventy7;

class Solution {
    fun combine(n: Int, k: Int): List<List<Int>> {

        val res = mutableListOf<MutableList<Int>>()
        val curr = mutableListOf<Int>()

        fun backtrack(i: Int, n: Int, k: Int, curr: MutableList<Int>, res: MutableList<MutableList<Int>>) {
            if (curr.size == k) {
                res.add(curr.toMutableList())
                return
            }
            if (i > n) return

            for (j in i..n) {


                curr.add(j)
                backtrack(j + 1, n, k, curr, res)
                curr.removeLast()

            }


        }
        backtrack(1, n, k, curr, res)

        return res
    }

}