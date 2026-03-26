package leetcode1971

/**
 * Problem: 1971. Find if Path Exists in Graph
 * * Logic:
 * 1. Build an Adjacency List using [getOrPut] for clean map initialization.
 * 2. Use BFS with [ArrayDeque] for optimal FIFO performance.
 * 3. Use early returns and functional paradigms to keep the code concise.
 */
class Solution {

    fun validPath(n: Int, edges: Array<IntArray>, source: Int, destination: Int): Boolean {
        if (source == destination) return true

        // Build adjacency list idiomatically
        val adj = mutableMapOf<Int, MutableList<Int>>()
        for ((u, v) in edges) {
            adj.getOrPut(u) { mutableListOf() } += v
            adj.getOrPut(v) { mutableListOf() } += u
        }

        return hasPathBfs(source, destination, adj)
    }

    private fun hasPathBfs(start: Int, target: Int, adj: Map<Int, List<Int>>): Boolean {
        val queue = ArrayDeque<Int>().apply { addLast(start) }
        val visited = mutableSetOf(start)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()

            // Safe access using Elvis operator to handle nodes with no neighbors
            for (neighbor in adj[current] ?: emptyList()) {
                if (neighbor == target) return true

                if (visited.add(neighbor)) { // .add() returns true if the element was not already present
                    queue.addLast(neighbor)
                }
            }
        }

        return false
    }
}
