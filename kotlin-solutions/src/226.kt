package two66


class TreeNode(var `val`: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}

class Solution {
    // Creating the tree:
    //        1
    //       / \
    //      2   3
    //     / \   \
    //    4   5   6
    public val root = TreeNode(1).apply {
        left = TreeNode(2).apply {
            left = TreeNode(4)
            right = TreeNode(5)
        }
        right = TreeNode(3).apply {
            right = TreeNode(6)
        }
    }

    fun invertTree(root: TreeNode?): TreeNode? {
        val newroot = bfs(root)
        bfs2(newroot)
        return newroot

    }

    fun bfs2(root: TreeNode?): Unit {
        if (root == null) return
        val q = ArrayDeque<TreeNode>()

        q.add(root)
        var level = 0

        while (q.isNotEmpty()) {
            val levelSize = q.size
            val items = mutableListOf<Int>()

            for (i in 0 until levelSize) {
                val curr = q.removeFirst()
                items.add(curr.`val`)

                curr.left?.let { q.addLast(it) }
                curr.right?.let { q.addLast(it) }
            }

            println("level $level : $items")
            level++
        }
    }

    fun bfs(root: TreeNode?): TreeNode? {
        if (root == null) return null
        val q = ArrayDeque<TreeNode>()

        q.add(root)

        while (q.isNotEmpty()) {
            val curr = q.removeFirst()

            val temp = curr.left
            curr.left = curr.right
            curr.right = temp

            curr.left?.let { q.addLast(it) }
            curr.right?.let { q.addLast(it) }
        }

        return root
    }


}

fun main() {
    val solution = Solution()
    solution.invertTree(solution.root)
}
