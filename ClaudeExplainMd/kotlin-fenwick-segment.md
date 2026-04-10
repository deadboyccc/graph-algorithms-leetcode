# Fenwick Tree & Segment Tree — Kotlin Reference

Complete annotated implementations with intuition, LeetCode problems, and idiomatic Kotlin.

---

## Part 1 — Fenwick Tree (Binary Indexed Tree)

### Intuition

Imagine you have an array and you need to answer two operations repeatedly:
- **Point update**: change one element
- **Prefix sum query**: sum of `arr[0..i]`

A plain array does updates in O(1) but queries in O(n).
A prefix-sum array does queries in O(1) but updates in O(n).
A Fenwick tree does **both in O(log n)** using a clever bit trick.

**The core idea**: index `i` in the Fenwick tree stores the sum of a range whose
length is exactly `i & (-i)` — the value of the **lowest set bit** of `i`.

```
Index (1-based):  1   2   3   4   5   6   7   8
Binary:          001 010 011 100 101 110 111 1000
Lowest set bit:   1   2   1   4   1   2   1   8
Range covered:   [1] [1,2] [3] [1,4] [5] [5,6] [7] [1,8]
```

Each node "owns" a bucket of that width.  
Update: propagate upward by adding `i & (-i)` (move to parent).  
Query: sum downward by subtracting `i & (-i)` (strip the lowest bit).

---

### Implementation

```kotlin
/**
 * 1-indexed Fenwick Tree (Binary Indexed Tree).
 *
 * @param n  number of elements (tree is sized n+1; index 0 is unused)
 */
class FenwickTree(private val n: Int) {

    private val tree = LongArray(n + 1)

    /**
     * Adds [delta] to position [i] (1-indexed) and propagates upward.
     *
     * i += i & (-i)  moves i to the next node that "covers" position i.
     * This is the parent in the Fenwick tree's implicit structure.
     *
     * Example with n=8, i=3 (binary 011):
     *   i=3  → update tree[3]   (covers [3,3])
     *   i=4  → update tree[4]   (covers [1,4])
     *   i=8  → update tree[8]   (covers [1,8])
     */
    fun update(i: Int, delta: Long) {
        var x = i
        while (x <= n) {
            tree[x] += delta
            x += x and (-x)      // climb to next responsible ancestor
        }
    }

    /**
     * Returns the prefix sum of [1..i] (1-indexed).
     *
     * i -= i & (-i)  strips the lowest set bit, jumping to the previous
     * disjoint bucket that together build up the prefix sum.
     *
     * Example with i=7 (binary 111):
     *   i=7  → add tree[7]   (covers [7,7])
     *   i=6  → add tree[6]   (covers [5,6])
     *   i=4  → add tree[4]   (covers [1,4])
     *   i=0  → stop
     */
    fun query(i: Int): Long {
        var x = i
        var sum = 0L
        while (x > 0) {
            sum += tree[x]
            x -= x and (-x)      // jump to previous disjoint bucket
        }
        return sum
    }

    /**
     * Range sum query [l..r] (both 1-indexed, inclusive).
     * Uses the prefix-sum identity: sum(l,r) = prefix(r) - prefix(l-1).
     */
    fun query(l: Int, r: Int): Long = query(r) - query(l - 1)

    /**
     * Point update for 0-indexed callers — translates internally to 1-indexed.
     * Keeps the call site cleaner when the input array is 0-indexed.
     */
    fun update0(i: Int, delta: Long) = update(i + 1, delta)
    fun query0(i: Int): Long        = query(i + 1)
    fun query0(l: Int, r: Int): Long = query(l + 1, r + 1)
}
```

---

### Fenwick: LeetCode Problems

#### LC 307 — Range Sum Query (Mutable)

**Problem**: given an int array, support `update(i, val)` and `sumRange(l, r)`.  
**Why Fenwick**: canonical point-update + range-query; O(log n) both.

```kotlin
class NumArray(nums: IntArray) {

    private val n = nums.size
    private val bit = FenwickTree(n)

    init {
        // Build in O(n log n).
        // O(n) build exists (see note below) but rarely needed on LC.
        for (i in nums.indices) bit.update(i + 1, nums[i].toLong())
    }

    fun update(index: Int, value: Int) {
        // We stored the original value implicitly; to change it we add the delta.
        // To support this cleanly, track the original array separately.
        // Simpler LC version: store nums, compute delta on update.
        bit.update(index + 1, value.toLong())
    }

    fun sumRange(left: Int, right: Int): Int =
        bit.query(left + 1, right + 1).toInt()
}
```

