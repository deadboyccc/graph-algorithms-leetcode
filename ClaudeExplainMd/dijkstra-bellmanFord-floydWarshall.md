# Shortest Path Algorithms — Kotlin Reference

Dijkstra, Bellman-Ford, and Floyd-Warshall: complete annotated implementations,
internal mechanics, LeetCode problems, and a decision guide.

---

## Shared Vocabulary

| Term | Meaning |
|---|---|
| `n` | number of vertices |
| `m` | number of edges |
| `src` | source vertex |
| `dist[v]` | best known distance from `src` to `v` |
| `INF` | sentinel for "no path found yet"; use `Long.MAX_VALUE / 2` to avoid overflow on `INF + w` |
| relaxation | the core step: `if (dist[u] + w < dist[v]) dist[v] = dist[u] + w` |
| settled | a vertex whose shortest distance is finalized and will never shrink again |

**Relaxation** is the atomic operation every shortest-path algorithm is built on.
The algorithms differ only in *which* edges they relax and *in what order*.

---

## Part 1 — Dijkstra's Algorithm

### Intuition

Dijkstra works on graphs with **non-negative edge weights**.
It is a greedy algorithm: at each step it picks the unsettled vertex with the
smallest current `dist`, declares it settled, and relaxes all its outgoing edges.

Why does greedy work here? Because all weights are >= 0, the first time a vertex
is popped from the min-heap its distance cannot be improved further — any other
path to it would pass through vertices with equal or greater distance, and then
add more non-negative weight on top.

Think of it as a wavefront expanding outward from `src`. The wavefront always
advances along the cheapest available edge, so the order vertices get settled is
exactly their shortest-path order.

```
Graph:          Step-by-step settled order (src = A):
A -1-> B        1. settle A  (dist=0)
A -4-> C        2. settle B  (dist=1, reached via A-1->B)
B -2-> C        3. settle C  (dist=3, reached via A-1->B-2->C, NOT A-4->C)
B -5-> D
C -1-> D        dist = { A:0, B:1, C:3, D:4 }
```

### Implementation

```kotlin
import java.util.PriorityQueue

// Canonical edge type used throughout this file.
// 'to' is the destination vertex, 'cost' is the edge weight.
data class Edge(val to: Int, val cost: Long)

// State pushed into the priority queue: the tentative distance to reach 'node'.
// Implementing Comparable lets PriorityQueue order by distance automatically.
data class State(val node: Int, val dist: Long) : Comparable<State> {
    override fun compareTo(other: State) = dist.compareTo(other.dist)
}

const val INF = Long.MAX_VALUE / 2   // safe sentinel: INF + any edge weight stays positive

/**
 * Dijkstra's algorithm — single-source shortest paths on non-negative weighted graphs.
 *
 * @param src  source vertex (0-indexed)
 * @param adj  adjacency list: adj[u] = list of Edge(to, cost)
 * @return     dist array where dist[v] = shortest distance from src to v, or INF if unreachable
 *
 * Time:  O((n + m) log n)  — each vertex enters the heap at most once per incoming edge,
 *                             and each heap operation is O(log n)
 * Space: O(n + m)
 */
fun dijkstra(src: Int, adj: Array<List<Edge>>): LongArray {
    val n = adj.size
    val dist = LongArray(n) { INF }
    dist[src] = 0L

    // Min-heap ordered by tentative distance.
    // We use a lazy-deletion heap: stale entries (dist > dist[node]) are simply skipped.
    val pq = PriorityQueue<State>()
    pq.add(State(src, 0L))

    while (pq.isNotEmpty()) {
        // 1. Pop the vertex with the current smallest tentative distance.
        val (u, d) = pq.poll()

        // 2. Lazy deletion: if we've already settled u with a shorter path, skip.
        //    This replaces the "decrease-key" operation that a Fibonacci heap would do.
        if (d > dist[u]) continue

        // 3. Relax all outgoing edges from u.
        for ((v, w) in adj[u]) {
            val newDist = dist[u] + w
            if (newDist < dist[v]) {
                dist[v] = newDist
                pq.add(State(v, newDist))   // push improved state; old state becomes stale
            }
        }
    }

    return dist
}
```

### Dijkstra with Path Reconstruction

