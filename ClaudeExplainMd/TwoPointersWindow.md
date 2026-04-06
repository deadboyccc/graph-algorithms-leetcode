# TWO-POINTER & WINDOWING PATTERNS IN KOTLIN

This document covers:
1. Two-pointer zip pattern
2. Windowing and iteration
3. Max profit problem evolution
4. Why outside-in pairing fails for max profit
5. Correct single-pass solution

---

## PART 1: TWO-POINTER ZIP PATTERN

Basic two-pointer pattern using Kotlin ranges and zip.
Traverses a list from both ends inward, stopping at the middle.

```kotlin
fun <T> twoPointerZipExample(list: List<T>) {
    // Create ranges: left from 0 to middle, right from end to middle
    for ((left, right) in (0 until list.size / 2).zip((list.size - 1) downTo (list.size / 2))) {
        println("Left index: $left, Right index: $right")
        println("Left value: ${list[left]}, Right value: ${list[right]}")
    }
}
```

**Example usage:**
```kotlin
twoPointerZipExample(listOf(1, 2, 3, 4, 5, 6))
```

**Output:**
```
Left index: 0, Right index: 5
Left index: 1, Right index: 4
Left index: 2, Right index: 3
```

---

## PART 2: WINDOWING ITERATION

Windowing: Splits array into overlapping subarrays of fixed size.
`windowed(size, step)` creates windows of 'size' with 'step' offset.

```kotlin
fun windowingExample() {
    val prices = intArrayOf(7, 1, 5, 3, 6, 4)
    
    // windowed(prices.size - 1, 1):
    // - Window size: 5 (size - 1)
    // - Step: 1 (slide by 1)
    val windowed = prices.toList().windowed(prices.size - 1, 1)
    
    // Result:
    // [7, 1, 5, 3, 6]
    // [1, 5, 3, 6, 4]
    
    // Process each window with two pointers
    windowed.forEach { window ->
        for ((left, right) in (0 until window.size / 2).zip((window.size - 1) downTo (window.size / 2))) {
            println("Window left[${left}]=${window[left]}, right[${right}]=${window[right]}")
        }
    }
}
```

---

## PART 3: DATA CLASS FOR TRACKING

```kotlin
data class StockPrice(var price: Int, var index: Int)
```

**Usage in loops:**

```kotlin
fun dataClassExample() {
    val prices = intArrayOf(7, 1, 5, 3, 6, 4)
    val priceList = prices.toList()

    for ((left, right) in (0 until priceList.size / 2).zip((priceList.size - 1) downTo (priceList.size / 2))) {
        val leftPrice = StockPrice(priceList[left], left)
        val rightPrice = StockPrice(priceList[right], right)
        println("Left: $leftPrice | Right: $rightPrice")
    }
}
```

---

## PART 4: MAX PROFIT EVOLUTION - INCORRECT APPROACHES

### WRONG APPROACH 1: Outside-in pairing

**Problem:** Assumes pairing indices from outside-in works.
Fails because it doesn't enforce buy-before-sell constraint.

```kotlin
fun maxProfitWrong_OutsideIn(prices: IntArray): Int {
    if (prices.isEmpty()) return 0
    
    val priceList = prices.toList()
    var maxProfit = 0

    // Pairs: (0, n-1), (1, n-2), etc.
    for ((left, right) in (0 until priceList.size / 2).zip((priceList.size - 1) downTo (priceList.size / 2))) {
        val profit = priceList[right] - priceList[left]
        maxProfit = maxOf(maxProfit, profit)
    }

    return maxProfit
}
```

**Test case `[1, 4, 2]`:**
- Pairs: (0, 2) → prices[2] - prices[0] = 2 - 1 = 1
- Returns 1, but correct answer is 3 (buy at 1, sell at 4)

### WRONG APPROACH 2: Tracking min/max with two-pointer from outside

**Problem:** Moving pointers inward doesn't explore all valid buy-sell pairs
where buy happens before sell.

```kotlin
fun maxProfitWrong_TwoPointerOutside(prices: IntArray): Int {
    if (prices.isEmpty()) return 0
    
    var left = 0
    var right = prices.size - 1
    var maxProfit = 0

    while (left < right) {
        val profit = prices[right] - prices[left]
        maxProfit = maxOf(maxProfit, profit)
        // This heuristic doesn't work for all cases
        if (prices[left] > prices[right]) left++ else right--
    }

    return maxProfit
}
```

**Test case `[1, 4, 2]`:**
- Iteration 1: left=0 (price 1), right=2 (price 2), profit=1, move right--
- Iteration 2: left=0, right=1, stop (left < right fails)
- Returns 1, but correct answer is 3

---

## PART 5: CORRECT SOLUTION - SINGLE PASS, LEFT TO RIGHT

### CORRECT APPROACH: Single-pass with running minimum

**Key insight:**
- Iterate from left to right (forward only)
- Track the minimum price seen so far
- At each position, calculate profit = current - minimum
- This guarantees: buy at min, sell at current (current >= min index)

