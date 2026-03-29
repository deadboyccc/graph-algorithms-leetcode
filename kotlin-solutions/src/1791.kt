package one791;

class Solution {
    fun findCenter(edges: Array<IntArray>): Int {
        val (u1, v1) = edges[0]
        val (u2, v2) = edges[1]

        // If u1 is the center, it must appear in the second edge
        return if (u1 == u2 || u1 == v2) u1 else v1
    }
}