> **O(n) build trick**: instead of n updates, assign directly:
> `tree[i] += delta` then `tree[i + (i and -i)] += tree[i]` for each i.
> This is O(n) but only worth it when n > 10⁶.

---

#### LC 315 — Count of Smaller Numbers After Self

**Problem**: for each element, count how many elements to its right are smaller.  
**Why Fenwick**: process right-to-left; for each element query how many seen
values are < current, then record current value.  
**Key insight**: coordinate-compress values to `[1..n]` first.

```kotlin
fun countSmaller(nums: IntArray): List<Int> {
    // 1. Coordinate compress: rank each value in sorted order
    val sorted = nums.toSortedSet().toList()
    val rank = sorted.withIndex().associate { (i, v) -> v to (i + 1) }
    //   rank maps original value → 1-based rank

    val bit = FenwickTree(nums.size)
    val result = IntArray(nums.size)

    // 2. Traverse right to left
    for (i in nums.indices.reversed()) {
        val r = rank[nums[i]]!!
        // How many already-seen values have rank < r?
        result[i] = if (r > 1) bit.query(r - 1).toInt() else 0
        // Record this value
        bit.update(r, 1L)
    }

    return result.toList()
}
```

**Complexity**: O(n log n) — both compress sort and n Fenwick ops.

---

#### LC 493 — Reverse Pairs

**Problem**: count pairs `(i, j)` where `i < j` and `nums[i] > 2 * nums[j]`.  
**Pattern**: modified merge-sort OR Fenwick with coordinate compression.

```kotlin
fun reversePairs(nums: IntArray): Int {
    // Coordinate compress both nums and 2*nums together
    val coords = (nums.map { it.toLong() } + nums.map { it.toLong() * 2 })
        .distinct().sorted()
    val rank = coords.withIndex().associate { (i, v) -> v to (i + 1) }
    val m = coords.size

    val bit = FenwickTree(m)
    var count = 0

    for (x in nums.reversed()) {
        // Count already-seen y where y < x/2, i.e. 2y < x, i.e. 2y ≤ x-1
        val threshold = rank[x.toLong() * 2]!! - 1
        if (threshold > 0) count += bit.query(threshold).toInt()
        // Insert x
        bit.update(rank[x.toLong()]!!, 1L)
    }
    return count
}
```

---

### Fenwick: 2D Variant

For problems on a grid (sum of rectangle `[r1,c1]..[r2,c2]`):

```kotlin
class FenwickTree2D(private val rows: Int, private val cols: Int) {

    private val tree = Array(rows + 1) { LongArray(cols + 1) }

    fun update(r: Int, c: Int, delta: Long) {
        var x = r
        while (x <= rows) {
            var y = c
            while (y <= cols) {
                tree[x][y] += delta
                y += y and (-y)
            }
            x += x and (-x)
        }
    }

    fun query(r: Int, c: Int): Long {
        var sum = 0L
        var x = r
        while (x > 0) {
            var y = c
            while (y > 0) {
                sum += tree[x][y]
                y -= y and (-y)
            }
            x -= x and (-x)
        }
        return sum
    }

    /** Rectangle sum [r1,c1]..[r2,c2] using inclusion-exclusion. */
    fun query(r1: Int, c1: Int, r2: Int, c2: Int): Long =
        query(r2, c2) - query(r1 - 1, c2) - query(r2, c1 - 1) + query(r1 - 1, c1 - 1)
}
```

---

## Part 2 — Segment Tree

### Intuition

A Fenwick tree is compact and fast, but it only supports operations that can be
expressed as a **prefix query** (sum, XOR, …). A segment tree is more general:
it can support **arbitrary range queries** and **range updates** (with lazy propagation).

Think of the segment tree as a complete binary tree where:
- Each **leaf** stores one array element.
- Each **internal node** stores the aggregate (sum, min, max, GCD, …) of its range.
- A node covering `[l, r]` has children covering `[l, mid]` and `[mid+1, r]`.

