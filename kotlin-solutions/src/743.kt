package seven93;
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
class Solution {

    data class Edge(val target: Int, val travelTime: Int)
    data class State(val time: Int, val node: Int)

    fun networkDelayTime(times: Array<IntArray>, n: Int, k: Int): Int {

        // 1. Build adjacency map: source -> outgoing edges (O(1) neighbor lookup)
        val graph = times
            .map { (u, v, w) -> u to Edge(v, w) }
            .groupBy({ it.first }, { it.second })

        // 2. Min-heap keyed by cumulative arrival time: (time, node)
        val pq = PriorityQueue<State>(compareBy { it.time })
        pq.add(State(time = 0, node = k))

        // 3. Track finalized nodes and the latest arrival time among them
        val settled = HashSet<Int>()
        var maxTime = 0

        while (pq.isNotEmpty()) {
            val (time, node) = pq.poll()

            if (node in settled) continue // already settled via a shorter path

            // Finalize this node
            settled.add(node)
            maxTime = time

            // 4. Enqueue unsettled neighbors with their cumulative travel time from k
            graph[node]?.forEach { edge ->
                if (edge.target !in settled)
                    pq.add(State(time + edge.travelTime, edge.target))
            }
        }

        // 5. All nodes reached → max arrival time is the answer; otherwise unreachable
        return if (settled.size == n) maxTime else -1
    }
}
