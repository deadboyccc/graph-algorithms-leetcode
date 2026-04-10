
# Kotlin Algorithms & Functional Programming Reference
## Cheapest Flights Within K Stops + Kotlin FP Cheat Sheet

---

# Part 1 — Problem Reference

## Cheapest Flights Within K Stops

Given:

- `n` cities
- `flights[i] = [from, to, price]`
- `src`
- `dst`
- `k` maximum stops

Return the **cheapest price from `src` to `dst` with ≤ k stops**.

If unreachable, return `-1`.

Important relationship:

```
k stops = k + 1 flights (edges)
```

---

# Algorithms Covered

1. Bellman-Ford (Iterative DP)
2. Idiomatic Kotlin Bellman-Ford
3. Functional Kotlin (`takeIf` + `let`)
4. Functional `fold`
5. Lazy pipelines with `generateSequence`
6. `runningFold` state evolution
7. `buildList` builder pattern
8. Priority Queue (Modified Dijkstra)

---

# 1. Bellman-Ford Style Solution

Idea: Relax edges **k+1 times**.

```kotlin
class Solution {
    fun findCheapestPrice(
        n: Int,
        flights: Array<IntArray>,
        src: Int,
        dst: Int,
        k: Int
    ): Int {

        var prices = IntArray(n) { Int.MAX_VALUE }
        prices[src] = 0

        repeat(k + 1) {
            val temp = prices.copyOf()

            flights.forEach { (from, to, price) ->
                if (prices[from] != Int.MAX_VALUE) {
                    temp[to] = minOf(temp[to], prices[from] + price)
                }
            }

            prices = temp
        }

        return if (prices[dst] == Int.MAX_VALUE) -1 else prices[dst]
    }
}
```

### Complexity

```
Time: O(kE)
Space: O(V)
```

### Pros

- Simple
- Deterministic
- Easy to reason about

### Cons

- Always scans all edges

---

# 2. Idiomatic Kotlin Version

```kotlin
class Solution {

    fun findCheapestPrice(
        numberOfCities: Int,
        flights: Array<IntArray>,
        sourceCity: Int,
        destinationCity: Int,
        maxStops: Int
    ): Int {

        val unreachableCost = Int.MAX_VALUE

        var minimumCostToCity =
            IntArray(numberOfCities) { unreachableCost }
                .apply { this[sourceCity] = 0 }

        repeat(maxStops + 1) {

            val updatedCostToCity = minimumCostToCity.copyOf()

            for ((departureCity, arrivalCity, flightPrice) in flights) {

                val costToDeparture = minimumCostToCity[departureCity]
                if (costToDeparture == unreachableCost) continue

                val newCost = costToDeparture + flightPrice

                updatedCostToCity[arrivalCity] =
                    minOf(updatedCostToCity[arrivalCity], newCost)
            }

            minimumCostToCity = updatedCostToCity
        }

        val cheapestPrice = minimumCostToCity[destinationCity]

        return if (cheapestPrice == unreachableCost) -1 else cheapestPrice
    }
}
```

---

# 3. Functional Kotlin Style

```kotlin
flights.forEach { (departureCity, arrivalCity, flightPrice) ->

    minimumCostToCity[departureCity]
        .takeIf { it != unreachableCost }
        ?.let { costToDeparture ->

            val newCost = costToDeparture + flightPrice

            updatedCostToCity[arrivalCity] =
                minOf(updatedCostToCity[arrivalCity], newCost)
        }
}
```

Functional pipeline:

```
value
 ↓
takeIf(condition)
 ↓
let(block)
```

---

# 4. Functional Fold Version

```kotlin
val finalCosts =
    (0..maxStops).fold(initialCosts) { currentCosts, _ ->

        val updatedCosts = currentCosts.copyOf()

        flights.forEach { (from, to, price) ->
            currentCosts[from]
                .takeIf { it != unreachableCost }
                ?.let { updatedCosts[to] = minOf(updatedCosts[to], it + price) }
        }

        updatedCosts
    }
```

Concept:

```
state(n+1) = transform(state(n))
```

---

# 5. Lazy Pipeline (generateSequence)

```kotlin
val finalCosts =
    generateSequence(initialCosts) { previousCosts ->

        val updatedCosts = previousCosts.copyOf()

        flights.forEach { (from, to, price) ->
            previousCosts[from]
                .takeIf { it != unreachableCost }
                ?.let { updatedCosts[to] = minOf(updatedCosts[to], it + price) }
        }

        updatedCosts
    }
    .drop(maxStops + 1)
    .first()
```

Pipeline model:

