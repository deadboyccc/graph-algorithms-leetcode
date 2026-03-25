import java.util.*
import kotlin.math.abs

class Solution {
    data class Point(val x: Int, val y: Int)

    fun getDistance(p1: Point, p2: Point): Int = abs(p1.x - p2.x) + abs(p1.y - p2.y)

    fun minCostConnectPoints(points: Array<IntArray>): Int {
        // Pre-convert to Point objects to avoid re-allocation in loops
        val allPoints = points.map { Point(it[0], it[1]) }
        val n = allPoints.size

        var totalCost = 0
        val visited = mutableSetOf<Point>()

        // Min-heap tracks {distance, point}; start with first point at 0 cost
        val minHeap = PriorityQueue<Pair<Int, Point>>(compareBy { it.first })
        minHeap.add(0 to allPoints[0])

        while (visited.size < n && minHeap.isNotEmpty()) {
            val (dist, curr) = minHeap.poll()

            // Skip if point is already part of the MST
            if (curr in visited) continue

            // Add the cheapest available edge to total and mark as visited
            totalCost += dist
            visited.add(curr)

            // Add all reachable unvisited neighbors to the heap
            allPoints.filter { it !in visited }
                .forEach { nextPoint ->
                    minHeap.add(getDistance(curr, nextPoint) to nextPoint)
                }
        }

        return totalCost
    }
}
