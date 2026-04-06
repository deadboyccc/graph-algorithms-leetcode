# CONSTRAINT ANALYSIS: TEMPORAL VS SPATIAL, ORDERING VS DISTANCE

---

## INTRODUCTION

The algorithm you choose depends entirely on understanding the **constraints** of your problem.

Most problems fall into two categories:
1. **TEMPORAL CONSTRAINTS** - Time-based ordering matters (which event happens first)
2. **SPATIAL CONSTRAINTS** - Position-based distance matters (which element is further away)

Confusing these leads to wrong algorithms and failed test cases.

---

## PART 1: TEMPORAL CONSTRAINTS

### Definition
**Temporal constraints** enforce an ordering based on sequence or time.
The **position in the sequence** determines validity.

### Key Characteristics
- "Before" and "after" matter
- Earlier indices enable later indices
- Causality: A must happen before B
- Sequential dependency

### Common Patterns
- Buy before sell
- Parent before child (in trees)
- Previous before next (in DP)
- Start before end (in intervals)

### Example 1: Maximum Stock Profit (TEMPORAL)

**Problem Statement:**
```
You are given an array of stock prices.
You can buy and sell exactly once.
You must BUY before you SELL.
Find the maximum profit.

prices = [7, 1, 5, 3, 6, 4]
Output: 5 (buy at 1, sell at 6)
```

**Why this is TEMPORAL:**
- You cannot sell on day 2 if you buy on day 4
- The constraint is: `buyDay < sellDay` (ordering)
- Not about distance between indices

**WRONG APPROACH (Spatial thinking):**
```kotlin
// Outside-in pairing: pairs extreme indices
// (0, 5), (1, 4), (2, 3)
for ((left, right) in (0 until size/2).zip((size-1) downTo (size/2))) {
    val profit = prices[right] - prices[left]
    maxProfit = maxOf(maxProfit, profit)
}

// For [7, 1, 5, 3, 6, 4]:
// Pairs: (0,5)=(7,4)=-3, (1,4)=(1,6)=5, (2,3)=(5,3)=-2
// Returns 5 (works here, but...)

// For [1, 4, 2]:
// Pairs: (0,2)=(1,2)=1
// Returns 1 (WRONG! Correct is 3: buy at 1, sell at 4)
// Index 1 is internal, skipped by outside-in approach
```

**CORRECT APPROACH (Temporal thinking):**
```kotlin
// Single-pass left-to-right: respects temporal ordering
// At each day, calculate profit from buying at minimum seen so far
fun maxProfit(prices: IntArray): Int {
    var minPrice = prices[0]    // Minimum seen SO FAR (temporal)
    var maxProfit = 0

    for (i in 1 until prices.size) {
        val profit = prices[i] - minPrice
        maxProfit = maxOf(maxProfit, profit)
        minPrice = minOf(minPrice, prices[i])
    }

    return maxProfit
}

// For [1, 4, 2]:
// i=1: profit=4-1=3, maxProfit=3
// i=2: profit=2-1=1, maxProfit=3
// Returns 3 ✓
```

**Why correct:**
- Enforces: minimum price seen **before** current price
- At each position, optimal buy is the minimum **up to that point**
- Guarantees: `buyIndex < sellIndex`

---

### Example 2: Buy and Sell Stock Multiple Times (TEMPORAL)

**Problem Statement:**
```
Buy and sell multiple times.
Still must BUY before SELL.
Find maximum profit.

prices = [3, 2, 6, 5, 0, 3]
Output: 7 (buy at 2, sell at 6, profit 4 + buy at 0, sell at 3, profit 3)
```

**WRONG APPROACH (Spatial thinking):**
```kotlin
// Trying to pair prices spatially
for ((left, right) in (0 until size/2).zip((size-1) downTo (size/2))) {
    // This doesn't allow multiple transactions
    // Doesn't respect the temporal sequence
}
```

**CORRECT APPROACH (Temporal thinking):**
```kotlin
fun maxProfit(prices: IntArray): Int {
    var maxProfit = 0

    for (i in 1 until prices.size) {
        // If we can profit, we take it (temporal opportunity)
        if (prices[i] > prices[i-1]) {
            maxProfit += prices[i] - prices[i-1]
        }
    }

    return maxProfit
}

// For [3, 2, 6, 5, 0, 3]:
// i=1: 2<3, skip
// i=2: 6>2, profit += 4, maxProfit=4
// i=3: 5<6, skip
// i=4: 0<5, skip
// i=5: 3>0, profit += 3, maxProfit=7
// Returns 7 ✓
```