```
initial
 ↓
state1
 ↓
state2
 ↓
state3
```

---

# 6. runningFold Version

```kotlin
val finalCosts =
    (0..maxStops)
        .asSequence()
        .runningFold(initialCosts) { previousCosts, _ ->

            val updatedCosts = previousCosts.copyOf()

            flights.forEach { (from, to, price) ->
                previousCosts[from]
                    .takeIf { it != unreachableCost }
                    ?.let { updatedCosts[to] = minOf(updatedCosts[to], it + price) }
            }

            updatedCosts
        }
        .last()
```

Running states:

```
state0
state1
state2
state3
```

---

# 7. buildList Pattern

```kotlin
val states = buildList {

    var currentCosts = initialCosts

    repeat(maxStops + 1) {

        val nextCosts = currentCosts.copyOf()

        for ((from, to, price) in flights) {

            val departureCost = currentCosts[from]
            if (departureCost == unreachable) continue

            nextCosts[to] =
                minOf(nextCosts[to], departureCost + price)
        }

        add(nextCosts)
        currentCosts = nextCosts
    }
}
```

---

# 8. Priority Queue Solution (Modified Dijkstra)

```kotlin
data class FlightState(
    val city: Int,
    val totalCost: Int,
    val stopsUsed: Int
)
```

```kotlin
val minHeap = PriorityQueue(compareBy<FlightState> { it.totalCost })

minHeap.add(FlightState(sourceCity, 0, 0))

while (minHeap.isNotEmpty()) {

    val (city, cost, stops) = minHeap.poll()

    if (city == destinationCity)
        return cost

    if (stops > maxStops) continue

    for ((nextCity, price) in adjacencyList[city]) {

        minHeap.add(
            FlightState(
                nextCity,
                cost + price,
                stops + 1
            )
        )
    }
}
```

Complexity:

```
O(E log V)
```

---

# Part 2 — Kotlin Functional Programming Reference

This section explains the most important functional operators used in Kotlin collections.

---

# map

Transforms each element.

```kotlin
val nums = listOf(1,2,3)
val doubled = nums.map { it * 2 }
```

Result:

```
[2,4,6]
```

Diagram:

```
1 → 2
2 → 4
3 → 6
```

---

# flatMap

Transforms and flattens nested collections.

```kotlin
val words = listOf("hi","ok")

val chars = words.flatMap { it.toList() }
```

Result:

```
[h,i,o,k]
```

Diagram:

```
["hi","ok"]
   ↓
[['h','i'],['o','k']]
   ↓
['h','i','o','k']
```

---

# fold

Accumulates values with an initial value.

```kotlin
val sum = listOf(1,2,3).fold(0) { acc, x -> acc + x }
```

Steps:

```
0 + 1 = 1
1 + 2 = 3
3 + 3 = 6
```

---

# reduce

Like fold but uses the first element as the accumulator.

```kotlin
val sum = listOf(1,2,3).reduce { acc, x -> acc + x }
```

Steps:

```
1 + 2 = 3
3 + 3 = 6
```

---

# runningFold (scan)

Produces intermediate accumulation states.

```kotlin
listOf(1,2,3).runningFold(0) { acc, x -> acc + x }
```

Result:

```
[0,1,3,6]
```

Visualization:

```
0
 ↓
1
 ↓
3
 ↓
6
```

---

# generateSequence

Creates lazy sequences.

```kotlin
generateSequence(1) { it * 2 }.take(5).toList()
```

Result:

```
[1,2,4,8,16]
```

---

# Sequences vs Collections

Collections:

```
eager evaluation
```

Sequences:

```
lazy evaluation
pipeline processing
```

Example:

```kotlin
numbers
    .asSequence()
    .map { it * 2 }
    .filter { it > 10 }
    .toList()
```

Pipeline:

```
data
 ↓
map
 ↓
filter
 ↓
result
```

---

# Kotlin FP Operator Summary

| Operator | Purpose |
|--------|--------|
| map | transform elements |
| flatMap | transform + flatten |
| fold | accumulate with initial value |
| reduce | accumulate without initial value |
| runningFold | produce intermediate states |
| generateSequence | create lazy streams |
| asSequence | convert collection to lazy pipeline |

---

# Recommended Usage

For clarity:

```
Prefer simple loops
```

For expressive Kotlin:

```
map
fold
runningFold
```

For lazy pipelines:

```
Sequence API
```

---

# Final Insight

Many algorithms can be expressed as:

```
state(n+1) = transform(state(n))
```

This idea connects:

- dynamic programming
- functional programming
- streaming pipelines

Understanding this pattern makes complex algorithms easier to reason about.