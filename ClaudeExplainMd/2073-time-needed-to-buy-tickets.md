# LeetCode 2073 — Time Needed to Buy Tickets

> Complete solution reference: simulation, formula, pitfalls, and deep analysis

---

## Problem Statement

There are `n` people in a line queuing to buy tickets. The `0th` person is at the front, `(n-1)th` at the back.

- Each person takes **1 second** to buy one ticket.
- A person can only buy **1 ticket at a time**, then rejoins the **back** of the line.
- A person with no tickets left **leaves** the line.

**Return** the time taken for the person at position `k` (0-indexed) to finish buying all their tickets.

### Constraints

- `n == tickets.length`
- `1 <= n <= 100`
- `1 <= tickets[i] <= 100`
- `0 <= k < n`

---

## Examples

### Example 1

```
Input:  tickets = [2, 3, 2], k = 2
Output: 6
```

```
t=0: [2, 3, 2]   <- k=2 is underlined
t=1: [3, 2, 1]
t=2: [2, 1, 2]
t=3: [1, 2, 1]
t=4: [2, 1]      <- person 0 left (ran out)
t=5: [1, 1]
t=6: [1]         <- person k=2 bought last ticket, return 6
```

### Example 2

```
Input:  tickets = [5, 1, 1, 1], k = 0
Output: 8
```

```
t=0: [5, 1, 1, 1]
t=1: [1, 1, 1, 4]
t=2: [1, 1, 4]   <- persons 1, 2, 3 leave after 1 ticket each
...
t=8: []          <- person k=0 bought all 5 tickets, return 8
```

---

## Solution 1 — Simulation (Queue)

### Approach

Directly model the queue. Each iteration: dequeue the front person, decrement their count. If they still need tickets, re-enqueue at the back. Stop the moment person `k` buys their last ticket.

### Code

```kotlin
class Solution {
    fun timeRequiredToBuy(tickets: IntArray, k: Int): Int {
        // Queue stores (ticket_count, original_index) pairs.
        // We track original index to identify when person k finishes.
        val queue = ArrayDeque<Pair<Int, Int>>()

        // 1. Initialize queue with all people and their ticket counts
        tickets.forEachIndexed { index, count -> queue.add(count to index) }

        var seconds = 0

        // 2. Simulate the buying process
        while (queue.isNotEmpty()) {
            seconds++
            val (count, index) = queue.removeFirst()  // see pitfall below
            val remaining = count - 1

            // 3. If person k just bought their last ticket, we're done
            if (remaining == 0 && index == k) return seconds

            // 4. If person still needs tickets, rejoin the back of the queue
            if (remaining > 0) {
                queue.add(remaining to index)
            }
        }

        return seconds
    }
}
```

### Complexity

| Metric    | Value                                                   |
|-----------|---------------------------------------------------------|
| **Time**  | O(n x max_tickets) — each ticket buy is one iteration  |
| **Space** | O(n) — queue holds up to n people                      |

---

## Solution 2 — O(n) Formula (Optimal)

### Core Insight

Person `k` needs exactly `target = tickets[k]` rounds to finish. The queue is circular — everyone cycles through in order, repeatedly. This structure is **predictable**: you can calculate each person's contribution without simulating each second.

**Key observation about the final round:**

- Rounds `1` through `target - 1`: everyone gets a turn.
- Round `target` (the last round): the queue stops **the moment person `k` buys**. People **after** `k` in the original order never get their turn in this final round.

### The Formula

```kotlin
if (i <= k) minOf(t, target)      // person before k, or k itself
else        minOf(t, target - 1)  // person after k
```

#### People at `i <= k` (before or at position k)

- They are ahead of `k` in line.
- In every round — including the final round — they buy **before** `k`.
- They get a chance in all `target` rounds.
- If `t < target`, they exhaust their tickets early after `t` rounds.
- **Contribution:** `min(t, target)`

#### People at `i > k` (after position k)