```kotlin
/**
 * Returns the dist array AND the predecessor array.
 * prev[v] = the vertex that comes before v on the shortest path from src.
 * prev[src] = -1 (no predecessor).
 * prev[v] = -1 also means v is unreachable.
 */
fun dijkstraWithPath(src: Int, adj: Array<List<Edge>>): Pair<LongArray, IntArray> {
    val n = adj.size
    val dist = LongArray(n) { INF }
    val prev = IntArray(n) { -1 }
    dist[src] = 0L

    val pq = PriorityQueue<State>()
    pq.add(State(src, 0L))

    while (pq.isNotEmpty()) {
        val (u, d) = pq.poll()
        if (d > dist[u]) continue
        for ((v, w) in adj[u]) {
            val nd = dist[u] + w
            if (nd < dist[v]) {
                dist[v] = nd
                prev[v] = u              // record: we reached v optimally from u
                pq.add(State(v, nd))
            }
        }
    }
    return dist to prev
}

/**
 * Reconstructs the shortest path from src to dst using the prev array.
 * Returns an empty list if dst is unreachable.
 */
fun buildPath(prev: IntArray, src: Int, dst: Int): List<Int> {
    if (prev[dst] == -1 && dst != src) return emptyList()
    val path = mutableListOf<Int>()
    var cur = dst
    while (cur != -1) {
        path.add(cur)
        cur = prev[cur]
    }
    return path.reversed()
}
```

### Dijkstra: LeetCode Problems

#### LC 743 — Network Delay Time

**Problem**: `n` nodes, directed weighted edges. Signal sent from `k`. How long
until all nodes receive it? Return -1 if any node is unreachable.

**Why Dijkstra**: single-source shortest paths on a non-negative weighted directed graph.
Answer = `max(dist)` after running from source `k`.

```kotlin
fun networkDelayTime(times: Array<IntArray>, n: Int, k: Int): Int {
    // Build 0-indexed adjacency list (input is 1-indexed)
    val adj = Array(n) { mutableListOf<Edge>() }
    for ((u, v, w) in times) adj[u - 1] += Edge(v - 1, w.toLong())

    val dist = dijkstra(k - 1, adj)

    val ans = dist.max()!!
    return if (ans >= INF) -1 else ans.toInt()
}
```

---

#### LC 1514 — Path with Maximum Probability

**Problem**: find the path from `start` to `end` that maximizes the product of
edge probabilities.

**Why Dijkstra**: transform to a shortest-path problem by negating log-probabilities,
OR run a max-probability variant of Dijkstra with a max-heap.

```kotlin
fun maxProbability(
    n: Int,
    edges: Array<IntArray>,
    succProb: DoubleArray,
    start: Int,
    end: Int
): Double {
    // adj stores (neighbor, probability) as doubles
    val adj = Array(n) { mutableListOf<Pair<Int, Double>>() }
    for (i in edges.indices) {
        val (u, v) = edges[i]
        adj[u] += v to succProb[i]
        adj[v] += u to succProb[i]
    }

    // prob[v] = best probability of reaching v from start
    val prob = DoubleArray(n) { 0.0 }
    prob[start] = 1.0

    // Max-heap: process highest probability first (Dijkstra with reversed ordering)
    val pq = PriorityQueue<Pair<Int, Double>>(compareByDescending { it.second })
    pq.add(start to 1.0)

    while (pq.isNotEmpty()) {
        val (u, p) = pq.poll()
        if (p < prob[u]) continue       // stale entry
        if (u == end) return p
        for ((v, edgeProb) in adj[u]) {
            val np = prob[u] * edgeProb
            if (np > prob[v]) {
                prob[v] = np
                pq.add(v to np)
            }
        }
    }
    return 0.0
}
```

---

#### LC 1631 — Path with Minimum Effort

**Problem**: grid of heights; move to adjacent cells; effort = max absolute
height difference along the path. Find min-effort path from top-left to bottom-right.

**Why Dijkstra**: `dist[r][c]` = minimum effort to reach cell `(r, c)`.
The relaxation condition becomes: `effort = max(currentEffort, abs(height diff))`.

