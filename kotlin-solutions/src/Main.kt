import java.util.*
import kotlin.math.abs

class Solution {
    data class Point(val x: Int, val y: Int)

    fun getDistance(p1: Point, p2: Point): Int = abs(p1.x - p2.x) + abs(p1.y - p2.y)

    fun minCostConnectPoints(points: Array<IntArray>): Int {
        // 1. Convert to List<Point> ONCE to avoid millions of allocations
        val allPoints = points.map { Point(it[0], it[1]) }
        val n = allPoints.size

        var totalCost = 0
        val visited = mutableSetOf<Point>()

        // 2. Min-Heap: Pair(Distance, Point)
        val minHeap = PriorityQueue<Pair<Int, Point>>(compareBy { it.first })
        minHeap.add(0 to allPoints[0])

        while (visited.size < n && minHeap.isNotEmpty()) {
            val (dist, curr) = minHeap.poll()

            // 3. Skip if we've already locked this point into our MST
            if (curr in visited) continue

            totalCost += dist
            visited.add(curr)

            // 4. Only look at neighbors we haven't visited yet
            for (nextPoint in allPoints) {
                if (nextPoint !in visited) {
                    minHeap.add(getDistance(curr, nextPoint) to nextPoint)
                }
            }
        }

        return totalCost
    }
}