- They are behind `k` in line.
- In the final round, `k` buys first and we stop — they **never get their turn**.
- They participate in at most `target - 1` rounds.
- If `t < target - 1`, they exhaust their tickets early.
- **Contribution:** `min(t, target - 1)`

### Code

```kotlin
class Solution {
    fun timeRequiredToBuy(tickets: IntArray, k: Int): Int {
        val target = tickets[k]
        return tickets.withIndex().sumOf { (i, t) ->
            if (i <= k) minOf(t, target) else minOf(t, target - 1)
        }
    }
}
```

### Complexity

| Metric    | Value                               |
|-----------|-------------------------------------|
| **Time**  | O(n) — single pass over the array   |
| **Space** | O(1) — no auxiliary data structures |

---

## Deep Trace — Formula

### Example 1: `tickets = [2, 3, 2]`, `k = 2`, `target = 2`

| i | t | i <= k? | Formula   | Result |
|---|---|---------|-----------|--------|
| 0 | 2 | yes     | min(2, 2) | 2      |
| 1 | 3 | yes     | min(3, 2) | 2      |
| 2 | 2 | yes (k) | min(2, 2) | 2      |

**Sum = 6**

No one comes after `k = 2` (last index), so the `target - 1` branch never fires.

### Example 2: `tickets = [5, 1, 1, 1]`, `k = 0`, `target = 5`

| i | t | i <= k? | Formula   | Result |
|---|---|---------|-----------|--------|
| 0 | 5 | yes (k) | min(5, 5) | 5      |
| 1 | 1 | no      | min(1, 4) | 1      |
| 2 | 1 | no      | min(1, 4) | 1      |
| 3 | 1 | no      | min(1, 4) | 1      |

**Sum = 8**

People 1-3 each only want 1 ticket, so `min(1, 4) = 1`. They leave early and only contribute 1 second each.

---

## Kotlin Pitfalls

### Pitfall 1 — `ArrayDeque` API

Kotlin's stdlib `ArrayDeque` does **not** have `.poll()`. That is a `java.util.ArrayDeque` method.

| Type                            | Dequeue Method   |
|---------------------------------|------------------|
| `kotlin.collections.ArrayDeque` | `.removeFirst()` |
| `java.util.ArrayDeque`          | `.poll()`        |

Using `.poll()` on the Kotlin stdlib version causes a compile error. Always use `.removeFirst()`.

### Pitfall 2 — Destructuring `IntArray`

Kotlin does **not** support direct destructuring of `IntArray` in a for loop.

```kotlin
// Compile error — component1() is ambiguous for IntArray
for ((i, v) in tickets) { ... }

// Correct — use .withIndex()
for ((i, v) in tickets.withIndex()) { ... }
```

The ambiguity arises because `IntArray` has multiple competing `componentN()` extension functions from different array types.

---

## Solution Comparison

| Metric            | Simulation                          | Formula                            |
|-------------------|-------------------------------------|------------------------------------|
| **Time**          | O(n x max_tickets)                  | O(n)                               |
| **Space**         | O(n)                                | O(1)                               |
| **Readability**   | High — mirrors the problem directly | Requires insight to understand     |
| **Interview use** | Good starting point                 | Demonstrates mathematical thinking |

---

## Key Takeaways

1. **Simulation is always safe** — model the problem literally when the constraint space is small enough. Here `n, tickets[i] <= 100` so the worst case is 10,000 iterations — perfectly fine.

2. **The formula requires a key observation** — the circular queue makes each person's contribution independent and calculable. The split at `i <= k` vs `i > k` captures who does or does not get a turn in the final round.

3. **Kotlin array API matters** — `IntArray` requires `.withIndex()` for destructuring, and `kotlin.collections.ArrayDeque` uses `.removeFirst()` not `.poll()`.

4. **Both solutions are correct** — choose simulation for clarity in interviews, formula when asked to optimize.
