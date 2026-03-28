package one584;
import java.util.*
import kotlin.math.abs

/**
 * Problem: Min Cost to Connect All Points (Minimum Spanning Tree)
 *
 * Goal: Connect all 'n' points such that the total distance is minimized.
 *
 * Key Points:
 * 1. Points are (x, y) coordinates on a 2D grid.
 * 2. Distance is Manhattan Distance: |x1 - x2| + |y1 - y2|.
 * 3. All points must be connected (directly or indirectly).
 * 4. Return the minimum sum of edge weights.
 */
class Solution {
    data class Point(val x: Int, val y: Int)

    // Extension function makes the call site cleaner: a.distTo(b)
    private fun Point.distTo(other: Point) = abs(x - other.x) + abs(y - other.y)

    fun minCostConnectPoints(points: Array<IntArray>): Int {
        val nodes = points.map { (x, y) -> Point(x, y) }
        val n = nodes.size

        var totalCost = 0
        val settled = mutableSetOf<Point>()

        // Min-heap of (cost, point) — always expand the cheapest reachable node next
        val pq = PriorityQueue<Pair<Int, Point>>(compareBy { it.first })
        pq.add(0 to nodes[0])

        while (settled.size < n && pq.isNotEmpty()) {
            val (cost, node) = pq.poll()

            if (!settled.add(node)) continue // add() returns false if already settled

            totalCost += cost

            // Use sequences to avoid creating intermediate lists with filter
            nodes.asSequence()
                .filter { it !in settled }
                .forEach { neighbor -> pq.add(node.distTo(neighbor) to neighbor) }
        }

        return totalCost
    }
}
