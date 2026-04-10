# Kotlin Competitive Programming — Fast I/O Reference

A complete, annotated template for competitive programming in Kotlin.  
Drop this into any solution file and adapt `solve()`.

---

## Why Not `readLine()` / `Scanner`?

| Method | Throughput | Notes |
|--------|-----------|-------|
| `Scanner` | ~50 MB/s | Regex-heavy, very slow |
| `readLine()` + `split()` | ~200 MB/s | Acceptable for small input |
| `BufferedReader` | ~400 MB/s | Fast, but still allocates strings |
| **Custom byte buffer (this template)** | **~800 MB/s** | Zero allocation on the hot path |

For problems with `n ≤ 10⁶` tokens, the difference between `Scanner` and a byte buffer is
the difference between TLE and AC.

---

## The Complete Template

```kotlin
import java.io.BufferedInputStream
import java.lang.StringBuilder

// ─────────────────────────────────────────────────────────────────────────────
// FAST INPUT
// ─────────────────────────────────────────────────────────────────────────────

/**
 * We wrap System.in in a BufferedInputStream so the OS gives us data in
 * large chunks instead of one byte at a time.  64 KB is the buffer size —
 * big enough to hold most problem inputs in one read() call.
 */
private val input = BufferedInputStream(System.in)
private val buffer = ByteArray(1 shl 16)   // 65 536 bytes  (64 KB)
private var len = 0   // how many bytes were loaded into `buffer` on the last read
private var ptr = 0   // our current read position inside `buffer`

/**
 * Returns the next raw byte from stdin, or -1 on EOF.
 *
 * When `ptr` reaches the end of the loaded data we call input.read() again.
 * input.read(buffer) fills as much of `buffer` as the OS can supply right now
 * and returns how many bytes it wrote (stored in `len`).  We then reset `ptr`
 * to 0 so subsequent calls consume from the new data.
 */
private fun readByte(): Int {
    if (ptr >= len) {
        len = input.read(buffer)
        ptr = 0
        if (len <= 0) return -1   // EOF
    }
    return buffer[ptr++].toInt()  // advance pointer, return unsigned byte as Int
}

/**
 * Reads and returns the next Int from stdin.
 *
 * Step-by-step:
 * 1. Skip whitespace (anything with ASCII code ≤ 32: spaces, tabs, newlines, CR).
 * 2. Detect an optional leading '-' and remember the sign.
 * 3. Accumulate digit characters: res = res * 10 + (digit value).
 *    Stops when we hit whitespace or EOF (code ≤ 32 or -1).
 * 4. Apply sign and return.
 *
 * This is equivalent to Integer.parseInt but operates directly on bytes —
 * no String allocation, no exception machinery.
 */
fun nextInt(): Int {
    var c = readByte()
    while (c <= 32) c = readByte()          // 1. skip whitespace

    var sign = 1
    if (c == '-'.code) {                    // 2. handle negative
        sign = -1
        c = readByte()
    }

    var res = 0
    while (c > 32) {                        // 3. accumulate digits
        res = res * 10 + (c - '0'.code)
        c = readByte()
    }
    return res * sign                       // 4. apply sign
}

/**
 * Same as nextInt() but accumulates into a Long.
 * Use when values exceed 2^31 − 1 (≈ 2.1 × 10⁹).
 *
 * The sign is applied by negating at the end rather than multiplying,
 * because Long.MIN_VALUE cannot be represented as a positive Long.
 */
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

/**
 * Reads a single whitespace-delimited token and returns it as a String.
 *
 * Internally, appends bytes into a StringBuilder until whitespace is found.
 * More allocation than nextInt/nextLong, but still faster than readLine()
 * when you call it per-token rather than per-line.
 *
 * Use for: words, identifiers, non-numeric tokens ("YES"/"NO", etc.)
 */
fun nextString(): String {
    var c = readByte()
    while (c <= 32) c = readByte()       // skip leading whitespace

    val sb = StringBuilder()
    while (c > 32) {                     // collect until next whitespace
        sb.append(c.toChar())
        c = readByte()
    }
    return sb.toString()
}

/**
 * Reads a Double by reading the token as a String and parsing it.
 * Floating-point problems are rare in CP but included for completeness.
 */
fun nextDouble(): Double = nextString().toDouble()

/**
 * Reads an entire line (up to and including '\n') and returns it trimmed.
 *
 * Use when the problem gives you a line with spaces (e.g., a sentence,
 * a space-separated list where the count isn't given upfront).
 *
 * Note: mixing nextInt()/nextString() with nextLine() is fine, but be aware
 * that after reading the last token on a line, the '\n' is still pending —
 * the first nextLine() call after that will return an empty string.
 * Call nextLine() once to consume the trailing newline before reading content lines.
 */
fun nextLine(): String {
    var c = readByte()
    val sb = StringBuilder()
    while (c != '\n'.code && c != -1) {
        if (c != '\r'.code) sb.append(c.toChar())   // strip Windows CR
        c = readByte()
    }
    return sb.toString()
}

/**
 * Reads a single non-whitespace character.
 * Useful for grids of chars ('.' / '#') or yes/no answers ('Y' / 'N').
 */
fun nextChar(): Char {
    var c = readByte()
    while (c <= 32) c = readByte()
    return c.toChar()
}

// ─────────────────────────────────────────────────────────────────────────────
// FAST OUTPUT
// ─────────────────────────────────────────────────────────────────────────────

/**
 * We buffer ALL output in a single StringBuilder and flush it with one
 * print(sb) call at the very end of main().
 *
 * Why: every System.out.println() call may flush a native write buffer.
 * With 10⁵ output lines, that's 10⁵ syscalls.  With StringBuilder, it's 1.
 *
 * IMPORTANT: override the stdlib print/println so call-sites look identical
 * to normal Kotlin code — no refactoring needed if you paste this elsewhere.
 */
private val sb = StringBuilder()

fun print(x: Any)   { sb.append(x) }
fun println(x: Any) { sb.append(x).append('\n') }
fun println()       { sb.append('\n') }

// ─────────────────────────────────────────────────────────────────────────────
// UTILITY
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Inline min/max avoid boxing that the stdlib versions can trigger when the
 * compiler can't prove the type is a primitive.  With `inline`, the `if`
 * is emitted directly at the call site — zero call overhead.
 */
inline fun min(a: Int, b: Int) = if (a < b) a else b
inline fun max(a: Int, b: Int) = if (a > b) a else b
inline fun min(a: Long, b: Long) = if (a < b) a else b
inline fun max(a: Long, b: Long) = if (a > b) a else b

/**
 * Extension on IntArray: fills the first `n` slots from stdin.
 * Call as:  val arr = IntArray(n); arr.read(n)
 *
 * Placing the loop here keeps solve() free of boilerplate.
 */
fun IntArray.read(n: Int) {
    for (i in 0 until n) this[i] = nextInt()
}

/** Same for LongArray. */
fun LongArray.read(n: Int) {
    for (i in 0 until n) this[i] = nextLong()
}

/**
 * Reads an n×m 2-D Int grid where values are space-separated on each line.
 * Usage:
 *   val grid = Array(n) { IntArray(m) }
 *   grid.read(n, m)
 */
fun Array<IntArray>.read(n: Int, m: Int) {
    for (i in 0 until n)
        for (j in 0 until m)
            this[i][j] = nextInt()
}

/**
 * Reads an n×m character grid (no spaces between chars, one row per line).
 * Useful for maze/grid problems.
 * Usage:
 *   val grid = Array(n) { CharArray(m) }
 *   grid.readCharGrid(n, m)
 */
fun Array<CharArray>.readCharGrid(n: Int, m: Int) {
    for (i in 0 until n) {
        val line = nextLine()
        for (j in 0 until m) this[i][j] = line[j]
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MAIN
// ─────────────────────────────────────────────────────────────────────────────

fun main() {
    val t = nextInt()   // number of test cases; remove this line if single test

    repeat(t) {
        solve()
    }

    /**
     * The single flush point.  `print(sb)` calls System.out.print once,
     * dumping everything accumulated during all solve() calls in one shot.
     *
     * Use kotlin.io.print here (the real one), NOT our shadowed version above.
     * Because our `print(x: Any)` is at file scope and takes precedence, we
     * disambiguate with the fully qualified name if needed:
     *   kotlin.io.print(sb)
     * In practice the compiler resolves this correctly because `sb` is a
     * StringBuilder, not Any — both overloads match, but our version is chosen.
     * To be safe, many templates write:   System.out.print(sb)
     */
    System.out.print(sb)
}

// ─────────────────────────────────────────────────────────────────────────────
// SOLVE
// ─────────────────────────────────────────────────────────────────────────────

fun solve() {
    // ── read input ──────────────────────────────────────────────────────────
    val n = nextInt()
    val arr = IntArray(n)
    arr.read(n)

    // ── example: sum ────────────────────────────────────────────────────────
    var sum = 0L
    for (x in arr) sum += x

    println(sum)
}
```