```
Array:    [3, 1, 4, 1, 5, 9, 2, 6]
           0  1  2  3  4  5  6  7

             [0,7] = 31
           /              \
       [0,3]=9          [4,7]=22
      /       \         /       \
   [0,1]=4 [2,3]=5  [4,5]=14 [6,7]=8
   / \      / \      / \      / \
  3   1    4   1    5   9    2   6
```

**Storage**: a 1-indexed array of size `4 * n` is enough for any n.
Node `k` has children `2k` and `2k+1`; parent is `k/2`.

---

### Implementation — Point Update, Range Query (Sum)

```kotlin
/**
 * Segment tree with point updates and range sum queries.
 * 0-indexed externally, 1-indexed internally.
 *
 * @param n  number of elements
 */
class SegmentTree(private val n: Int) {

    // 4*n is a safe upper bound for any n.
    // Exact minimum is 2 * nextPowerOfTwo(n), but 4n is simpler.
    private val tree = LongArray(4 * n)

    /**
     * Build from an existing array in O(n).
     * Each node is filled bottom-up: leaves first, then parents pull from children.
     */
    fun build(arr: IntArray, node: Int = 1, l: Int = 0, r: Int = n - 1) {
        if (l == r) {
            tree[node] = arr[l].toLong()
            return
        }
        val mid = (l + r) / 2
        build(arr, 2 * node, l, mid)           // recurse left child
        build(arr, 2 * node + 1, mid + 1, r)  // recurse right child
        tree[node] = tree[2 * node] + tree[2 * node + 1]  // pull up
    }

    /**
     * Updates position [i] to value [v] (0-indexed).
     *
     * We walk from root to the target leaf.  At each node we descend into
     * whichever child's range contains i, then on the way back up we
     * re-aggregate the current node from its two children.
     *
     * Time: O(log n)
     */
    fun update(i: Int, v: Long, node: Int = 1, l: Int = 0, r: Int = n - 1) {
        if (l == r) {
            tree[node] = v    // leaf: set new value
            return
        }
        val mid = (l + r) / 2
        if (i <= mid) update(i, v, 2 * node, l, mid)
        else          update(i, v, 2 * node + 1, mid + 1, r)
        tree[node] = tree[2 * node] + tree[2 * node + 1]  // re-aggregate
    }

    /**
     * Returns the sum of [ql..qr] (0-indexed, inclusive).
     *
     * Three cases at each node covering [l,r]:
     *   1. Query range completely covers node range → return tree[node] directly.
     *   2. Query range has no overlap            → return 0 (identity for sum).
     *   3. Partial overlap                        → recurse into both children.
     *
     * Time: O(log n) — at most 4 nodes per level are "partial".
     */
    fun query(ql: Int, qr: Int, node: Int = 1, l: Int = 0, r: Int = n - 1): Long {
        if (qr < l || r < ql) return 0L          // case 2: no overlap
        if (ql <= l && r <= qr) return tree[node] // case 1: full overlap
        val mid = (l + r) / 2                     // case 3: partial
        return query(ql, qr, 2 * node, l, mid) +
               query(ql, qr, 2 * node + 1, mid + 1, r)
    }
}
```

---

### Implementation — Range Update, Range Query (Lazy Propagation)

When you want to **add a value to every element in a range**, doing `n` point
updates costs O(n log n).  **Lazy propagation** defers the work: mark a node
"pending" and only push the pending value to children when you need to go deeper.