```kotlin
fun minimumEffortPath(heights: Array<IntArray>): Int {
    val rows = heights.size
    val cols = heights[0].size
    val dist = Array(rows) { IntArray(cols) { Int.MAX_VALUE } }
    dist[0][0] = 0

    // State: (effort, row, col)
    val pq = PriorityQueue<Triple<Int, Int, Int>>(compareBy { it.first })
    pq.add(Triple(0, 0, 0))

    val dirs = arrayOf(intArrayOf(0,1), intArrayOf(0,-1), intArrayOf(1,0), intArrayOf(-1,0))

    while (pq.isNotEmpty()) {
        val (effort, r, c) = pq.poll()
        if (r == rows - 1 && c == cols - 1) return effort
        if (effort > dist[r][c]) continue

        for ((dr, dc) in dirs) {
            val nr = r + dr; val nc = c + dc
            if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue
            // The effort for this edge is the absolute height difference.
            // The total effort for the path is the max single-edge effort seen.
            val newEffort = maxOf(effort, Math.abs(heights[nr][nc] - heights[r][c]))
            if (newEffort < dist[nr][nc]) {
                dist[nr][nc] = newEffort
                pq.add(Triple(newEffort, nr, nc))
            }
        }
    }
    return 0
}
```

---

#### LC 787 — Cheapest Flights Within K Stops

**Problem**: find cheapest price from `src` to `dst` with at most `k` stops.

**Why modified Dijkstra / Bellman-Ford**: the stop constraint makes this NOT a
plain shortest path. The classic approach is Bellman-Ford with exactly `k+1`
relaxation rounds. A Dijkstra variant also works with state `(node, stops_used)`.

```kotlin
// Bellman-Ford approach (see Part 2 for full Bellman-Ford explanation)
fun findCheapestPrice(n: Int, flights: Array<IntArray>, src: Int, dst: Int, k: Int): Int {
    var dist = IntArray(n) { Int.MAX_VALUE }
    dist[src] = 0

    // Run exactly k+1 rounds (k stops = k+1 edges)
    repeat(k + 1) {
        val prev = dist.copyOf()   // snapshot: only use distances from PREVIOUS round
        for ((u, v, w) in flights) {
            if (prev[u] != Int.MAX_VALUE && prev[u] + w < dist[v]) {
                dist[v] = prev[u] + w
            }
        }
    }

    return if (dist[dst] == Int.MAX_VALUE) -1 else dist[dst]
}
```

---

## Part 2 — Bellman-Ford

### Intuition

Bellman-Ford works on graphs that **may have negative edge weights**, but
**no negative cycles** (when asking for shortest paths — a negative cycle makes
the concept of "shortest path" undefined, but BF can *detect* them).

The key insight is that the shortest path between any two vertices in a graph
with `n` vertices uses **at most `n-1` edges** (otherwise it would revisit a
vertex, forming a cycle, which can be removed to get a shorter or equal path).

So Bellman-Ford simply performs `n-1` full passes over all edges, each time
relaxing every edge. After round `i`, `dist[v]` is guaranteed to be the
shortest path that uses **at most `i` edges**.

If any distance still improves on round `n` (the nth pass), a negative cycle exists.

```
Round 1: all edges relaxed once  -> dist holds shortest 1-edge paths
Round 2: all edges relaxed again -> dist holds shortest 2-edge paths
...
Round n-1:                        -> dist holds true shortest paths (if no neg cycle)
Round n (detection):              -> any improvement means a negative cycle exists
```

### Implementation

```kotlin
/**
 * Bellman-Ford — single-source shortest paths, handles negative weights.
 *
 * @param src   source vertex (0-indexed)
 * @param n     number of vertices
 * @param edges list of (from, to, weight) triples
 * @return      dist array, or null if a negative cycle is reachable from src
 *
 * Time:  O(n * m)  — n-1 rounds, each scanning all m edges
 * Space: O(n)
 */
fun bellmanFord(src: Int, n: Int, edges: List<Triple<Int, Int, Long>>): LongArray? {
    val dist = LongArray(n) { INF }
    dist[src] = 0L

    // n-1 relaxation rounds
    repeat(n - 1) {
        for ((u, v, w) in edges) {
            // Only relax if u is reachable; avoids INF + w overflow
            if (dist[u] < INF && dist[u] + w < dist[v]) {
                dist[v] = dist[u] + w
            }
        }
    }

    // Round n: if any edge still relaxes, a negative cycle exists
    for ((u, v, w) in edges) {
        if (dist[u] < INF && dist[u] + w < dist[v]) {
            return null   // negative cycle detected
        }
    }

    return dist
}
```

