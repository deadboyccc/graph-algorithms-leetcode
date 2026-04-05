package two57;

class Solution {
    fun binaryTreePaths(root: TreeNode?): List<String> {
        // if root is null return an empty list of paths
        if (root == null) return listOf()


        val currPath = mutableListOf<Int>()
        val res = mutableListOf<String>()

        fun backtrack(currNode: TreeNode, currPath: MutableList<Int>, res: MutableList<String>) {
            // 1. Add current node
            currPath.add(currNode.`val`)

            // 2. Leaf check
            if (currNode.left == null && currNode.right == null) {
                res.add(currPath.joinToString("->"))
            } else {
                // 3. Recurse only if child exists
                currNode.left?.let { backtrack(it, currPath, res) }
                currNode.right?.let { backtrack(it, currPath, res) }
            }

            // 4. BACKTRACK: Remove the node we just added before going back up
            currPath.removeLast()
        }
        backtrack(root, currPath, res)
        return res
    }

}

class TreeNode(var `val`: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}
