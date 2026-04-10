# Kotlin Labels: Complete Guide

## What Are Labels?

A **label** is an identifier followed by `@` that marks a specific location in your code—usually a loop or lambda
expression. Labels enable you to:

- Return from nested lambdas to a specific location
- Break or continue from nested loops to outer scopes
- Control flow in multi-level nested structures

Labels use the syntax: `labelName@`

---

## 1. Basic Label Syntax

### Marking Locations

```kotlin
// Loop labels
outer@ for (i in 1..3) {
    for (j in 1..3) {
        println("$i, $j")
    }
}

// Lambda labels
val result = outerLambda@{
    return@outerLambda 42
}

// Function labels
myFunction@{
    // Can return from here
}
```

---

## 2. Labels with Return (Most Common Use)

### Return from Nested Lambdas

Without labels, `return` exits the **entire enclosing function**, not just the lambda:

```kotlin
fun demonstrateReturnProblem() {
    val numbers = listOf(1, 2, 3, 4, 5)

    numbers.forEach { num ->
        if (num == 3) {
            return  // ❌ Exits demonstrateReturnProblem entirely!
        }
        println(num)
    }
    println("This line is never reached")
}

// Output:
// 1
// 2
```

**Solution: Use a label with `return@label`**

```kotlin
fun demonstrateReturnWithLabel() {
    val numbers = listOf(1, 2, 3, 4, 5)

    numbers.forEach { num ->
        if (num == 3) {
            return@forEach  // ✅ Returns only from forEach lambda
        }
        println(num)
    }
    println("This line IS reached")
}

// Output:
// 1
// 2
// 4
// 5
// This line IS reached
```

### Implicit vs Explicit Labels

```kotlin
// Implicit label (function name becomes the label)
list.forEach { item ->
    if (item == 5) return@forEach  // Implicitly returns from forEach
}

// Explicit label
list.forEach label@{ item ->
    if (item == 5) return@label  // Explicitly returns from label
}

// Both are equivalent in this case
```

---

## 3. The `@outer` Pattern (Nested Lambdas)

### Common Real-World Scenario: Nested forEach

```kotlin
fun searchMatrix(matrix: List<List<Int>>, target: Int): Boolean {
    var found = false

    matrix.forEach { row ->
        row.forEach { element ->
            if (element == target) {
                found = true
                return@forEach  // ❌ Only exits inner forEach
            }
        }
    }

    return found
}
```

**Problem:** `return@forEach` only exits the inner forEach, so you're still checking remaining rows.

**Solution: Label the outer forEach**

```kotlin
fun searchMatrix(matrix: List<List<Int>>, target: Int): Boolean {
    matrix.forEach outer@{ row ->
        row.forEach { element ->
            if (element == target) {
                return@outer  // ✅ Exits outer forEach entirely
            }
        }
    }

    return false
}
```

### Example: Nested foreach with @outer

```kotlin
fun demonstrateOuterLabel() {
    val grid = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9)
    )

    grid.forEach outer@{ row ->
        println("Starting row: $row")
        row.forEach { element ->
            if (element == 5) {
                println("Found 5, exiting outer forEach")
                return@outer  // Exits both loops
            }
            println("  Element: $element")
        }
    }
    println("Done with search")
}

// Output:
// Starting row: [1, 2, 3]
//   Element: 1
//   Element: 2
//   Element: 3
// Starting row: [4, 5, 6]
//   Element: 4
// Found 5, exiting outer forEach
// Done with search
```

---

## 4. Labels with Break and Continue

### Breaking from Nested Loops

```kotlin
fun demonstrateBreakLabel() {
    outer@ for (i in 1..3) {
        for (j in 1..3) {
            if (i == 2 && j == 2) {
                println("Breaking from nested loop at ($i, $j)")
                break@outer  // Breaks entire outer for loop
            }
            println("($i, $j)")
        }
    }
    println("After nested loops")
}

// Output:
// (1, 1)
// (1, 2)
// (1, 3)
// (2, 1)
// Breaking from nested loop at (2, 2)
// After nested loops
```

### Continue to Next Iteration of Outer Loop

```kotlin
fun demonstrateContinueLabel() {
    outer@ for (i in 1..3) {
        for (j in 1..3) {
            if (j == 2) {
                continue@outer  // Skips to next iteration of i
            }
            println("($i, $j)")
        }
    }
}

// Output:
// (1, 1)
// (2, 1)
// (3, 1)
```

---

## 5. Scoping Rules

### What a Label Refers To

A label always marks the **lambda or loop immediately following it**:

```kotlin
// Label marks this forEach
list.forEach label@{ item ->
    // This entire lambda is the labeled scope
}

// Label marks this for loop
outer@ for (i in 1..10) {
    // This entire for loop is the labeled scope
}
```

### Accessibility Rules

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

numbers.forEach outer@{ num1 ->
    numbers.forEach inner@{ num2 ->
        if (num1 * num2 > 6) {
            // Can return to either label
            return@inner   // Exits inner forEach
            return@outer   // Exits outer forEach (line would be unreachable)
        }
    }
}

