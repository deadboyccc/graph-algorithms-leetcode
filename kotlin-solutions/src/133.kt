package one33;

class Node(var `val`: Int) {
    var neighbors: ArrayList<Node?> = ArrayList<Node?>()
}

class Solution {
    fun cloneGraph(node: Node?): Node? {
        if (node == null) return null

        val clones = mutableMapOf<Node, Node>()

        fun dfs(original: Node): Node {
            clones[original]?.let { return it }

            val clone = Node(original.`val`).also { clones[original] = it }
            clone.neighbors = original.neighbors
                .filterNotNull()
                .map { dfs(it) }
                .toCollection(ArrayList())

            return clone
        }

        return dfs(node)
    }
}