**Why correct:**
- Respects temporal sequence: left-to-right only
- Captures every opportunity as it arises
- No spatial assumptions about where profits lie

---

### Example 3: Longest Increasing Subsequence (TEMPORAL)

**Problem Statement:**
```
Find the longest subsequence where elements are in increasing order.
Elements don't need to be consecutive.

nums = [10, 9, 2, 5, 3, 7, 101, 18]
Output: 4 (subsequence [2, 3, 7, 101])
```

**Why this is TEMPORAL:**
- An element can only extend a subsequence if it comes **after** previous element
- Position in array matters for validity

**CORRECT APPROACH (Temporal/DP):**
```kotlin
fun lengthOfLIS(nums: IntArray): Int {
    val dp = IntArray(nums.size) { 1 }  // dp[i] = LIS ending at i

    for (i in 1 until nums.size) {
        for (j in 0 until i) {  // Look at all elements BEFORE i
            if (nums[j] < nums[i]) {
                dp[i] = maxOf(dp[i], dp[j] + 1)
            }
        }
    }

    return dp.maxOrNull() ?: 0
}

// For [10, 9, 2, 5, 3, 7, 101, 18]:
// dp[0]=1 (10)
// dp[1]=1 (9, can't extend from 10)
// dp[2]=1 (2, can't extend)
// dp[3]=2 (5, extends from 2)
// dp[4]=2 (3, extends from 2)
// dp[5]=3 (7, extends from either 5 or 3)
// dp[6]=4 (101, extends from 7)
// dp[7]=3 (18, extends from 7)
// Returns 4 ✓
```

**Why correct:**
- Only looks at indices **before** current position
- Respects temporal/positional ordering
- Builds solution incrementally from past

---

## PART 2: SPATIAL CONSTRAINTS

### Definition
**Spatial constraints** enforce a relationship based on physical position or distance.
The **physical location** determines validity.

### Key Characteristics
- "Left" and "right" matter (geometry)
- Distance between elements matters
- Symmetry or mirroring patterns
- Container/value relationships

### Common Patterns
- Container with most water (distance between walls)
- Palindrome checking (mirror positions)
- Trapping rain water (height relationships at positions)
- Two sum with sorted array (pointers moving inward/outward)

### Example 1: Container With Most Water (SPATIAL)

**Problem Statement:**
```
Given heights of vertical lines.
Find two lines that form a container with maximum area.
Area = width × min(height[i], height[j])

heights = [1, 8, 6, 2, 5, 4, 8, 3, 7]
Output: 49 (width 8, height 7, area = 8 × 7)
```

**Why this is SPATIAL:**
- Area depends on **distance** between indices
- Height relationship matters
- Not about temporal ordering

**CORRECT APPROACH (Spatial thinking - Two Pointer Inward):**
```kotlin
fun maxArea(height: IntArray): Int {
    var left = 0
    var right = height.size - 1
    var maxArea = 0

    while (left < right) {
        // Area is constrained by shorter wall
        val h = minOf(height[left], height[right])
        val width = right - left
        val area = h * width
        maxArea = maxOf(maxArea, area)

        // Move the shorter wall inward (spatial reasoning)
        // If we move the taller wall, width decreases but height can't increase
        if (height[left] < height[right]) {
            left++
        } else {
            right--
        }
    }

    return maxArea
}

// For [1, 8, 6, 2, 5, 4, 8, 3, 7]:
// Start: left=0 (h=1), right=8 (h=7), area=1*8=8, move left (shorter)
// left=1 (h=8), right=8 (h=7), area=7*7=49, move right (shorter)
// left=1 (h=8), right=7 (h=3), area=3*6=18, move right (shorter)
// ... continue until left >= right
// Returns 49 ✓
```

**Why this two-pointer inward works:**
- Starts with maximum width
- Moves inward to explore smaller widths with potentially taller walls
- Spatial reasoning: physical distance is key

---

### Example 2: Palindrome Checking (SPATIAL)

**Problem Statement:**
```
Check if a string is a palindrome.
Characters must mirror around center.

s = "racecar"
Output: true (mirrors around 'e')
```

**Why this is SPATIAL:**
- Mirroring: first mirrors with last, second with second-last
- Position from edges matters
- Not about temporal ordering

