package One514;

import java.util.*


class Solution {


    data class ProbableNode(val node: Int, val probability: Double)

    fun maxProbability(n: Int, edges: Array<IntArray>, succProb: DoubleArray, start_node: Int, end_node: Int): Double {
        // 1. Create adj list
        // Node --> (Node, Probability)
        // Merges each sub-list with its corresponding probability

        val adj = hashMapOf<Int, MutableList<ProbableNode>>()
        // 2. Populate the Map
        for (i in edges.indices) {
            val (u, v) = edges[i]
            val p = succProb[i]

            // computeIfAbsent is the idiomatic way to handle "if key doesn't exist, create list"
            adj.computeIfAbsent(u) { mutableListOf() }.add(ProbableNode(v, p))
            adj.computeIfAbsent(v) { mutableListOf() }.add(ProbableNode(u, p))
        }


        // -- Dijkstra --
        // 2. create pq and populate it with the first node
        // pq = Pair(Node, double = probability)
        val pq = PriorityQueue<Pair<Int, Double>>(compareByDescending { it.second })
        pq.add(start_node to 1.0)

        val maxProb = DoubleArray(n) { 0.0 }
        maxProb[start_node] = 1.0

        // -- Main Dijkstra
        while (!pq.isEmpty()) {
            val (currNode, currProb) = pq.poll()

            // If the node we popped is the end_node - return the probability
            if (currNode == end_node) {
                return currProb
            }

            // skip if we have a better probability
            if (maxProb[currNode] > currProb) {
                continue
            }

            // if not bfs style its neighbors and add them to the pq
            // each item is ProbableNode (dstNode: Node/Int , probability/Double)
            adj[currNode]?.forEach { (dst, probability) ->
                val newProb = probability * currProb
                if (newProb > maxProb[dst]) {
                    // 1. Update the record so other paths know this is the new "best"
                    maxProb[dst] = newProb

                    // 2. Add to queue to explore further
                    pq.add(Pair(dst, newProb))
                }
            }


        }


        return 0.toDouble();
    }
}
