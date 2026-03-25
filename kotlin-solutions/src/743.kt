import java.util.*

/**
 * Problem: Network Delay Time — Dijkstra's Algorithm
 * Goal: Find the minimum time for a signal from node 'k' to reach ALL 'n' nodes.
 *
 * Constraints:
 * - Nodes are 1-indexed (1 to n)
 * - Edges are directed: times[i] = [source, target, travelTime]
 * - Return the arrival time of the last node reached, or -1 if any node is unreachable
 */
class SolutionB {

    data class WeightedEdge(val target: Int, val travelTime: Int)

    fun networkDelayTime(times: Array<IntArray>, n: Int, k: Int): Int {

        // 1. Build adjacency map: source -> outgoing edges (O(1) neighbor lookup)
        val graph = times
            .map { (u, v, w) -> u to WeightedEdge(v, w) }
            .groupBy({ it.first }, { it.second })

        // 2. Min-heap keyed by cumulative arrival time: (arrivalTime, node)
        val pq = PriorityQueue<Pair<Int, Int>>(compareBy { it.first })
        pq.add(0 to k)

        // 3. Track finalized nodes and the latest arrival time among them
        val visited = HashSet<Int>()
        var maxTime = 0

        while (pq.isNotEmpty()) {
            val (arrivalTime, currNode) = pq.poll()

            if (currNode in visited) continue // already settled via a shorter path

            // Finalize this node
            visited.add(currNode)
            maxTime = arrivalTime

            // 4. Enqueue unvisited neighbors with their cumulative travel time from k
            graph[currNode]?.forEach { edge ->
                if (edge.target !in visited)
                    pq.add(arrivalTime + edge.travelTime to edge.target)
            }
        }

        // 5. All nodes reached → max arrival time is the answer; otherwise unreachable
        return if (visited.size == n) maxTime else -1
    }
}