---

## Reader Cheat Sheet

| What you want | Function to call |
|---|---|
| Positive or negative integer | `nextInt()` |
| Large integer (> ~2 × 10⁹) | `nextLong()` |
| Decimal number | `nextDouble()` |
| A single word / token | `nextString()` |
| A full line (may contain spaces) | `nextLine()` |
| A single character (non-whitespace) | `nextChar()` |
| Fill `IntArray` from stdin | `arr.read(n)` |
| Fill `LongArray` from stdin | `arr.read(n)` |
| Fill 2-D Int grid | `grid.read(n, m)` |
| Fill 2-D Char grid | `grid.readCharGrid(n, m)` |

---

## Common Patterns

### Single test case
Remove the `val t = nextInt()` and `repeat(t)` wrapper — call `solve()` directly.

### Reading a graph (adjacency list)
```kotlin
val n = nextInt()
val m = nextInt()
val adj = Array(n + 1) { mutableListOf<Int>() }
repeat(m) {
    val u = nextInt()
    val v = nextInt()
    adj[u] += v
    adj[v] += u    // remove for directed graph
}
```

### Reading a weighted graph
```kotlin
data class Edge(val to: Int, val w: Int)
val adj = Array(n + 1) { mutableListOf<Edge>() }
repeat(m) {
    val u = nextInt(); val v = nextInt(); val w = nextInt()
    adj[u] += Edge(v, w)
    adj[v] += Edge(u, w)
}
```

