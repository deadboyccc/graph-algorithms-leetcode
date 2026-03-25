import java.util.*

class SolutionB {

    data class WeightedEdge(val source: Int, val target: Int, val travelTime: Int)

    fun networkDelayTime(times: Array<IntArray>, n: Int, k: Int): Int {

        val weightedEdges = times.map { (u, v, w) -> WeightedEdge(u, v, w) }

        // Min-heap storing Pair(node, cumulativeArrivalTime)
        val pq = PriorityQueue<Pair<Int, Int>>(compareBy { it.second })
        pq.add(k to 0)

        val visited = hashSetOf<Int>()
        var maxTime = 0

        while (pq.isNotEmpty()) {
            val (currNode, arrivalTime) = pq.poll()

            if (currNode in visited) continue

            // Mark node as reached and update the latest arrival time
            visited.add(currNode)
            maxTime = arrivalTime

            // Explore all outgoing edges from the current node
            weightedEdges.filter { it.source == currNode }
                .forEach { edge ->
                    // Add neighbor to heap with its total travel time from source k
                    pq.add(edge.target to (arrivalTime + edge.travelTime))
                }
        }

        // Return max arrival time if all nodes reached; otherwise -1
        return if (visited.size == n) maxTime else -1
    }
}