### Bellman-Ford with Negative Cycle Identification

```kotlin
/**
 * Returns the set of vertices that are affected by a negative cycle
 * (i.e., can reach a negative cycle or are on one).
 * These vertices have dist = -INF (reachable with arbitrarily small cost).
 */
fun bellmanFordMarkNegCycle(src: Int, n: Int, edges: List<Triple<Int, Int, Long>>): LongArray {
    val dist = LongArray(n) { INF }
    dist[src] = 0L

    repeat(n - 1) {
        for ((u, v, w) in edges) {
            if (dist[u] < INF && dist[u] + w < dist[v]) dist[v] = dist[u] + w
        }
    }

    // Run n more rounds; any vertex whose distance still decreases is
    // on or reachable from a negative cycle — mark it -INF
    repeat(n) {
        for ((u, v, w) in edges) {
            if (dist[u] < INF && dist[u] + w < dist[v]) {
                dist[v] = -INF   // -INF signals "arbitrarily cheap"
            }
        }
    }

    return dist
}
```

### SPFA — Shortest Path Faster Algorithm (Bellman-Ford with a Queue)

SPFA is a practical optimization of Bellman-Ford: instead of scanning ALL edges
each round, only re-relax edges from vertices whose distance just improved.
Average case is much faster than O(nm), but worst case remains O(nm).

```kotlin
/**
 * SPFA (Bellman-Ford + BFS queue optimization).
 * Practically faster than plain Bellman-Ford for sparse graphs.
 * Worst case still O(n * m); use Dijkstra when weights are non-negative.
 *
 * Returns null if a negative cycle is detected (vertex enqueued > n times).
 */
fun spfa(src: Int, adj: Array<List<Edge>>): LongArray? {
    val n = adj.size
    val dist = LongArray(n) { INF }
    val inQueue = BooleanArray(n)
    val enqueueCount = IntArray(n)   // tracks how many times each vertex was enqueued

    dist[src] = 0L
    val queue = ArrayDeque<Int>()
    queue.add(src)
    inQueue[src] = true
    enqueueCount[src] = 1

    while (queue.isNotEmpty()) {
        val u = queue.removeFirst()
        inQueue[u] = false

        for ((v, w) in adj[u]) {
            if (dist[u] + w < dist[v]) {
                dist[v] = dist[u] + w
                if (!inQueue[v]) {
                    queue.add(v)
                    inQueue[v] = true
                    enqueueCount[v]++
                    // A vertex enqueued n or more times signals a negative cycle
                    if (enqueueCount[v] >= n) return null
                }
            }
        }
    }
    return dist
}
```

### Bellman-Ford: LeetCode Problems

#### LC 743 — Network Delay Time (Bellman-Ford version)

Same problem as before, now solved with Bellman-Ford to show the contrast:

```kotlin
fun networkDelayTimeBF(times: Array<IntArray>, n: Int, k: Int): Int {
    val edges = times.map { (u, v, w) -> Triple(u - 1, v - 1, w.toLong()) }
    val dist = bellmanFord(k - 1, n, edges) ?: return -1
    val ans = dist.max()!!
    return if (ans >= INF) -1 else ans.toInt()
}
```

---

#### LC 787 — Cheapest Flights Within K Stops (canonical BF version)

Already shown above in the Dijkstra section. The BF snapshot trick (`prev = dist.copyOf()`
before each round) enforces the "at most K+1 edges" constraint correctly.

---

#### LC 1334 — Find the City With the Smallest Number of Neighbors

**Problem**: given edges with weights and a distance threshold, find the city
that has the fewest cities reachable within the threshold. Ties broken by highest
city index.

**Why Floyd-Warshall**: all-pairs shortest paths needed — see Part 3. But this
problem can also be solved with `n` Dijkstra or Bellman-Ford runs from each source.

---

## Part 3 — Floyd-Warshall

