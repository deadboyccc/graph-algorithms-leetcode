package nine4;

/**
 * Example:
 * var ti = TreeNode(5)
 * var v = ti.`val`
 * Definition for a binary tree node.
 * class TreeNode(var `val`: Int) {
 *     var left: TreeNode? = null
 *     var right: TreeNode? = null
 * }
 */
class Solution {
    fun inorderTraversal(root: TreeNode?): List<Int> {
        if (root == null) return emptyList()
        return inorderTraversal(root.left) + root.`val` + inorderTraversal(root.right)


    }
}

class TreeNode(var `val`: Int, var left: TreeNode? = null, var right: TreeNode? = null)