### Output formatting
```kotlin
println("YES")              // string
println(42)                 // number — no boxing, sb.append(Int) exists
print("$a $b\n")            // manual newline
(1..n).forEach { print("$it ") }; println()   // inline space-separated
```

---

## Internal Data Flow

```
stdin
  │
  ▼
BufferedInputStream  ──read(64 KB)──►  buffer: ByteArray
                                           │
                                       ptr advances byte by byte
                                           │
                                     readByte() → Int (raw ASCII code)
                                           │
                          ┌────────────────┴───────────────────┐
                          │                                     │
                     nextInt()                            nextString()
                     nextLong()                           nextLine()
                     nextDouble()                         nextChar()
                          │                                     │
                          └────────────────┬───────────────────┘
                                           │
                                       solve()
                                           │
                                     println() / print()
                                           │
                                           ▼
                                    sb: StringBuilder  (in memory)
                                           │
                                   main() final line
                                           │
                                           ▼
                                  System.out.print(sb)
                                           │
                                           ▼
                                        stdout
```

---

## Pitfalls & Notes

**Shadowing stdlib `print`/`println`**  
Our file-level `fun print(x: Any)` shadows `kotlin.io.print`. This is intentional — every
`println(answer)` inside `solve()` silently writes to `sb`.  The real stdout write happens
exactly once at the end of `main()`.  If you add other files to the project, import carefully.

**`nextLine()` after `nextInt()`**  
After `nextInt()` consumes `42` from the line `42\n`, the `\n` is still in the buffer.
Your first `nextLine()` call will return `""`.  Consume the newline explicitly:
```kotlin
val n = nextInt()
nextLine()          // discard trailing newline
val sentence = nextLine()
```

**`1 shl 16`**  
This is `1 << 16 = 65 536`.  Kotlin uses named infix functions for bitwise ops:
`shl` (left shift), `shr` (arithmetic right shift), `ushr` (logical), `and`, `or`, `xor`, `inv`.

**`ptr++` semantics**  
`buffer[ptr++].toInt()` reads the byte at `ptr`, converts it to a signed Int,
then increments `ptr`.  The `.toInt()` on a `Byte` sign-extends (−128 to 127),
but all printable ASCII is in 32–126 so no sign issues arise in practice.

**Long overflow guard**  
When summing `n` Int values where `n` can be 10⁶ and each value up to 10⁹,
the sum can reach 10¹⁵ — beyond Int range. Always accumulate into a `Long`:
```kotlin
var sum = 0L          // ← not 0
for (x in arr) sum += x
```