### Intuition

Floyd-Warshall solves **all-pairs shortest paths** (APSP): the shortest distance
between every pair of vertices simultaneously.

The key insight is a dynamic programming recurrence over intermediate vertices:

```
dist[i][j][k] = shortest path from i to j using only vertices {0, 1, ..., k} as intermediates
```

Base case (`k = -1`): `dist[i][j] = direct edge weight, or INF if no edge`.

Transition: either the shortest path from `i` to `j` does NOT go through vertex `k`
(keep old answer), or it DOES (go `i -> k` then `k -> j`):

```
dist[i][j][k] = min(dist[i][j][k-1],  dist[i][k][k-1] + dist[k][j][k-1])
```

Because the two sub-paths `i->k` and `k->j` only use vertices `{0..k-1}` as intermediates,
the 3-D array collapses to a 2-D array updated in-place — the standard implementation.

The triple nested loop visits all `(i, k, j)` triplets: for each intermediate `k`,
try routing every `(i, j)` pair through `k`.

### Implementation

```kotlin
/**
 * Floyd-Warshall — all-pairs shortest paths.
 *
 * @param w   weight matrix: w[i][j] = edge weight from i to j, or INF if no direct edge.
 *            w[i][i] must be 0. Mutated in-place — pass a copy if you need the original.
 * @return    dist matrix (same array, mutated): dist[i][j] = shortest path i->j, or INF
 *
 * Time:  O(n^3)
 * Space: O(n^2)  — the matrix itself; O(1) extra
 *
 * Handles negative edges. Does NOT handle negative cycles (dist[i][i] < 0 after running
 * signals that vertex i is on a negative cycle).
 */
fun floydWarshall(w: Array<LongArray>): Array<LongArray> {
    val n = w.size

    // Try each vertex k as an intermediate relay point
    for (k in 0 until n) {
        for (i in 0 until n) {
            for (j in 0 until n) {
                // Guard: only relax if both i->k and k->j are reachable
                if (w[i][k] < INF && w[k][j] < INF) {
                    w[i][j] = minOf(w[i][j], w[i][k] + w[k][j])
                }
            }
        }
    }

    return w
}

/**
 * Builds the initial weight matrix from an edge list.
 * Self-loops are 0; all other pairs start at INF.
 */
fun buildMatrix(n: Int, edges: List<Triple<Int, Int, Long>>): Array<LongArray> {
    val w = Array(n) { i -> LongArray(n) { j -> if (i == j) 0L else INF } }
    for ((u, v, wt) in edges) {
        w[u][v] = minOf(w[u][v], wt)   // handle multi-edges: keep smallest
    }
    return w
}

/**
 * Detects negative cycles after running Floyd-Warshall.
 * If dist[i][i] < 0 for any i, vertex i is on a negative cycle.
 */
fun hasNegativeCycle(dist: Array<LongArray>): Boolean =
    dist.indices.any { i -> dist[i][i] < 0L }
```

### Floyd-Warshall with Path Reconstruction

```kotlin
/**
 * Floyd-Warshall that also records the path via a 'next' matrix.
 * next[i][j] = the first step to take from i to reach j optimally.
 *
 * To reconstruct path i -> j:
 *   path = [i]
 *   while i != j: i = next[i][j]; path += i
 */
fun floydWarshallWithPath(w: Array<LongArray>): Pair<Array<LongArray>, Array<IntArray>> {
    val n = w.size
    // next[i][j] = direct next hop from i toward j; -1 if no path
    val next = Array(n) { i -> IntArray(n) { j -> if (w[i][j] < INF && i != j) j else -1 } }

    for (k in 0 until n) {
        for (i in 0 until n) {
            for (j in 0 until n) {
                if (w[i][k] < INF && w[k][j] < INF && w[i][k] + w[k][j] < w[i][j]) {
                    w[i][j] = w[i][k] + w[k][j]
                    next[i][j] = next[i][k]   // go via k: first hop i->k is same as i->j now
                }
            }
        }
    }
    return w to next
}

fun reconstructPath(next: Array<IntArray>, i: Int, j: Int): List<Int> {
    if (next[i][j] == -1) return emptyList()
    val path = mutableListOf(i)
    var cur = i
    while (cur != j) {
        cur = next[cur][j]
        path.add(cur)
    }
    return path
}
```

