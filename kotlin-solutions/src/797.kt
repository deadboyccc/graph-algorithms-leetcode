package seven97;

// graph (int -> list of int
class Solution {
    fun allPathsSourceTarget(graph: Array<IntArray>): List<List<Int>> {
        val adj = graph.indices
            .associateWith { i -> graph[i].toMutableList() }.toMutableMap()

        val res = mutableListOf<List<Int>>()
        val currPath = mutableListOf<Int>()

        fun dfs(node: Int) {
            // add
            currPath.add(node)

            if (node == graph.lastIndex) {
                res.add(currPath.toList())
            }

            // explore
            adj[node]?.forEach {
                dfs(it)
            }


            // backtrack
            currPath.removeLast()
        }


        dfs(0)
        return res
    }
}

fun main() {

    // Input: graph = [[1,2],[3],[3],[]]
    Solution().allPathsSourceTarget(
        arrayOf(
            intArrayOf(1, 2),
            intArrayOf(3),
            intArrayOf(3),
            intArrayOf(),
        )
    ).also { println(it) }

}