// Cannot do:
// return@outer from outside the outer@ forEach
```

---

## 6. Why You Can't Return from forEach Without a Label

### The Non-Local Return Problem

```kotlin
fun regularFunction() {
    listOf(1, 2, 3).forEach { num ->
        if (num == 2) {
            return  // ❌ Non-local return exits regularFunction entirely
        }
    }
    println("Unreachable")
}
```

**Why?** Lambdas are inline, so `return` is compiled as a return from the enclosing function.

**Solution:**

```kotlin
fun withLabel() {
    listOf(1, 2, 3).forEach { num ->
        if (num == 2) {
            return@forEach  // ✅ Returns only from this iteration
        }
    }
    println("This runs")
}
```

---

## 7. Practical Example: Matrix Search (LeetCode Pattern)

```kotlin
fun searchInMatrix(matrix: List<List<Int>>, target: Int): Pair<Int, Int>? {
    matrix.forEachIndexed outer@{ rowIndex, row ->
        row.forEachIndexed { colIndex, element ->
            if (element == target) {
                println("Found $target at ($rowIndex, $colIndex)")
                return@outer  // Exit both loops
            }
        }
    }
    return null
}

fun main() {
    val matrix = listOf(
        listOf(1, 3, 5, 7),
        listOf(10, 11, 16, 20),
        listOf(23, 30, 34, 60)
    )

    searchInMatrix(matrix, 13)  // Not found, checks all
    searchInMatrix(matrix, 16)  // Found and exits early
}
```

---

## 8. Multiple Labels (Rare But Possible)

```kotlin
fun multipleLabels() {
    outer@ for (i in 1..3) {
        middle@ for (j in 1..3) {
            inner@ for (k in 1..3) {
                if (i == 2 && j == 2 && k == 2) {
                    println("Exiting from triple nested loop")
                    break@middle  // Can break to any labeled scope
                }
                print("($i,$j,$k) ")
            }
        }
    }
}
```

---

## 9. Labels in Higher-Order Functions

### Custom Lambda Receiver

```kotlin
fun process(block: ProcessScope.() -> Unit) {
    ProcessScope().block()
}

class ProcessScope {
    fun onMatch(predicate: Boolean) {
        if (predicate) {
            return  // ❌ Problem: hard to escape
        }
    }
}

// Better approach with labeled lambda
inline fun processWithLabel(block: ProcessScope.() -> Unit) {
    ProcessScope().run block@{
        block()  // Can now use return@block to escape
    }
}
```

---

## 10. When to Use Labels (Best Practices)

### ✅ Use Labels When:

1. **Nested forEach/map:** Need to exit the outer iteration
2. **Matrix traversal:** Searching 2D structures
3. **Nested lambdas:** Early termination needed
4. **Multiple loop levels:** Breaking/continuing to specific level

### ❌ Don't Use Labels When:

1. **Single loop:** Use simple return/break
2. **Complex nesting:** Refactor into separate functions
3. **Confused naming:** If label name doesn't clarify intent
4. **Deep nesting (3+):** Extract into helper functions

### Better Alternative: Extract Functions

```kotlin
// ❌ Nested labels (hard to read)
matrix.forEach outer@{ row ->
    row.forEach { element ->
        if (element == target) {
            return@outer
        }
    }
}

// ✅ Extracted function (clearer)
fun findInMatrix(matrix: List<List<Int>>, target: Int): Boolean {
    for (row in matrix) {
        if (row.contains(target)) {
            return true
        }
    }
    return false
}
```

---

## 11. Common Patterns Summary

| Pattern            | Syntax           | Use Case                             |
|--------------------|------------------|--------------------------------------|
| Implicit return    | `return@forEach` | Exit specific lambda                 |
| Explicit return    | `return@myLabel` | Exit labeled lambda                  |
| Outer label return | `return@outer`   | Exit nested lambda                   |
| Break loop         | `break@label`    | Break nested loop                    |
| Continue loop      | `continue@label` | Skip to next iteration of outer loop |
| No label needed    | Simple `return`  | Exit entire function                 |

---

## 12. Quick Reference: Common Mistakes

```kotlin
// ❌ Mistake 1: Forgetting the @ symbol
// return outer  // Won't compile
// return @outer { }  // Wrong placement

// ✅ Correct
return@outer

// ❌ Mistake 2: Using wrong label name
list.forEach label@{ item ->
    return@forEach  // ❌ label is not @forEach
}

// ✅ Correct
list.forEach label@{ item ->
    return@label
}

// ❌ Mistake 3: Label in wrong position
list.forEach @label { item ->  // ❌ Wrong
    // ...
}

// ✅ Correct
list.forEach label@{ item ->
    // ...
}

// ❌ Mistake 4: Trying to reference non-existent label
list.forEach { item ->
    return@outer  // ❌ @outer doesn't exist
}

// ✅ Correct
list.forEach outer@{ item ->
    return@outer
}
```

---

## Summary

- **Labels** mark specific scopes (loops or lambdas) for control flow
- **`@labelName`** syntax on loops and lambdas
- **`return@label`** exits to that specific scope
- **`break@label` / `continue@label`** work with nested loops
- **Most common use:** `@outer` for nested forEach in matrix/grid problems
- **Implicit labels:** Function names auto-become labels (e.g., `return@forEach`)
- **When to use:** Nested structures where early termination is needed; when not, extract functions