**CORRECT APPROACH (Spatial thinking - Two Pointer Outside-In):**
```kotlin
fun isPalindrome(s: String): Boolean {
    var left = 0
    var right = s.length - 1

    while (left < right) {
        // Check spatial mirror relationship
        if (s[left] != s[right]) {
            return false
        }
        left++
        right--
    }

    return true
}

// For "racecar":
// left=0 (r), right=6 (r) ✓
// left=1 (a), right=5 (a) ✓
// left=2 (c), right=4 (c) ✓
// left=3 (e), center, stop
// Returns true ✓
```

**Why outside-in works here:**
- Checking mirror positions (spatial)
- Works from edges toward center (symmetric)
- NOT temporal ordering

---

### Example 3: Trapping Rain Water (SPATIAL)

**Problem Statement:**
```
Given elevations, calculate trapped water.
Water level at position = min(maxLeft, maxRight) - height[i]

heights = [0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]
Output: 6
```

**Why this is SPATIAL:**
- Water trapped depends on surrounding heights
- Left and right walls matter equally (symmetry)
- Not about order of processing

**CORRECT APPROACH (Spatial thinking - Two Pointer):**
```kotlin
fun trap(height: IntArray): Int {
    if (height.size < 3) return 0

    var left = 0
    var right = height.size - 1
    var leftMax = 0
    var rightMax = 0
    var water = 0

    while (left < right) {
        // Process side with smaller max height
        if (height[left] < height[right]) {
            if (height[left] >= leftMax) {
                leftMax = height[left]
            } else {
                water += leftMax - height[left]
            }
            left++
        } else {
            if (height[right] >= rightMax) {
                rightMax = height[right]
            } else {
                water += rightMax - height[right]
            }
            right--
        }
    }

    return water
}

// For [0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]:
// Water trapped between walls on left and right
// Spatial calculation of trapped volume
// Returns 6 ✓
```

**Why two-pointer works:**
- Processes from both edges (spatial)
- Balances left and right max heights (symmetric)
- Spatial insight: water is trapped between walls

---

## PART 3: DECISION MATRIX

### How to Identify Constraint Type

| Question | Answer = TEMPORAL | Answer = SPATIAL |
|----------|-------------------|------------------|
| Does order of processing matter? | YES → TEMPORAL | NO → SPATIAL |
| Does "before/after" or "earlier/later" matter? | YES → TEMPORAL | NO → SPATIAL |
| Can earlier elements affect validity of later elements? | YES → TEMPORAL | NO → SPATIAL |
| Do we need to know "maximum/minimum seen so far"? | YES → TEMPORAL | NO → SPATIAL |
| Does distance between elements matter? | NO → TEMPORAL | YES → SPATIAL |
| Is it about mirroring or symmetry? | NO → TEMPORAL | YES → SPATIAL |
| Does the problem involve intervals or sequences? | YES → TEMPORAL | NO → SPATIAL |
| Does the problem involve pairs or combinations? | MAYBE EITHER | Check carefully |

---

## PART 4: ALGORITHM SELECTION GUIDE

### For TEMPORAL Constraints

**Single-Pass Left-to-Right:**
```kotlin
var state = initialValue
for (i in 0 until array.size) {
    // Process based on state built from past
    state = updateState(state, array[i])
}
```
- Max profit: track minimum seen
- LIS: track best ending at each position
- DP problems: track state up to current position

**Key:** Current decisions depend on what came **before**

---

### For SPATIAL Constraints

**Two-Pointer Inward (Outside-In):**
```kotlin
var left = 0
var right = size - 1
while (left < right) {
    // Process pair from outside
    // Move based on spatial logic
    if (shouldMoveLeft) left++ else right--
}
```
- Container with water: maximize distance
- Palindrome: check mirrors
- Trapping rain: balance wall heights

**Key:** Pairs are based on **position**, not sequence

---

**Two-Pointer Outward (Inside-Out):**
```kotlin
var left = center
var right = center
while (left >= 0 && right < size) {
    // Expand outward from center
}
```
- Expand around center (palindrome substring)
- Search from target outward

**Key:** Build solution from **center** or **foundation**

---

## PART 5: COMMON MISTAKES

### Mistake 1: Using Spatial Algorithm for Temporal Problem