**Time:** O(n), **Space:** O(1)

```kotlin
fun maxProfitCorrect(prices: IntArray): Int {
    if (prices.isEmpty()) return 0
    
    var minPrice = prices[0]      // Minimum price seen so far
    var maxProfit = 0             // Best profit found so far

    for (i in 1 until prices.size) {
        val profit = prices[i] - minPrice
        maxProfit = maxOf(maxProfit, profit)
        minPrice = minOf(minPrice, prices[i])
    }

    return maxProfit
}
```

**Walkthrough for `[1, 4, 2]`:**
```
i=1: price=4, minPrice=1, profit=4-1=3, maxProfit=3
i=2: price=2, minPrice=1, profit=2-1=1, maxProfit=3
Returns 3 ✓
```

**Walkthrough for `[7, 1, 5, 3, 6, 4]`:**
```
i=1: price=1, minPrice=1, profit=0, maxProfit=0
i=2: price=5, minPrice=1, profit=4, maxProfit=4
i=3: price=3, minPrice=1, profit=2, maxProfit=4
i=4: price=6, minPrice=1, profit=5, maxProfit=5
i=5: price=4, minPrice=1, profit=3, maxProfit=5
Returns 5 ✓
```

---

## PART 6: WHY OUTSIDE-IN TWO-POINTER FAILS

### Problem Analysis:

Outside-in two-pointer assumes:
- Optimal profit is between extreme indices (start-end pairing)
- But max profit requires: buy BEFORE sell (temporal ordering)

**Example: `[1, 4, 2]`**
- Outside-in pairs: (index 0, index 2) = (1, 2) = profit 1
- But optimal: (index 0, index 1) = (1, 4) = profit 3
- Index 1 is skipped because it's internal, not on the boundaries

**The constraint is NOT spatial** (which element is further away)
**The constraint is TEMPORAL** (which element comes first in time)

---

## PART 7: COMPLETE SOLUTION CLASS

```kotlin
class StockMaxProfitSolution {
    data class StockPrice(var price: Int, var index: Int)

    /**
     * Find maximum profit from buying and selling a stock.
     * Constraint: Can only sell after buying.
     * 
     * Algorithm: Single-pass with running minimum
     * - Iterate prices left to right
     * - Track minimum price seen
     * - Calculate profit at each step
     * - Return maximum profit found
     */
    fun maxProfit(prices: IntArray): Int {
        if (prices.isEmpty()) return 0
        
        var minPrice = prices[0]
        var maxProfit = 0

        for (i in 1 until prices.size) {
            val profit = prices[i] - minPrice
            maxProfit = maxOf(maxProfit, profit)
            minPrice = minOf(minPrice, prices[i])
        }

        return maxProfit
    }
}
```

---

## PART 8: TEST CASES

```kotlin
fun main() {
    val solution = StockMaxProfitSolution()
    
    println("=== Test Case 1 ===")
    val prices1 = intArrayOf(7, 1, 5, 3, 6, 4)
    println("Input: ${prices1.toList()}")
    println("Output: ${solution.maxProfit(prices1)}")
    println("Expected: 5 (buy at 1, sell at 6)")
    
    println("\n=== Test Case 2 ===")
    val prices2 = intArrayOf(1, 4, 2)
    println("Input: ${prices2.toList()}")
    println("Output: ${solution.maxProfit(prices2)}")
    println("Expected: 3 (buy at 1, sell at 4)")
    
    println("\n=== Test Case 3 ===")
    val prices3 = intArrayOf(7, 6, 4, 3, 1)
    println("Input: ${prices3.toList()}")
    println("Output: ${solution.maxProfit(prices3)}")
    println("Expected: 0 (prices only decrease)")
    
    println("\n=== Test Case 4 ===")
    val prices4 = intArrayOf(2, 4, 1)
    println("Input: ${prices4.toList()}")
    println("Output: ${solution.maxProfit(prices4)}")
    println("Expected: 2 (buy at 2, sell at 4)")
}
```

---

## SUMMARY

### KEY TAKEAWAYS:

#### 1. TWO-POINTER ZIP:
```kotlin
for ((left, right) in (0 until n/2).zip((n-1) downTo (n/2)))
```
- Good for symmetric/mirrored processing
- Not suitable for buy-sell problems

#### 2. WINDOWING:
```kotlin
list.windowed(size, step).forEach { window -> ... }
```
- Good for sliding window analysis
- Adds complexity if not needed

#### 3. MAX PROFIT REQUIRES:
- Temporal ordering (buy before sell)
- Single-pass left-to-right iteration
- Track running minimum
- Calculate profit at each step

#### 4. TIME COMPLEXITY:
- Correct: O(n) with O(1) space
- Incorrect approaches: Still O(n) but semantically wrong

#### 5. CONSTRAINT ANALYSIS:
Always identify constraints (temporal vs spatial, ordering vs distance)
Choose algorithm accordingly
