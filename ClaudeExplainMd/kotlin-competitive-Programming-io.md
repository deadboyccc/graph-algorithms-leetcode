# 🚀 Kotlin Competitive Programming I/O --- Complete Guide

## 🧠 Goal

Understand: - ALL input methods in Kotlin - Why some are slow vs fast -
When to use each - A full **contest-ready Kotlin kit**

------------------------------------------------------------------------

# 🥉 1. Slowest: Scanner (DO NOT USE)

``` kotlin
import java.util.Scanner

fun main() {
    val sc = Scanner(System.`in`)
    val n = sc.nextInt()
    val arr = IntArray(n) { sc.nextInt() }

    println(arr.sum())
}
```

## ❌ Why Scanner is Slow

-   Uses regex internally
-   Parses tokens with heavy abstraction
-   Lots of object creation

## 🧠 Mental Model

Scanner = "Java enterprise parsing engine"\
→ Overkill for CP

------------------------------------------------------------------------

# 🥈 2. Medium: readLine + split

``` kotlin
fun main() {
    val n = readLine()!!.toInt()
    val arr = readLine()!!.split(" ").map { it.toInt() }

    println(arr.sum())
}
```

## ❌ Why This is Slow

-   `split()` creates MANY strings
-   Each number = new object
-   High memory pressure

## Example Breakdown

Input:

    1 23 456

Becomes:

    ["1", "23", "456"]

Then: - Convert each to Int → extra work

------------------------------------------------------------------------

# 🥇 3. Fast: BufferedReader + StringTokenizer

``` kotlin
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.StringTokenizer

val br = BufferedReader(InputStreamReader(System.`in`))
var st = StringTokenizer("")

fun next(): String {
    while (!st.hasMoreTokens()) {
        st = StringTokenizer(br.readLine())
    }
    return st.nextToken()
}

fun nextInt(): Int = next().toInt()

fun main() {
    val n = nextInt()
    val arr = IntArray(n) { nextInt() }

    println(arr.sum())
}
```

## ✅ Why This is Faster

-   Reads large chunks at once
-   Avoids `split()`
-   Reuses tokenizer

## 🧠 Inner Working

-   Reads line → `"1 23 456"`
-   Tokenizer walks through string without creating many objects

------------------------------------------------------------------------

# ⚡ 4. FASTEST: BufferedInputStream (Byte Parsing)

``` kotlin
import java.io.BufferedInputStream

val input = BufferedInputStream(System.`in`)

fun nextInt(): Int {
    var c = input.read()
    while (c <= 32) c = input.read()

    var sign = 1
    if (c == '-'.code) {
        sign = -1
        c = input.read()
    }

    var res = 0
    while (c > 32) {
        res = res * 10 + (c - '0'.code)
        c = input.read()
    }
    return res * sign
}
```

## 🚀 Why This is Fastest

-   No Strings created
-   No tokenization
-   Pure byte processing

------------------------------------------------------------------------

## 🧠 Step-by-Step Example

Input:

    123

Processing: - '1' → res = 1 - '2' → res = 12 - '3' → res = 123

------------------------------------------------------------------------

# ⚡ 5. Fast Output

## ❌ Slow

``` kotlin
for (i in 0..n) println(i)
```

## ✅ Fast

``` kotlin
val sb = StringBuilder()
for (i in 0..n) sb.append(i).append('\n')
print(sb)
```

## Why Faster?

-   Avoids flushing output repeatedly
-   Writes once

------------------------------------------------------------------------

# 🧱 6. FULL KOTLIN KIT

``` kotlin
import java.io.BufferedInputStream
import java.lang.StringBuilder

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

private val sb = StringBuilder()

fun println(x: Any) { sb.append(x).append('\n') }

fun IntArray.read(n: Int) {
    for (i in 0 until n) this[i] = nextInt()
}

fun main() {
    val t = nextInt()
    repeat(t) { solve() }
    print(sb)
}

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

# 🏁 7. Final Comparison

  Method                Speed          Use
  --------------------- -------------- ---------------
  Scanner               ❌ Very Slow   Never
  readLine + split      ⚠️ Medium      Small input
  BufferedReader + ST   ✅ Fast        Most problems
  BufferedInputStream   🚀 Fastest     Heavy input

------------------------------------------------------------------------

# 💡 Final Advice

-   Use **BufferedReader + ST** normally
-   Use **raw input** for heavy problems
-   Always batch output

------------------------------------------------------------------------

End.