```kotlin
/**
 * Segment tree with range add updates and range sum queries.
 * Uses lazy propagation to achieve O(log n) for both operations.
 */
class LazySegmentTree(private val n: Int) {

    private val tree = LongArray(4 * n)  // stores actual sums
    private val lazy = LongArray(4 * n)  // stores pending additions

    fun build(arr: IntArray, node: Int = 1, l: Int = 0, r: Int = n - 1) {
        if (l == r) { tree[node] = arr[l].toLong(); return }
        val mid = (l + r) / 2
        build(arr, 2 * node, l, mid)
        build(arr, 2 * node + 1, mid + 1, r)
        tree[node] = tree[2 * node] + tree[2 * node + 1]
    }

    /**
     * Push the lazy value at [node] down to its children.
     *
     * A lazy[node] of X means "every element in this node's range should
     * receive +X, but we haven't done it yet."
     *
     * Pushing down:
     *   - Each child's sum increases by X * (size of child's range).
     *   - Each child's lazy accumulates X (will push further when needed).
     *   - Parent's lazy resets to 0 (done).
     */
    private fun pushDown(node: Int, l: Int, r: Int) {
        if (lazy[node] == 0L) return
        val mid = (l + r) / 2
        val leftSize  = (mid - l + 1).toLong()
        val rightSize = (r - mid).toLong()

        tree[2 * node]     += lazy[node] * leftSize
        lazy[2 * node]     += lazy[node]

        tree[2 * node + 1] += lazy[node] * rightSize
        lazy[2 * node + 1] += lazy[node]

        lazy[node] = 0L
    }

    /**
     * Adds [delta] to every element in [ul..ur] (0-indexed).
     *
     * Full overlap: update tree[node] directly (sum += delta * range_length)
     *               and record delta in lazy[node] for future push-downs.
     * Partial overlap: push down any pending lazy first, recurse, re-aggregate.
     */
    fun update(ul: Int, ur: Int, delta: Long, node: Int = 1, l: Int = 0, r: Int = n - 1) {
        if (ur < l || r < ul) return           // no overlap
        if (ul <= l && r <= ur) {              // full overlap
            tree[node] += delta * (r - l + 1)
            lazy[node] += delta
            return
        }
        pushDown(node, l, r)                   // partial: push before descending
        val mid = (l + r) / 2
        update(ul, ur, delta, 2 * node, l, mid)
        update(ul, ur, delta, 2 * node + 1, mid + 1, r)
        tree[node] = tree[2 * node] + tree[2 * node + 1]
    }

    /**
     * Range sum query [ql..qr] (0-indexed).
     * Must push lazy down before descending into children.
     */
    fun query(ql: Int, qr: Int, node: Int = 1, l: Int = 0, r: Int = n - 1): Long {
        if (qr < l || r < ql) return 0L
        if (ql <= l && r <= qr) return tree[node]
        pushDown(node, l, r)
        val mid = (l + r) / 2
        return query(ql, qr, 2 * node, l, mid) +
               query(ql, qr, 2 * node + 1, mid + 1, r)
    }
}
```

---

### Generic Segment Tree (Any Monoid)

To reuse the same tree for min, max, GCD, XOR, etc., parameterize the merge
operation and identity element.

```kotlin
/**
 * Generic segment tree parameterized over a monoid (T, combine, identity).
 *
 * A monoid is any type T with:
 *   - an associative binary operation `combine(a, b)`
 *   - an identity element such that `combine(identity, x) == x`
 *
 * Examples:
 *   Sum:   combine = { a, b -> a + b },  identity = 0L
 *   Min:   combine = ::minOf,             identity = Long.MAX_VALUE
 *   Max:   combine = ::maxOf,             identity = Long.MIN_VALUE
 *   GCD:   combine = ::gcd,               identity = 0L
 *   XOR:   combine = { a, b -> a xor b }, identity = 0L
 */
class GenericSegTree<T>(
    private val n: Int,
    private val identity: T,
    private val combine: (T, T) -> T
) {
    @Suppress("UNCHECKED_CAST")
    private val tree = arrayOfNulls<Any>(4 * n) as Array<T?>

    init { tree.fill(identity) }

    fun build(arr: Array<T>, node: Int = 1, l: Int = 0, r: Int = n - 1) {
        if (l == r) { tree[node] = arr[l]; return }
        val mid = (l + r) / 2
        build(arr, 2 * node, l, mid)
        build(arr, 2 * node + 1, mid + 1, r)
        tree[node] = combine(tree[2 * node]!!, tree[2 * node + 1]!!)
    }

    fun update(i: Int, v: T, node: Int = 1, l: Int = 0, r: Int = n - 1) {
        if (l == r) { tree[node] = v; return }
        val mid = (l + r) / 2
        if (i <= mid) update(i, v, 2 * node, l, mid)
        else          update(i, v, 2 * node + 1, mid + 1, r)
        tree[node] = combine(tree[2 * node]!!, tree[2 * node + 1]!!)
    }

    fun query(ql: Int, qr: Int, node: Int = 1, l: Int = 0, r: Int = n - 1): T {
        if (qr < l || r < ql) return identity
        if (ql <= l && r <= qr) return tree[node]!!
        val mid = (l + r) / 2
        return combine(
            query(ql, qr, 2 * node, l, mid),
            query(ql, qr, 2 * node + 1, mid + 1, r)
        )
    }
}

// Usage examples:
// val sumTree = GenericSegTree(n, 0L) { a, b -> a + b }
// val minTree = GenericSegTree(n, Long.MAX_VALUE, ::minOf)
// val maxTree = GenericSegTree(n, Long.MIN_VALUE, ::maxOf)
// val xorTree = GenericSegTree(n, 0L) { a, b -> a xor b }
```

