# 🚀 Kotlin Concurrency Masterclass: Bidirectional BFS

This repository serves as a master guide and production-grade implementation of **Multithreaded Bidirectional Breadth-First Search (BFS)** using modern Kotlin Coroutines.

It demonstrates **Structured Concurrency**, **Atomic State Management**, the `select` expression, and deep Kotlin language features like scope labeling and collection transformation pipelines.

---

## 📑 Table of Contents

1. [The Coroutine Exception Trap](#1-the-coroutine-exception-trap)
2. [Concept: Bidirectional BFS](#2-concept-bidirectional-bfs)
3. [Graph Generation Pipeline](#3-graph-generation-pipeline)
4. [Method A: The Push Model (launch + Deferred)](#4-method-a-the-push-model-launch--deferred)
5. [Method B: The Pull Model (async + select)](#5-method-b-the-pull-model-async--select)
6. [Deep Dive: Kotlin Language Features](#6-deep-dive-kotlin-language-features)
7. [Production-Grade JUnit Tests](#7-production-grade-junit-tests)

---

## 1. The Coroutine Exception Trap

Before writing concurrent algorithms, we must understand how Coroutines handle failures, specifically the difference between `launch` and `async`.

**The Gotcha:** Exceptions thrown inside an `async` block are encapsulated in a `Deferred` object. If `async` is a child of a `launch` block, it will silently fail or propagate the error in unexpected ways unless you call `.await()`.

**The Golden Rule:**

- Use `launch` for side effects (exceptions propagate to the `CoroutineExceptionHandler`).
- Use `async` when you need a result (exceptions are thrown when you call `await()`).

---

## 2. Concept: Bidirectional BFS

Traditional BFS searches from a **Start** node outward like a giant expanding circle until it finds the **Target**. Bidirectional BFS is an optimization where you search forward from the Start and backward from the Target simultaneously.

When the two frontiers intersect, the search is complete. Mathematically, this reduces the time complexity from $O(b^d)$ to $O(b^{d/2})$, where $b$ is the branching factor and $d$ is the distance.

### Graph Visualization

Here is the 5-layer graph we use in this project. The searches start at opposite ends and race to the middle.

```
SEARCH START (Forward) →                      ← SEARCH TARGET (Backward)

[ A B C D E ]  <-- Layer 1 (Start Zone)
     ||
[ F G H I J ]  <-- Layer 2 (Transition)
     ||
[ K L M N O ]  <-- Layer 3 (Likely Collision Zone)
     ||
[ P Q R S T ]  <-- Layer 4 (Transition)
     ||
[ U V W X Y Z ] <-- Layer 5 (Target Zone)

Strategic Shortcuts: A -> K, M -> Z
```

---

## 3. Graph Generation Pipeline

To create our graph, we use an idiomatic Kotlin builder pattern utilizing `apply`, `mapValues`, and `distinct`.

```kotlin
fun generateLayeredGraph(): Map<Char, List<Char>> = mutableMapOf<Char, MutableList<Char>>().apply {
    fun addEdge(u: Char, v: Char) {
        this.getOrPut(u) { mutableListOf() }.add(v)
        this.getOrPut(v) { mutableListOf() }.add(u)
    }

    val layers = listOf('A'..'E', 'F'..'J', 'K'..'O', 'P'..'T', 'U'..'Z').map { it.toList() }

    for (i in 0 until layers.size - 1) {
        for (u in layers[i]) {
            for (v in layers[i+1]) {
                if ((u.code + v.code) % 2 == 0) addEdge(u, v)
                if ((u.code * v.code) % 3 == 0) addEdge(u, v)
            }
        }
    }
    addEdge('A', 'K')
    addEdge('M', 'Z')
}.mapValues { it.value.distinct().toList() }
```

### Pipeline Breakdown

- **`mapValues { ... }`**: Transforms the values of a map while keeping the keys the same. We use it to turn our temporary `MutableList` into an immutable, thread-safe `List`.
- **`distinct()`**: Filters out duplicate entries in a collection. If our loops accidentally added the edge `A -> B` twice, `distinct()` ensures our BFS doesn't process it twice.

---

## 4. Method A: The Push Model (launch + Deferred)

This approach uses "fire and forget" jobs. The two threads share a `ConcurrentHashMap`. When one thread notices the other thread has already visited a node, it "pushes" the result into a `CompletableDeferred`.

```kotlin
class BidirectionalSearchLaunch(private val adj: Map<Char, List<Char>>) {
    private val visited = ConcurrentHashMap<Char, String>()
    private val meetingPoint = CompletableDeferred<Char>()

    suspend fun runSearch(startNode: Char, targetNode: Char): Char = coroutineScope {
        launch(Dispatchers.Default) { performBfs(startNode, "START", "TARGET") }
        launch(Dispatchers.Default) { performBfs(targetNode, "TARGET", "START") }

        val winner = meetingPoint.await() // Suspends until collision
        this@coroutineScope.cancel()      // structured concurrency: cancel loser
        winner
    }

    private suspend fun performBfs(start: Char, myDir: String, otherDir: String) {
        val queue: java.util.Queue<Char> = java.util.LinkedList()
        queue.add(start)
        visited[start] = myDir

        while (queue.isNotEmpty() && !meetingPoint.isCompleted) {
            val current = queue.poll() ?: continue

            adj[current]?.forEach { neighbor ->
                // ATOMIC Check-and-Act
                val alreadyVisitedBy = visited.putIfAbsent(neighbor, myDir)

                when (alreadyVisitedBy) {
                    null -> queue.add(neighbor)
                    otherDir -> meetingPoint.complete(neighbor) // COLLISION!
                }
            }
        }
    }
}
```

---

## 5. Method B: The Pull Model (async + select)

This is the purer, more functional approach. Both searches are treated as competing producers. The `select` expression acts as a referee, "pulling" the first returned value and cancelling the loser.

```kotlin
import kotlinx.coroutines.selects.select

class BidirectionalSearchSelect(private val adj: Map<Char, List<Char>>) {
    private val visited = ConcurrentHashMap<Char, String>()

    suspend fun runSearch(startNode: Char, targetNode: Char): Char = coroutineScope {
        val forwardSearch = async(Dispatchers.Default) { performBfs(startNode, "START", "TARGET") }
        val backwardSearch = async(Dispatchers.Default) { performBfs(targetNode, "TARGET", "START") }

        // The Race Official
        val winnerNode = select<Char> {
            forwardSearch.onAwait { it }
            backwardSearch.onAwait { it }
        }

        coroutineContext.cancelChildren() // Cleanup the loser
        winnerNode
    }

    private suspend fun performBfs(start: Char, myDir: String, otherDir: String): Char {
        val queue: java.util.Queue<Char> = java.util.LinkedList()
        queue.add(start)

        while (queue.isNotEmpty()) {
            yield() // Check for cancellation from the winning thread
            val current = queue.poll() ?: continue

            adj[current]?.forEach { neighbor ->
                val alreadyVisitedBy = visited.putIfAbsent(neighbor, myDir)
                if (alreadyVisitedBy == otherDir) {
                    return neighbor // Return directly to the select block
                } else if (alreadyVisitedBy == null) {
                    queue.add(neighbor)
                }
            }
        }
        throw IllegalStateException("No path found")
    }
}
```

---

## 6. Deep Dive: Kotlin Language Features

### 6.1. Thread Safety

- **`ConcurrentHashMap`**: Ensures that when two coroutines call `putIfAbsent` at the exact same millisecond, the JVM guarantees only one succeeds. It prevents data corruption (lost updates).
- **`putIfAbsent`**: An atomic operation. It checks the map and inserts a value in one unbroken step. It returns `null` if the insertion was successful, or the existing value if it failed.

### 6.2. Labels and `this@`

Kotlin allows you to explicitly state which scope or loop you are referring to using labels.

- **Qualified This (`this@`)**: Used in nested scopes. In our code, `this@coroutineScope.cancel()` tells Kotlin to cancel the specific `coroutineScope` we opened, not the inner `launch` scope.

- **Loop Labels (`loop@`)**: Used to `break` or `continue` specific levels of nested loops.

```kotlin
outer@ for (i in 1..5) {
    inner@ for (j in 1..5) {
        if (j == 3) break@outer // Kills the outer loop entirely
    }
}
```

- **Lambda Returns (`return@`)**: You cannot use `break` inside a `forEach` lambda. You must use a labeled return to act like a `continue`.

```kotlin
listOf(1, 2, 3).forEach {
    if (it == 2) return@forEach // Skips to the next iteration
    println(it)
}
```

---

## 7. Production-Grade JUnit Tests

To ensure our concurrent logic is robust, we use `kotlinx-coroutines-test` to test our coroutines deterministically without real-time delays.

### Dependencies (`build.gradle.kts`)

```kotlin
dependencies {
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}
```

### The Test Suite

```kotlin
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class BidirectionalSearchTest {

    // A predictable, linear graph: A -> B -> C -> D -> E -> Z
    private val simpleGraph = mapOf(
        'A' to listOf('B'),
        'B' to listOf('A', 'C'),
        'C' to listOf('B', 'D'),
        'D' to listOf('C', 'E'),
        'E' to listOf('D', 'Z'),
        'Z' to listOf('E')
    )

    // A disconnected graph to test failures
    private val disconnectedGraph = mapOf(
        'A' to listOf('B'),
        'B' to listOf('A'),
        'Y' to listOf('Z'),
        'Z' to listOf('Y')
    )

    @Test
    fun `Push Model (Launch) successfully finds middle intersection`() = runTest {
        val solver = BidirectionalSearchLaunch(simpleGraph)
        val meetingPoint = solver.runSearch('A', 'Z')

        // Since they move at the same speed, they should meet in the middle
        assertTrue(meetingPoint == 'C' || meetingPoint == 'D')
    }

    @Test
    fun `Pull Model (Select) successfully finds middle intersection`() = runTest {
        val solver = BidirectionalSearchSelect(simpleGraph)
        val meetingPoint = solver.runSearch('A', 'Z')

        assertTrue(meetingPoint == 'C' || meetingPoint == 'D')
    }

    @Test
    fun `Pull Model throws Exception when no path exists`() = runTest {
        val solver = BidirectionalSearchSelect(disconnectedGraph)

        assertThrows(IllegalStateException::class.java) {
            // runTest requires wrapping throwing suspending functions
            solver.runSearch('A', 'Z')
        }
    }
}
```

---
