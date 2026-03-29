package misc2;

import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select
import java.util.*
import java.util.Queue
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.milliseconds

/**
 * --- GRAPH VISUALIZATION ---
 * [ A B C D E ]  <-- Layer 1 (Start)
 * ||
 * [ F G H I J ]  <-- Layer 2
 * ||
 * [ K L M N O ]  <-- Layer 3 (Collision Zone)
 * ||
 * [ P Q R S T ]  <-- Layer 4
 * ||
 * [ U V W X Y Z ] <-- Layer 5 (Target)
 */

fun main() = runBlocking {
    val adj = generateLayeredGraph()
    val solver = AsyncSelectSearch(adj)

    println("Starting Async + Select BFS race...")

    // The select block will return the first result found
    val meetingNode = solver.runSearch(startNode = 'A', targetNode = 'Z')

    println("\n[FINAL RESULT] The 'select' expression picked winner: '$meetingNode'")
}

class AsyncSelectSearch(private val adj: Map<Char, List<Char>>) {

    // Shared thread-safe map to track who visited what first
    private val visited = ConcurrentHashMap<Char, String>()

    /**
     * Orchestrates the race using async and select.
     */
    suspend fun runSearch(startNode: Char, targetNode: Char): Char = coroutineScope {

        // 1. Define two competing async tasks
        val forwardSearch = async(Dispatchers.Default) {
            performBfs(startNode, "START", "TARGET")
        }

        val backwardSearch = async(Dispatchers.Default) {
            performBfs(targetNode, "TARGET", "START")
        }

        // 2. The Race Official (select)
        // This suspends until either forwardSearch or backwardSearch returns a Char
        val winnerNode = select<Char> {
            forwardSearch.onAwait { it }
            backwardSearch.onAwait { it }
        }

        // 3. Cleanup
        // Once we have a winner, we cancel the "losing" async task immediately
        coroutineContext.cancelChildren()

        winnerNode
    }

    /**
     * BFS function that returns the Char where it detects a collision.
     */
    private suspend fun performBfs(start: Char, myDir: String, otherDir: String): Char {
        val queue: Queue<Char> = LinkedList()
        queue.add(start)
        visited[start] = myDir

        while (queue.isNotEmpty()) {
            // yield() checks for cancellation if the OTHER search already won
            yield()

            val current = queue.poll() ?: continue
            delay(50.milliseconds) // Slow down for visualization

            adj[current]?.forEach { neighbor ->
                val alreadyVisitedBy = visited.putIfAbsent(neighbor, myDir)

                when (alreadyVisitedBy) {
                    null -> {
                        queue.add(neighbor)
                        println("[$myDir] Claimed $neighbor")
                    }

                    otherDir -> {
                        // We found the other side!
                        // Returning this value triggers onAwait in the select block
                        println("[$myDir] Collision detected at $neighbor! Returning to select...")
                        return neighbor
                    }
                }
            }
        }
        throw IllegalStateException("Path not found")
    }
}

fun generateLayeredGraph(): Map<Char, List<Char>> = mutableMapOf<Char, MutableList<Char>>().apply {
    fun addEdge(u: Char, v: Char) {
        this.getOrPut(u) { mutableListOf() }.add(v)
        this.getOrPut(v) { mutableListOf() }.add(u)
    }

    val layers = listOf('A'..'E', 'F'..'J', 'K'..'O', 'P'..'T', 'U'..'Z').map { it.toList() }

    for (i in 0 until layers.size - 1) {
        for (u in layers[i]) {
            for (v in layers[i + 1]) {
                if ((u.code + v.code) % 2 == 0) addEdge(u, v)
            }
        }
    }
    addEdge('A', 'K')
    addEdge('M', 'Z')
}.mapValues { it.value.distinct().toList() }

/*
 * =========================================================================================
 * DEEP DIVE: THE ASYNC + SELECT PATTERN
 * =========================================================================================
 * * 1. HOW 'SELECT' WORKS
 * --------------------
 * Think of 'select' as an event listener for coroutines. Instead of awaiting both tasks
 * (which would take as long as the slowest one), 'select' fires as soon as the FIRST
 * 'onAwait' clause is satisfied.
 *
 * 2. THE RETURN MECHANISM
 * -----------------------
 * In the previous solution, we used a side-channel (Deferred). Here, the 'performBfs'
 * function itself is a producer. It only returns when it hits a 'return neighbor'
 * statement. This return triggers the 'onAwait' logic in the main caller.
 *
 * -----------------------------------------------------------------------------------------
 * LINE-BY-LINE BREAKDOWN
 * -----------------------------------------------------------------------------------------
 *
 * [runSearch() WITH SELECT]
 * - async(Dispatchers.Default): Creates a 'Deferred<Char>'. Unlike 'launch', this
 * promises a result in the future.
 * - select<Char> { ... }: This is a DSL. It checks the 'forwardSearch' and 'backwardSearch'
 * status. As soon as one finishes, it executes the code block inside 'onAwait' and
 * returns that result as the value of the 'select' expression.
 * - coroutineContext.cancelChildren(): Since 'forwardSearch' and 'backwardSearch' are
 * children of the current coroutine scope, this stops whichever one is still running.
 *
 * [performBfs() AS A PRODUCER]
 * - yield(): Crucial for cooperative cancellation. If Search A wins and Search B is
 * suspended at a 'delay', 'yield' or 'delay' will throw a CancellationException,
 * cleaning up Search B immediately.
 * - return neighbor: This is the exit point. In a normal BFS, you'd return at the end of
 * the function. Here, we return from the middle of the neighbor loop because that
 * represents the "success" state.
 *
 * [ADVANTAGES OF THIS APPROACH]
 * - It is highly idiomatic: It treats "Finding the meeting point" as a competitive
 * race between two data-producing tasks.
 * - Result Handling: You don't need external variables to store the winner; the
 * function return values handle the data flow naturally.
 * =========================================================================================
 */