---

### Segment Tree: LeetCode Problems

#### LC 307 — Range Sum Query (Mutable)

Same problem as before; segment tree version:

```kotlin
class NumArray(nums: IntArray) {
    private val st = SegmentTree(nums.size)
    init { st.build(nums) }
    fun update(index: Int, value: Int) = st.update(index, value.toLong())
    fun sumRange(left: Int, right: Int): Int = st.query(left, right).toInt()
}
```

**Fenwick vs Segment Tree here**: both work; Fenwick uses less memory (n vs 4n)
and has a smaller constant. Prefer Fenwick for pure prefix/range sum.

---

#### LC 2407 — Longest Increasing Subsequence II

**Problem**: find LIS where consecutive elements differ by at most `k`.  
**Why segment tree**: we need `max(dp[v])` for `v in [x-k, x-1]` per element,
updated after processing each element. That's a range-max query + point update.

```kotlin
fun lengthOfLIS(nums: IntArray, k: Int): Int {
    val maxVal = nums.max()!!
    // segment tree over value domain [1..maxVal]
    // stores max LIS length ending at each value
    val st = GenericSegTree(maxVal + 1, 0, ::maxOf)

    var ans = 0
    for (x in nums) {
        // best LIS length ending with a value in [x-k, x-1]
        val best = if (x - k > 0) st.query(x - k, x - 1) else 0
        val cur = best + 1
        ans = maxOf(ans, cur)
        st.update(x, cur)
    }
    return ans
}
```

---

#### LC 218 — The Skyline Problem

**Pattern**: coordinate-compress building edges; use a segment tree (or sorted
multiset) to maintain the current max height as you sweep left to right.

```kotlin
fun getSkyline(buildings: Array<IntArray>): List<List<Int>> {
    // Collect all x-coordinates; sort and deduplicate
    val xs = buildings.flatMap { listOf(it[0], it[1]) }.distinct().sorted()
    val idx = xs.withIndex().associate { (i, x) -> x to i }
    val n = xs.size

    val maxHeight = GenericSegTree(n, 0, ::maxOf)

    // Add each building: range update [l, r) with height h
    for ((l, r, h) in buildings) {
        val li = idx[l]!!
        val ri = idx[r]!! - 1      // half-open interval [l, r)
        if (li <= ri) maxHeight.update(li, h)  // simplified; real skyline needs lazy range-max
    }

    // Collect critical points
    val result = mutableListOf<List<Int>>()
    var prev = 0
    for ((i, x) in xs.withIndex()) {
        val h = maxHeight.query(i, i)
        if (h != prev) {
            result += listOf(x, h)
            prev = h
        }
    }
    return result
}
```

> **Note**: the full skyline solution requires a lazy range-max segment tree or
> a line-sweep with a max-heap. The above illustrates the segment tree pattern;
> the complete LC-accepted solution uses an event-based approach.

---

#### LC 699 — Falling Squares

**Problem**: squares drop onto a number line; after each drop, report the
maximum height anywhere on the line.

**Why segment tree with lazy propagation**: each square sets a range to
`max(current_height, square_height)` — a range-max update. Then query global max.

