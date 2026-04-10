# leetcode-core

> Kotlin LeetCode solutions built for understanding, not just passing.  
> Clarity-first code paired with deep-dive markdown guides on algorithms and data structures.

---

## What This Repo Is

A personal, actively maintained collection of LeetCode solutions in **Kotlin** (97.8%) with a companion **C++** track — plus a growing library of annotated DSA reference guides written from first principles.

The philosophy: **correctness over cleverness, readability over raw optimization.**  
Solutions are written the way code should be read in a review — named well, structured clearly, and explained where the logic is non-obvious.

---

## Repository Structure

```
leetcode-core/
├── kotlin-solutions/     # Primary solution track — idiomatic Kotlin
├── cpp-solutions/        # C++ companion track
├── ClaudeExplainMd/      # Deep-dive markdown DSA reference guides
└── README.md
```

### kotlin-solutions

Files are named by LeetCode problem number:

```
1.kt        # Two Sum
42.kt       # Trapping Rain Water
1114.kt     # Print in Order (concurrency)
```

Find any solution instantly:

```bash
# in your editor: Ctrl+P / Cmd+P, type the problem number
# on the command line:
ls kotlin-solutions/743.kt
```

### ClaudeExplainMd

Standalone markdown references built alongside the solutions.  
Each guide covers: intuition, annotated implementation, internal mechanics, LeetCode problem walkthroughs, a decision guide, complexity summary, and common pitfalls.

Current guides:

| File | Topics Covered |
|---|---|
| `dijkstra-bellmanFord-floydWarshall.md` | Dijkstra, Bellman-Ford, SPFA, Floyd-Warshall - full annotated implementations, path reconstruction, LeetCode walkthroughs, decision guide |
| `Dijkstra-ref.md` | Dijkstra deep-dive - heap variants, lazy deletion, State/Edge types, single-source to all-pairs |
| `Bellman-Ford-FP-Kotlin.md` | Bellman-Ford in idiomatic functional Kotlin - fold-based relaxation, negative cycle detection |
| `Bidirectional-BFS.md` | Bidirectional BFS - meet-in-the-middle search, word ladder pattern, complexity reduction |
| `N-Queens.md` | N-Queens backtracking - constraint propagation, bitmask optimization, solution counting |
| `TwoPointersWindow.md` | Two pointers and sliding window - fixed/variable window, same-direction vs opposite-direction patterns |
| `common-leetcode-patterns.md` | Catalog of recurring CP patterns - frequency counter, prefix sum, monotonic stack, intervals |
| `ConstraintsAnalysis.md` | Reading constraints to infer required complexity - n <= 10^6 means O(n log n), etc. |
| `kotlin-competitive-Programming-io.md` | Competitive programming I/O in Kotlin - fast reader, buffered writer, template structure |
| `kotlin-fastest-io.md` | Byte-buffer fast I/O - `BufferedInputStream`, `nextInt` / `nextLong` / `nextString` internals |
| `kotlin-fenwick-segment.md` | Fenwick Tree (1D + 2D), Segment Tree, Lazy Propagation, generic monoid seg tree |
| `kt-features-leetcode.md` | Kotlin stdlib features for LeetCode - `withIndex`, `groupBy`, `fold`, `buildList`, destructuring |
| `kt-labels-deep-dive` | Kotlin labeled returns, labeled breaks, and `run`/`let`/`also` scoping in algorithm code |
| `kt-leetcode-comprehensive-features.md` | Comprehensive Kotlin feature reference for competitive programming and interview contexts |
| `jvm-kotlin-concurrency-ref.md` | JVM concurrency in Kotlin - coroutines, `ReentrantLock`, `Semaphore`, LeetCode 1114/1115/1116/1117 |
| `set-mismatch-guide.md` | Set Mismatch (LC 645) - XOR trick, math approach, cycle detection in index mapping |
| `2073-time-needed-to-buy-tickets.md` | Time Needed to Buy Tickets (LC 2073) - queue simulation vs O(n) closed-form derivation |
| `Kt-labels-deep-dive` | Kotlin non-local returns, loop labels, and scope function chaining in complex control flow |

---

## Problem Categories

| Category | Examples |
|---|---|
| Arrays & Strings | Two Sum, Sliding Window, Two Pointers |
| Graphs | Dijkstra, BFS/DFS, Topological Sort |
| Dynamic Programming | Knapsack, LIS, Interval DP |
| Trees | BFS level-order, DFS inversion, BST patterns |
| Multithreading | Print in Order, Dining Philosophers (JVM concurrency) |
| Backtracking | N-Queens, Permutations, Subsets |
| Heap / Priority Queue | Kth Largest, Median Stream, Top K |
| Bit Manipulation | XOR tricks, Bitmask DP |

---

## Design Standards

**Language**: Kotlin (primary), C++ (companion)

**Naming**: expressive over terse — `settledNodes` not `vis`, `relaxedCost` not `d`

**Style**:
- Idiomatic Kotlin stdlib (`withIndex`, `groupBy`, `fold`, `buildList`, etc.)
- Named types over raw primitives where it adds clarity (`data class Edge(val to: Int, val cost: Long)`)
- Numbered comments on non-trivial algorithm phases
- No micro-optimizations that obscure intent

**Performance**: solutions target the asymptotically correct complexity class.  
Constant-factor tuning only when the naive approach would TLE on the given constraints.

---

## Running Solutions

Solutions are standalone Kotlin files.  
Any of the following work:

```bash
# IntelliJ / Android Studio — open the file, Run > Run 'MainKt'

# kotlinc CLI
kotlinc kotlin-solutions/743.kt -include-runtime -d 743.jar
java -jar 743.jar

# online: paste directly into LeetCode's Kotlin editor
```

---

## Roadmap

- [ ] Add solutions index with problem names, difficulty, and tags
- [ ] Expand `ClaudeExplainMd` — Union-Find, Trie, Monotonic Stack, Binary Search patterns
- [ ] Add JUnit 5 test files alongside solutions
- [ ] C++ parity for graph and DP sections

---

## About

Built by [Ahmed](https://github.com/deadboyccc) — Senior Backend Engineer at QiCard, Baghdad.  
Working toward Staff / Systems Architect at a major tech company.  
Self-studying a 70-book, 5-phase curriculum spanning JVM internals, distributed systems, and cloud-native architecture.

> *"Code should first be understood, then optimized."*
