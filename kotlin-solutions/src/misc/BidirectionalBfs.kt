package misc

import kotlinx.coroutines.*
import java.util.*
import java.util.Queue
import java.util.concurrent.ConcurrentHashMap

/**
 * --- GRAPH VISUALIZATION ---
 * * The BFS frontiers will expand from both ends, likely colliding in the Middle Zone.
 * Each bracket [ ] represents a layer of nodes.
 *
 * SEARCH START (Forward) →                      ← SEARCH TARGET (Backward)
 * * [ A B C D E ]  <-- Layer 1 (Start Zone)
 * ||
 * [ F G H I J ]  <-- Layer 2 (Transition)
 * ||
 * [ K L M N O ]  <-- Layer 3 (Likely Collision Zone)
 * ||
 * [ P Q R S T ]  <-- Layer 4 (Transition)
 * ||
 * [ U V W X Y Z ] <-- Layer 5 (Target Zone)
 *
 * Strategic Bridges (Shortcuts):
 * - A -> K (Jump to middle)
 * - M -> Z (Jump to end)
 */

fun main() = runBlocking {
    val solver = BidirectionalSearch(generateLayeredGraph())

    println("Starting Bidirectional BFS on thread: ${Thread.currentThread().name}")

    val meetingNode = solver.runSearch(startNode = 'A', targetNode = 'Z')

    println("\n[FINAL RESULT] The two searches met at Node: '$meetingNode'")
}

class BidirectionalSearch(private val adj: Map<Char, List<Char>>) {

    // Shared map to track visits: Node -> "START" or "TARGET"
    private val visited = ConcurrentHashMap<Char, String>()

    // A thread-safe way to communicate the first meeting point found
    private val meetingPoint = CompletableDeferred<Char>()

    suspend fun runSearch(startNode: Char, targetNode: Char): Char = coroutineScope {

        // Launch Forward Search
        launch(Dispatchers.Default) {
            performBfs(startNode, "START", "TARGET")
        }

        // Launch Backward Search
        launch(Dispatchers.Default) {
            performBfs(targetNode, "TARGET", "START")
        }

        // Suspend until one of the coroutines calls meetingPoint.complete()
        val winner = meetingPoint.await()

        // Structured Concurrency: cancelling the scope stops all active search jobs
        this@coroutineScope.cancel()

        winner
    }

    private suspend fun performBfs(start: Char, myDir: String, otherDir: String) {
        val queue: Queue<Char> = LinkedList()
        queue.add(start)
        visited[start] = myDir

        while (queue.isNotEmpty() && !meetingPoint.isCompleted) {
            val current = queue.poll()

            // Artificial delay to simulate work and allow the race to happen
            delay(50)

            adj[current]?.forEach { neighbor ->
                // putIfAbsent is ATOMIC: it returns the value already there, or null if it was empty
                val alreadyVisitedBy = visited.putIfAbsent(neighbor, myDir)

                when (alreadyVisitedBy) {
                    null -> {
                        // First time anyone has seen this node
                        queue.add(neighbor)
                        println("[$myDir] Visited $neighbor on ${Thread.currentThread().name}")
                    }

                    otherDir -> {
                        // COLLISION DETECTED: The other search was here first!
                        if (meetingPoint.complete(neighbor)) {
                            println("[$myDir] FOUND COLLISION at '$neighbor'!")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Idiomatic generation of the Adjacency Map
 */
fun generateLayeredGraph(): Map<Char, List<Char>> = mutableMapOf<Char, MutableList<Char>>().apply {
    fun addEdge(u: Char, v: Char) {
        this.getOrPut(u) { mutableListOf() }.add(v)
        this.getOrPut(v) { mutableListOf() }.add(u)
    }

    val layers = listOf('A'..'E', 'F'..'J', 'K'..'O', 'P'..'T', 'U'..'Z').map { it.toList() }

    for (i in 0 until layers.size - 1) {
        val currentLayer = layers[i]
        val nextLayer = layers[i + 1]
        for (u in currentLayer) {
            for (v in nextLayer) {
                // Connect layers with some deterministic "randomness"
                if ((u.code + v.code) % 2 == 0) addEdge(u, v)
                if ((u.code * v.code) % 3 == 0) addEdge(u, v)
            }
        }
    }

    // Add shortcuts to make the race interesting
    addEdge('A', 'K')
    addEdge('M', 'Z')

}.mapValues { it.value.distinct().toList() }