```kotlin
fun fallingSquares(positions: Array<IntArray>): List<Int> {
    // Coordinate compress
    val coords = positions.flatMap { (l, s) -> listOf(l, l + s) }.distinct().sorted()
    val rank = coords.withIndex().associate { (i, v) -> v to i }
    val n = coords.size

    // We need range-max update + global max query
    // Use a lazy tree that supports: set range to max(current, value)
    val tree = LongArray(4 * n)
    val lazy = LongArray(4 * n)

    fun pushDown(node: Int) {
        if (lazy[node] > 0) {
            tree[2 * node]     = maxOf(tree[2 * node], lazy[node])
            lazy[2 * node]     = maxOf(lazy[2 * node], lazy[node])
            tree[2 * node + 1] = maxOf(tree[2 * node + 1], lazy[node])
            lazy[2 * node + 1] = maxOf(lazy[2 * node + 1], lazy[node])
            lazy[node] = 0L
        }
    }

    fun update(ul: Int, ur: Int, v: Long, node: Int = 1, l: Int = 0, r: Int = n - 1) {
        if (ur < l || r < ul) return
        if (ul <= l && r <= ur) { tree[node] = maxOf(tree[node], v); lazy[node] = maxOf(lazy[node], v); return }
        pushDown(node)
        val mid = (l + r) / 2
        update(ul, ur, v, 2 * node, l, mid)
        update(ul, ur, v, 2 * node + 1, mid + 1, r)
        tree[node] = maxOf(tree[2 * node], tree[2 * node + 1])
    }

    fun query(ql: Int, qr: Int, node: Int = 1, l: Int = 0, r: Int = n - 1): Long {
        if (qr < l || r < ql) return 0L
        if (ql <= l && r <= qr) return tree[node]
        pushDown(node)
        val mid = (l + r) / 2
        return maxOf(query(ql, qr, 2 * node, l, mid), query(ql, qr, 2 * node + 1, mid + 1, r))
    }

    val result = mutableListOf<Int>()
    for ((left, size) in positions) {
        val l = rank[left]!!
        val r = rank[left + size]!! - 1
        val curMax = query(l, r)
        update(l, r, curMax + size)
        result += tree[1].toInt()
    }
    return result
}
```

---

## Part 3 — Decision Guide

```
Need range queries / updates?
│
├── Only prefix sums or prefix XOR?
│     └──► Fenwick Tree
│           • Simpler code (~15 lines)
│           • O(n) space
│           • O(log n) update + query
│
├── Range query, point update?
│     ├── Can express as prefix difference?
│     │     └──► Fenwick Tree
│     └── Arbitrary aggregate (min, max, GCD)?
│           └──► Segment Tree (no lazy)
│
└── Range update + range query?
      └──► Segment Tree with Lazy Propagation
            • Range add → lazy sum
            • Range set → lazy set (override, not accumulate)
            • Range max update → lazy max
```

---

## Part 4 — Complexity Summary

| Structure | Build | Point Update | Range Update | Range Query | Space |
|---|---|---|---|---|---|
| Prefix Sum Array | O(n) | O(n) | O(n) | O(1) | O(n) |
| Fenwick Tree | O(n log n) | O(log n) | N/A | O(log n) | O(n) |
| Segment Tree | O(n) | O(log n) | O(n) | O(log n) | O(4n) |
| Lazy Seg Tree | O(n) | O(log n) | O(log n) | O(log n) | O(8n) |

---

## Part 5 — Common Pitfalls

**1-indexing in Fenwick**  
The bit trick `i & (-i)` gives 0 when `i = 0`, causing an infinite loop.
Fenwick trees must be 1-indexed. Always `update(i + 1, ...)` for 0-indexed inputs.

**4n tree size**  
`4 * n` covers all n up to 10⁶ safely. For n = 1 the tree still needs indices 1–4.
Using `2 * n` will segfault for non-power-of-two n.

**Lazy identity vs zero**  
The identity for lazy addition is `0` (do nothing). But if your update operation
is "range set to v", the identity can't be 0 (0 is a valid value). Use a
sentinel like `Long.MIN_VALUE` or a nullable lazy array.

**Long overflow in sum trees**  
n = 10⁵ elements each up to 10⁹ → max sum = 10¹⁴ → must use `Long`, not `Int`.

**Merge operation must be associative**  
Segment trees rely on `combine(combine(a,b), c) == combine(a, combine(b,c))`.
Average, for example, is NOT associative — you can't build a segment tree for
range average directly (store sum + count instead, compute average at query time).

**Coordinate compression prerequisite**  
For problems where values reach 10⁹, build a Fenwick/segment tree over ranks
(compressed indices), not raw values. Map each value to its position in the
sorted unique list of all values that appear.
