# Dijkstra's Shortest Path — C++ → Kotlin Reference

> **Problem:** Single-source shortest path on a directed weighted graph with multiple test cases.
> **Closest LeetCode:** 743 · Network Delay Time / 1514 · Path with Maximum Probability
> **Competitive judge:** SPOJ SHPATH ("The Shortest Path")
> **Output:** Minimum cost from `start → end`, or `NO` if unreachable.

---

## Table of Contents
1. [Algorithm Overview](#1-algorithm-overview)
2. [C++ Implementation](#2-c-implementation)
3. [Kotlin Implementation](#3-kotlin-implementation)
4. [Language Mapping](#4-language-mapping)
5. [Complexity Analysis](#5-complexity-analysis)
6. [Key Design Decisions](#6-key-design-decisions)

---

## 1. Algorithm Overview

Dijkstra's algorithm finds the shortest path in a graph with **non-negative edge weights**.

```
Phase 0 — Build adjacency list from input edges
Phase 1 — Init: dist[start] = 0, dist[all others] = ∞, push start onto min-heap
Phase 2 — Settle: pop lowest-cost node; if already settled (stale), skip
Phase 3 — Relax: for each neighbour, if new cost < known cost → update dist, push to heap
           Stop early when destination node is settled
```

The two implementations differ in **one structural choice**: how they maintain the min-heap.

| Strategy | C++ | Kotlin |
|---|---|---|
| Heap type | `std::set` (ordered BST) | `PriorityQueue` (binary heap) |
| Decrease-key | Explicit: erase stale entry, reinsert | Lazy: push new entry, skip stale on poll |
| Complexity | O((V + E) log V) | O((V + E) log E) — same order, larger constant factor avoided in practice |

---

## 2. C++ Implementation

```cpp
#include <algorithm>
#include <cstdint>
#include <iostream>
#include <limits>
#include <set>
#include <utility>
#include <vector>

// ── Type aliases ─────────────────────────────────────────────────────────────
// Semantic names over raw primitives; uint16_t keeps memory tight for large graphs.
using Vertex    = std::uint16_t;
using Cost      = std::uint16_t;
using Edge      = std::pair<Vertex, Cost>;          // (destination, weight)
using Graph     = std::vector<std::vector<Edge>>;   // adjacency list
using CostTable = std::vector<std::uint64_t>;       // dist[] — wide enough to hold summed costs

// Sentinel: unreachable node marker. Max of CostTable's value type.
constexpr auto kInfiniteCost{ std::numeric_limits<CostTable::value_type>::max() };

// ── Dijkstra ─────────────────────────────────────────────────────────────────
// Returns min-cost from [start] to [end] in [graph], writing distances into [costTable].
// Uses std::set as an updatable min-heap: O(log V) erase + reinsert for decrease-key.
auto dijkstra(Vertex const start, Vertex const end,
              Graph const& graph, CostTable& costTable)
{
    // Phase 1 — Init
    std::fill(costTable.begin(), costTable.end(), kInfiniteCost);
    costTable[start] = 0;

    // set<(cost, vertex)> — ordered ascending by cost; acts as a min-heap.
    // Storing cost first makes the natural ordering give cheapest node at begin().
    std::set<std::pair<CostTable::value_type, Vertex>> minHeap;
    minHeap.emplace(0, start);

    // Phase 2 — Settle
    while (!minHeap.empty())
    {
        auto const vertexCost{ minHeap.begin()->first  };   // cheapest unsettled node
        auto const vertex    { minHeap.begin()->second };
        minHeap.erase(minHeap.begin());

        if (vertex == end) break;   // destination settled — shortest path found

        // Phase 3 — Relax neighbours
        for (auto const& neighbourEdge : graph[vertex])
        {
            auto const& neighbour{ neighbourEdge.first  };
            auto const& cost     { neighbourEdge.second };

            if (costTable[neighbour] > vertexCost + cost)
            {
                // Decrease-key: must remove stale entry before updating,
                // otherwise the set holds two entries for the same vertex.
                minHeap.erase({ costTable[neighbour], neighbour });
                costTable[neighbour] = vertexCost + cost;
                minHeap.emplace(costTable[neighbour], neighbour);
            }
        }
    }

    return costTable[end];
}

// ── Entry point ───────────────────────────────────────────────────────────────
int main()
{
    constexpr std::uint16_t maxVertices{ 10000 };

    // Phase 0 — Preallocate once; clear per test case to avoid reallocation cost.
    Graph     graph    (maxVertices);
    CostTable costTable(maxVertices);

    std::uint16_t testCases;
    std::cin >> testCases;

    while (testCases-- > 0)
    {
        // Clear adjacency lists — reuse allocated capacity.
        for (auto i{ 0 }; i < maxVertices; ++i) graph[i].clear();

        std::uint16_t numberOfVertices, numberOfEdges;
        std::cin >> numberOfVertices >> numberOfEdges;

        for (auto i{ 0 }; i < numberOfEdges; ++i)
        {
            Vertex from, to;
            Cost   cost;
            std::cin >> from >> to >> cost;
            graph[from].emplace_back(to, cost);   // directed edge only
        }

        Vertex start, end;
        std::cin >> start >> end;

        auto const result{ dijkstra(start, end, graph, costTable) };

        // kInfiniteCost → no path exists
        if (result == kInfiniteCost) std::cout << "NO\n";
        else                         std::cout << result << '\n';
    }

    return 0;
}
```

---

## 3. Kotlin Implementation

```kotlin
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.PriorityQueue
import java.util.StringTokenizer

// ── Type aliases ─────────────────────────────────────────────────────────────
// Mirrors C++ using-declarations; keeps call sites readable.
typealias Vertex = Int
typealias Cost   = Long

// Edge as a data class — destructures cleanly in for-loops: (neighbour, edgeCost)
data class Edge(val to: Vertex, val cost: Cost)

// INF halved vs Long.MAX_VALUE → safe against overflow during `nodeCost + edgeCost`.
// C++ equivalent: kInfiniteCost = UINT64_MAX (overflow not an issue there: unsigned wraps,
// but the check `costTable[n] > cost + e` would be false for wrapped values anyway).
private const val INF = Long.MAX_VALUE / 2

// ── Dijkstra ─────────────────────────────────────────────────────────────────
// Returns min-cost from [start] to [end], or INF if unreachable.
// Uses lazy-deletion min-heap instead of C++'s erase-then-reinsert set strategy:
//   — push updated entry alongside old one
//   — discard stale entries on poll via `if (nodeCost > dist[node]) continue`
fun dijkstra(start: Vertex, end: Vertex, graph: Array<MutableList<Edge>>): Cost {

    // Phase 1 — Init
    val dist = LongArray(graph.size) { INF }
    dist[start] = 0L

    // min-heap keyed on accumulated cost; Pair<Cost, Vertex> matches C++'s set element type.
    val heap = PriorityQueue<Pair<Cost, Vertex>>(compareBy { it.first })
    heap.offer(0L to start)

    // Phase 2 — Settle
    while (heap.isNotEmpty()) {
        val (nodeCost, node) = heap.poll()

        if (nodeCost > dist[node]) continue   // stale entry (lazy deletion) — skip
        if (node == end) break                // destination settled — done

        // Phase 3 — Relax outgoing edges
        for ((neighbour, edgeCost) in graph[node]) {
            val relaxed = nodeCost + edgeCost
            if (relaxed < dist[neighbour]) {
                dist[neighbour] = relaxed
                // Push new entry; old entry with higher cost will be skipped lazily.
                // C++ equivalent: erase(old) + emplace(new) in the set.
                heap.offer(relaxed to neighbour)
            }
        }
    }

    return dist[end]
}

// ── Entry point ───────────────────────────────────────────────────────────────
fun main() {
    val br  = BufferedReader(InputStreamReader(System.`in`))
    val out = StringBuilder()   // batch output — avoids per-line flush overhead

    fun nextTokenizer() = StringTokenizer(br.readLine())

    val testCases = br.readLine().trim().toInt()

    repeat(testCases) {
        // Phase 0 — Build adjacency list per test case (n+1 for 1-based vertex ids)
        var st = nextTokenizer()
        val n = st.nextToken().toInt()   // vertex count
        val m = st.nextToken().toInt()   // edge count

        // Fresh allocation per test case — unlike C++ which preallocates a global pool.
        // GC overhead is acceptable here; graph sizes are bounded per test case.
        val graph = Array(n + 1) { mutableListOf<Edge>() }

        repeat(m) {
            st = nextTokenizer()
            val from = st.nextToken().toInt()
            val to   = st.nextToken().toInt()
            val cost = st.nextToken().toLong()
            graph[from] += Edge(to, cost)   // directed edge
        }

        st = nextTokenizer()
        val start = st.nextToken().toInt()
        val end   = st.nextToken().toInt()

        val result = dijkstra(start, end, graph)
        out.appendLine(if (result == INF) "NO" else result)
    }

    print(out)
}
```

---

## 4. Language Mapping

### Type System

| C++ | Kotlin | Notes |
|---|---|---|
| `using Vertex = uint16_t` | `typealias Vertex = Int` | Kotlin typealias is an alias only — no distinct type safety. C++ using-decl is equivalent. |
| `using Cost = uint16_t` | `typealias Cost = Long` | Kotlin widens to `Long` upfront; C++ uses narrow input type and widens only in `CostTable`. |
| `using Edge = pair<Vertex, Cost>` | `data class Edge(val to, val cost)` | `data class` gives named fields + free destructuring. C++ `pair` uses `.first`/`.second`. |
| `using Graph = vector<vector<Edge>>` | `Array<MutableList<Edge>>` | Direct structural equivalent. |
| `using CostTable = vector<uint64_t>` | `LongArray` (inside `dijkstra`) | Kotlin inlines `dist[]` as a local — no need to pass it in since each call is fresh. |

### Infinity Sentinel

```
C++:    constexpr kInfiniteCost = numeric_limits<uint64_t>::max()
          → Safe because unsigned arithmetic wraps; but checking > after addition
            would silently overflow, so the erase-before-update strategy avoids that path.

Kotlin: const INF = Long.MAX_VALUE / 2
          → Halved because `nodeCost + edgeCost` uses signed Long; full MAX_VALUE
            would overflow to a negative, corrupting the < relaxation check.
```

### Heap Strategy: Decrease-Key vs Lazy Deletion

```
C++ std::set (BST — O(log V) erase + O(log V) insert):
  if (costTable[neighbour] > vertexCost + cost) {
      minHeap.erase({ costTable[neighbour], neighbour });  // remove stale
      costTable[neighbour] = vertexCost + cost;
      minHeap.emplace(costTable[neighbour], neighbour);    // reinsert updated
  }
  → At most one entry per vertex in the set at any time.
  → More memory-efficient; no duplicate entries.

Kotlin PriorityQueue (binary heap — O(log E) offer, no efficient arbitrary erase):
  if (relaxed < dist[neighbour]) {
      dist[neighbour] = relaxed
      heap.offer(relaxed to neighbour)    // push new; old stays in heap
  }
  // On poll: if (nodeCost > dist[node]) continue  ← discard stale
  → Heap may hold O(E) entries vs O(V) for the set approach.
  → Simpler code; faster in practice on JVM due to lower constant factors.
```

### I/O

| C++ | Kotlin | Reason |
|---|---|---|
| `std::cin >>` | `BufferedReader` + `StringTokenizer` | JVM's `Scanner` is ~10× slower for competitive I/O; `BufferedReader` matches C++ stdin speed. |
| `std::cout <<` | `StringBuilder` + single `print` | Avoids per-line JVM flush; batches all output. |
| Global preallocated `graph[10000]` + `clear()` per case | `Array(n+1)` fresh per case | C++ avoids reallocation cost. Kotlin trades that for simplicity; GC cost is negligible per case. |

### Structural Flow

```
C++ main()                          Kotlin main()
─────────────────────────────────   ──────────────────────────────────
preallocate graph[10000]            (no preallocate — per-case alloc)
read T test cases                   repeat(testCases)
  clear graph[]                       val graph = Array(n+1) { ... }
  read n, m                           read n, m
  read m edges → graph[from]          read m edges → graph[from]
  read start, end                     read start, end
  dijkstra(start, end, graph,         dijkstra(start, end, graph)
           costTable)                   ↳ dist[] is local here
  print result or "NO"                out.appendLine(result or "NO")
                                    print(out)
```

---

## 5. Complexity Analysis

Let **V** = vertices, **E** = edges (per test case), **T** = test cases.

### Time Complexity

| Phase | C++ | Kotlin |
|---|---|---|
| Build graph | O(E) | O(E) |
| Init dist[] | O(V) | O(V) |
| Heap operations | O((V + E) log V) | O((V + E) log E) |
| **Total per test case** | **O((V + E) log V)** | **O((V + E) log E)** |
| **Total all cases** | **O(T · (V + E) log V)** | **O(T · (V + E) log E)** |

> `log V` vs `log E`: In sparse graphs V ≈ E so they're equal. In dense graphs E = O(V²), making Kotlin's lazy heap `O(log V²) = O(2 log V)` — same asymptotic class, small constant difference.

### Space Complexity

| Structure | C++ | Kotlin |
|---|---|---|
| Adjacency list | O(V + E) — preallocated globally | O(V + E) — per test case, GC'd |
| dist[] / costTable | O(V) — reused globally | O(V) — local to `dijkstra`, GC'd |
| Heap (C++ set) | O(V) — at most one entry per vertex | — |
| Heap (Kotlin PQ) | — | O(E) — may hold duplicate entries |
| **Total** | **O(V + E)** (amortized across cases) | **O(V + E)** per case |

> C++ wins on memory reuse (global preallocated pool, cleared per case). Kotlin allocates fresh per case — fine for the given constraints, but C++ approach is preferable under tight memory limits.

---

## 6. Key Design Decisions

### Why `std::set` over `std::priority_queue` in C++?
`std::priority_queue` does not support `erase` of arbitrary elements. The set's BST structure allows O(log V) removal by value, enabling true decrease-key semantics. The trade-off: `std::set` has higher constant factors than a binary heap.

### Why lazy deletion in Kotlin?
`PriorityQueue` also lacks arbitrary `remove` by value in O(log n). The idiomatic JVM approach is lazy deletion: push duplicates, skip stale entries on poll. Heap size grows to O(E) in the worst case, but the `if (nodeCost > dist[node]) continue` guard makes correctness identical to the set approach.

### Why `INF = Long.MAX_VALUE / 2`?
Signed overflow is **undefined behavior in C++** and throws an `ArithmeticException` (or silently wraps in Kotlin with no overflow checks). The relaxation step `nodeCost + edgeCost` would overflow if `nodeCost = MAX_VALUE`. Halving leaves ample headroom for any realistic edge cost sum.

### Why `BufferedReader` + `StringTokenizer` in Kotlin?
`Scanner` tokenizes lazily with regex under the hood — measured ~5–10× slower than `BufferedReader` for large inputs. `StringTokenizer` avoids regex overhead. This pattern is standard for JVM competitive programming where TLE is otherwise common.
