# 🚀 Kotlin Competitive Programming I/O & Template Guide

## 🧠 Overview

This guide summarizes the fastest ways to handle input/output in Kotlin
for competitive programming, along with a **full optimized template
(Kotlin kit)**.

------------------------------------------------------------------------

# ⚡ 1. Why Fast I/O Matters

In competitive programming: - Input size can be huge (10\^6 -- 10\^7
numbers) - Default Kotlin I/O is **too slow**

### ❌ Slow methods

-   `readLine()`
-   `split()`
-   `Scanner`
-   `println()` inside loops

### ✅ Fast methods

-   Buffered input
-   Manual parsing
-   Batched output

------------------------------------------------------------------------

# 🥇 2. Two Levels of Fast I/O

## Level 1 (Standard Fast)

Uses: - BufferedReader - StringTokenizer

### Pros

-   Easy to write
-   Good for most problems

### Cons

-   Still creates Strings
-   Slower than raw parsing

------------------------------------------------------------------------

## Level 2 (Ultra Fast)

Uses: - BufferedInputStream - Manual byte parsing

### Pros

-   No string allocation
-   Extremely fast
-   Handles massive input

### Cons

-   Harder to understand
-   More code

------------------------------------------------------------------------

# ⚙️ 3. How Fast Input Works (Simple Explanation)

### Normal way:

    "123 456".split(" ")

➡ creates multiple strings → slow ❌

### Fast way:

Read raw bytes:

    '1' '2' '3'

Convert manually:

    res = res * 10 + digit

------------------------------------------------------------------------

### Example

Input:

    123

Steps: - '1' → res = 1 - '2' → res = 12 - '3' → res = 123

------------------------------------------------------------------------

# ⚡ 4. Fast Output

### ❌ Slow

    for (...) println(x)

### ✅ Fast

    StringBuilder → append → print once

------------------------------------------------------------------------

# 🧱 5. FULL KOTLIN CP TEMPLATE (ULTIMATE KIT)

``` kotlin
import java.io.BufferedInputStream
import java.lang.StringBuilder

// FAST INPUT
private val input = BufferedInputStream(System.`in`)
private val buffer = ByteArray(1 shl 16)
private var len = 0
private var ptr = 0

private fun readByte(): Int {
    if (ptr >= len) {
        len = input.read(buffer)
        ptr = 0
        if (len <= 0) return -1
    }
    return buffer[ptr++].toInt()
}

fun nextInt(): Int {
    var c = readByte()
    while (c <= 32) c = readByte()

    var sign = 1
    if (c == '-'.code) {
        sign = -1
        c = readByte()
    }

    var res = 0
    while (c > 32) {
        res = res * 10 + (c - '0'.code)
        c = readByte()
    }
    return res * sign
}

fun nextLong(): Long {
    var c = readByte()
    while (c <= 32) c = readByte()

    var sign = 1
    if (c == '-'.code) {
        sign = -1
        c = readByte()
    }

    var res = 0L
    while (c > 32) {
        res = res * 10 + (c - '0'.code)
        c = readByte()
    }
    return if (sign == 1) res else -res
}

// FAST OUTPUT
private val sb = StringBuilder()

fun print(x: Any) { sb.append(x) }
fun println(x: Any) { sb.append(x).append('\n') }

// UTIL
inline fun min(a: Int, b: Int) = if (a < b) a else b
inline fun max(a: Int, b: Int) = if (a > b) a else b

fun IntArray.read(n: Int) {
    for (i in 0 until n) this[i] = nextInt()
}

// MAIN
fun main() {
    val t = nextInt()

    repeat(t) {
        solve()
    }

    print(sb)
}

// SOLVE
fun solve() {
    val n = nextInt()
    val arr = IntArray(n)
    arr.read(n)

    var sum = 0L
    for (x in arr) sum += x

    println(sum)
}
```

------------------------------------------------------------------------

# 🧩 6. Example Walkthrough

### Input

    1
    5
    1 2 3 4 5

### Execution

-   Reads `t = 1`
-   Reads `n = 5`
-   Reads array
-   Computes sum

### Output

    15

------------------------------------------------------------------------

# 🔥 7. When to Use What

  Situation      Method
  -------------- ----------------
  Small input    readLine()
  Medium input   BufferedReader
  Large input    This template

------------------------------------------------------------------------

# ❗ 8. Common Mistakes

-   Using split → memory overhead
-   Printing in loops → slow
-   Mixing input methods → bugs
-   Overcomplicating template

------------------------------------------------------------------------

# 🏁 Final Takeaways

-   Always batch output
-   Avoid string parsing when possible
-   Use raw input for serious contests
-   Keep template simple in your head

------------------------------------------------------------------------

# 💡 Pro Tip

Speed of typing and clarity matters more than micro-optimizations in
easy problems.

Use this template mainly for: - Codeforces Div2 C+ - Div1 - ICPC

------------------------------------------------------------------------

End of Guide