```kotlin
// ❌ WRONG: Stock profit with outside-in pairing
for ((left, right) in (0 until size/2).zip((size-1) downTo (size/2))) {
    profit = prices[right] - prices[left]  // Ignores ordering!
}

// ✓ CORRECT: Stock profit with single-pass
var minPrice = prices[0]
for (i in 1 until prices.size) {
    profit = prices[i] - minPrice  // Respects temporal order
}
```

**Why it fails:** Outside-in pairing doesn't guarantee buy before sell.

---

### Mistake 2: Using Temporal Algorithm for Spatial Problem

```kotlin
// ❌ WRONG: Container with water using single-pass
var maxArea = 0
for (i in 0 until heights.size) {
    // No two-pointer balancing
    maxArea = maxOf(maxArea, heights[i] * i)  // Missing pairing!
}

// ✓ CORRECT: Container with water using two-pointer inward
var left = 0
var right = size - 1
while (left < right) {
    val area = minOf(height[left], height[right]) * (right - left)
    maxArea = maxOf(maxArea, area)  // Respects distance
    if (height[left] < height[right]) left++ else right--
}
```

**Why it fails:** Single-pass doesn't explore spatial distance relationships.

---

### Mistake 3: Not Identifying Constraint Type

```kotlin
// Problem: "Find two numbers that sum to target"
// This could be TEMPORAL (array processing order matters)
// Or SPATIAL (sorted array, use two pointers)

// ✓ For unsorted array (TEMPORAL-like):
fun twoSum(nums: IntArray, target: Int): IntArray {
    val map = mutableMapOf<Int, Int>()
    for ((i, num) in nums.withIndex()) {
        val complement = target - num
        if (complement in map) {
            return intArrayOf(map[complement]!!, i)
        }
        map[num] = i
    }
    return intArrayOf()
}

// ✓ For sorted array (SPATIAL):
fun twoSumSorted(nums: IntArray, target: Int): IntArray {
    var left = 0
    var right = nums.size - 1
    while (left < right) {
        val sum = nums[left] + nums[right]
        when {
            sum == target -> return intArrayOf(left + 1, right + 1)
            sum < target -> left++
            else -> right--
        }
    }
    return intArrayOf()
}
```

**Why it matters:** Same problem, different constraints → different algorithms

---

## PART 6: COMPREHENSIVE EXAMPLES

### Example: Multiple Problems, Constraint Analysis

#### Problem A: Best Time to Buy and Sell Stock
```
prices = [7, 1, 5, 3, 6, 4]
Constraint: Buy before sell (TEMPORAL)
Algorithm: Single-pass, track minimum

Answer: 5 (buy at 1, sell at 6)
```

#### Problem B: Container With Most Water
```
heights = [1, 8, 6, 2, 5, 4, 8, 3, 7]
Constraint: Maximize distance × minimum height (SPATIAL)
Algorithm: Two-pointer inward

Answer: 49
```

#### Problem C: Valid Palindrome
```
s = "A man, a plan, a canal: Panama"
Constraint: Mirror around center (SPATIAL)
Algorithm: Two-pointer outside-in

Answer: true
```

#### Problem D: Longest Increasing Subsequence
```
nums = [10, 9, 2, 5, 3, 7, 101, 18]
Constraint: Elements must be in increasing positional order (TEMPORAL)
Algorithm: DP, track best ending at each position

Answer: 4 ([2, 3, 7, 101])
```

---

## SUMMARY

### TEMPORAL CONSTRAINTS
- **What matters:** Sequence, ordering, causality
- **Question:** "Does this come before/after?"
- **Algorithms:** Single-pass, DP, prefix/suffix tracking
- **Examples:** Stock profit, LIS, DP problems
- **Key pattern:** Current state depends on past

### SPATIAL CONSTRAINTS
- **What matters:** Distance, position, symmetry
- **Question:** "How far apart? Do they mirror?"
- **Algorithms:** Two-pointer, outside-in or inside-out
- **Examples:** Container with water, palindrome, trapping rain
- **Key pattern:** Pairs based on position, not sequence

### DECISION PROCESS
1. Read problem carefully
2. Ask: "Does sequence/ordering matter?"
3. If YES → TEMPORAL → Single-pass or DP
4. If NO → SPATIAL → Two-pointer
5. Code with understanding, not pattern matching

### CRITICAL INSIGHT
The **constraint type** determines the **algorithm family**.
Choosing the wrong family leads to wrong answers.
Always identify constraints before coding.