### Floyd-Warshall: LeetCode Problems

#### LC 1334 — Find the City With the Smallest Number of Neighbors at a Threshold Distance

**Problem**: find the city that can reach the fewest other cities within `distanceThreshold`.
Ties: return the city with the greatest index.

**Why Floyd-Warshall**: we need all-pairs distances; n <= 100 so O(n^3) is fine.

```kotlin
fun findTheCity(n: Int, edges: Array<IntArray>, distanceThreshold: Int): Int {
    val dist = Array(n) { i -> LongArray(n) { j -> if (i == j) 0L else INF } }
    for ((u, v, w) in edges) {
        dist[u][v] = w.toLong()
        dist[v][u] = w.toLong()
    }

    floydWarshall(dist)   // mutates dist in-place

    var bestCity = -1
    var bestCount = Int.MAX_VALUE

    // For each city, count reachable cities within threshold
    for (i in 0 until n) {
        val count = dist[i].count { d -> d <= distanceThreshold }
        // Prefer higher index on tie (>= instead of >)
        if (count <= bestCount) {
            bestCount = count
            bestCity = i
        }
    }
    return bestCity
}
```

---

#### LC 399 — Evaluate Division

**Problem**: given equations like `A/B = k`, answer queries `X/Y = ?`.
Model as a weighted graph: edge `A -> B` with weight `k`, `B -> A` with `1/k`.
Query `X/Y` = product of weights on path `X -> Y`.

**Why Floyd-Warshall**: the "combination" of two paths is multiplication, not addition —
but the DP recurrence is the same shape. Replace `+` with `*` and `min` with `max`.

```kotlin
fun calcEquation(
    equations: List<List<String>>,
    values: DoubleArray,
    queries: List<List<String>>
): DoubleArray {
    // Assign integer IDs to string variables
    val id = mutableMapOf<String, Int>()
    equations.forEach { (a, b) ->
        id.getOrPut(a) { id.size }
        id.getOrPut(b) { id.size }
    }
    val n = id.size

    // dist[i][j] = value of i/j; 0.0 = unknown
    val dist = Array(n) { i -> DoubleArray(n) { j -> if (i == j) 1.0 else 0.0 } }
    for ((eq, v) in equations.zip(values.toList())) {
        val a = id[eq[0]]!!; val b = id[eq[1]]!!
        dist[a][b] = v
        dist[b][a] = 1.0 / v
    }

    // Floyd-Warshall with multiplication instead of addition
    for (k in 0 until n)
        for (i in 0 until n)
            for (j in 0 until n)
                if (dist[i][k] != 0.0 && dist[k][j] != 0.0)
                    dist[i][j] = dist[i][k] * dist[k][j]

    return DoubleArray(queries.size) { qi ->
        val (xs, ys) = queries[qi]
        val x = id[xs]; val y = id[ys]
        if (x == null || y == null) -1.0 else dist[x][y].let { if (it == 0.0) -1.0 else it }
    }
}
```

---

#### LC 1462 — Course Schedule IV (Reachability Queries)

**Problem**: given prerequisite pairs, answer reachability queries: "can I reach
course `j` from course `i`?"

**Why Floyd-Warshall**: treat edge weights as booleans (`true`/`false`) and replace
`min` with `||` and `+` with `&&`. This is the **transitive closure** variant.

```kotlin
fun checkIfPrerequisite(
    numCourses: Int,
    prerequisites: Array<IntArray>,
    queries: Array<IntArray>
): List<Boolean> {
    val n = numCourses
    val reach = Array(n) { BooleanArray(n) }

    for ((u, v) in prerequisites) reach[u][v] = true

    // Floyd-Warshall for transitive closure
    for (k in 0 until n)
        for (i in 0 until n)
            for (j in 0 until n)
                if (reach[i][k] && reach[k][j]) reach[i][j] = true

    return queries.map { (u, v) -> reach[u][v] }
}
```

---

## Part 4 — Decision Guide

```
Do you have negative edge weights?
|
+-- NO (all weights >= 0)
|     |
|     +-- Single source to one or all targets?
|     |     YES --> Dijkstra   O((n+m) log n)
|     |
|     +-- All pairs?
|           |
|           +-- n is small (n <= 400)?
|           |     YES --> n x Dijkstra  O(n * (n+m) log n)
|           |             OR Floyd-Warshall O(n^3)
|           +-- n is large?
|                 YES --> n x Dijkstra (FW would TLE)
|
+-- YES (negative weights possible)
      |
      +-- Negative cycles possible?
      |     |
      |     +-- Need to DETECT them?
      |     |     YES --> Bellman-Ford (returns null on neg cycle)
      |     +-- Shortest path undefined if neg cycle reachable?
      |           YES --> Bellman-Ford with neg-cycle marking
      |
      +-- No negative cycles, single source?
      |     YES --> Bellman-Ford  O(n*m)
      |             or SPFA (faster in practice)
      |
      +-- No negative cycles, all pairs?
            YES --> Floyd-Warshall  O(n^3)

Special cases:
  - K-stop constraint            --> Bellman-Ford with K rounds
  - Path with max probability    --> Dijkstra with max-heap
  - Reachability / connectivity  --> Floyd-Warshall transitive closure
  - Unweighted graph             --> BFS (O(n+m), beats all of the above)
```

---

## Part 5 — Complexity & Constraints Summary

| Algorithm | Time | Space | Negative Weights | Negative Cycles | Use Case |
|---|---|---|---|---|---|
| Dijkstra | O((n+m) log n) | O(n+m) | No | No | SSSP, non-neg weights |
| Bellman-Ford | O(n*m) | O(n) | Yes | Detects | SSSP, neg weights |
| SPFA | O(n*m) worst | O(n+m) | Yes | Detects | BF optimization |
| Floyd-Warshall | O(n^3) | O(n^2) | Yes | Detects | APSP, small n |

**Practical n limits** (competitive programming, ~10^8 ops/sec):

| Algorithm | Safe n | Safe m |
|---|---|---|
| Dijkstra | n <= 10^5 | m <= 3*10^5 |
| Bellman-Ford | n <= 10^3 | m <= 10^4 |
| SPFA | n <= 10^4 | m <= 10^5 (avg case) |
| Floyd-Warshall | n <= 500 | N/A (dense OK) |

---

## Part 6 — Common Pitfalls

**INF + w overflow**
Never use `Int.MAX_VALUE` or `Long.MAX_VALUE` as INF directly.
Adding any positive weight overflows to a negative number, making
unreachable nodes look reachable and breaking all comparisons.
Use `Long.MAX_VALUE / 2` — it stays positive after one addition.

**Dijkstra on negative weights**
Dijkstra's greedy settlement is wrong when edges are negative. A settled
vertex can be "unsettled" by a path going through a negative edge later.
Always use Bellman-Ford or SPFA when any weight can be negative.

**Lazy deletion heap entries**
Dijkstra with a lazy heap can have O(m) entries in the priority queue.
Always check `if (d > dist[u]) continue` immediately after popping.
Without this, you re-process stale entries and get wrong results.

**Bellman-Ford round count**
The loop must run exactly `n-1` times for a graph with `n` vertices,
NOT `n` (that is used for detection). Off-by-one here causes silent
wrong answers on the last possible longest path.

**Floyd-Warshall loop order**
The intermediate vertex `k` MUST be the outermost loop.
`for k ... for i ... for j` is correct.
`for i ... for j ... for k` is WRONG — it tries to use `k` as intermediate
before all `(i, k)` and `(k, j)` sub-paths are finalized.

**Floyd-Warshall self-loops**
Initialize `dist[i][i] = 0` before running, not INF.
After running, check `dist[i][i] < 0` to detect negative cycles.

**SPFA and negative cycles**
SPFA's enqueue-count heuristic (`>= n`) can give false negatives on
adversarial inputs. For guaranteed detection, prefer plain Bellman-Ford's
nth-round check.

**Undirected graphs**
For an undirected edge `(u, v, w)`, add BOTH `adj[u] += Edge(v, w)` and
`adj[v] += Edge(u, w)`. Forgetting one direction gives asymmetric distances.
For Floyd-Warshall, set BOTH `w[u][v]` and `w[v][u]`.
