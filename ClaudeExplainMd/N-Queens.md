# N-Queens Kotlin Solution - Line by Line

## Function Signature

```kotlin
fun solveNQueens(n: Int): List<List<String>>
```

- Takes `n` (board size) as input
- Returns a list of all valid solutions, where each solution is a list of board rows as strings

---

## Data Structures (Setup)

```kotlin
val col = mutableSetOf<Int>()
```

- Tracks which columns have queens placed
- Example: if a queen is at column 2, `col` contains `2`
- Why set? O(1) lookup to check if column occupied

```kotlin
val posDiag = mutableSetOf<Int>()
```

- Tracks positive diagonals (`/` direction)
- Formula: `row + col` is constant on same positive diagonal
- Example: positions (0,1), (1,2), (2,3) all have `r+c = 1, 2, 3`

```kotlin
val negDiag = mutableSetOf<Int>()
```

- Tracks negative diagonals (`\` direction)
- Formula: `row - col` is constant on same negative diagonal
- Example: positions (0,2), (1,1), (2,0) all have `r-c = 2, 0, -2`

```kotlin
val res = mutableListOf<List<String>>()
```

- Accumulates all valid solutions found

```kotlin
val board = Array(n) { CharArray(n) { '.' } }
```

- Creates n×n board filled with dots
- `Array(n) { ... }` creates outer array with `n` rows
- Each row: `CharArray(n) { '.' }` creates `n` characters initialized to `'.'`
- Result: 4×4 example:
  ```
  . . . .
  . . . .
  . . . .
  . . . .
  ```

---

## Backtracking Function Definition

```kotlin
fun backtrack(r: Int) {
```

- Nested function defined inside `solveNQueens`
- `r` = current row being filled
- Has access to all outer variables: `col`, `posDiag`, `negDiag`, `res`, `board`, `n`

---

## Base Case (Found Solution)

```kotlin
if (r == n) {
    res.add(board.map { it.joinToString("") })
    return
}
```

- When `r == n`, we've placed all n queens (filled all rows)
- `board.map { ... }` transforms:
    - Input: Array of CharArrays
    - `it` = each CharArray (row)
    - `it.joinToString("")` = join chars into single string "Q..Q"
    - Output: List<String> where each string is a board row
- `res.add(...)` stores this complete solution
- `return` exits this branch of recursion

Example for n=4 with one solution:

```
Input board:  [Q . . .]  →  "Q..Q"
              [. . Q .]     "..Q."
              [. Q . .]     ".Q.."
              [. . . Q]     "...Q"

res becomes:
[[
  "Q..Q",
  "..Q.",
  ".Q..",
  "...Q"
]]
```

---

## Try Each Column in Current Row

```kotlin
for (c in 0 until n) {
```

- Loop through columns 0 to n-1 in row `r`
- `0 until n` is Kotlin range (excludes n, like Python's `range(n)`)

---

## Conflict Check

```kotlin
if (c in col || (r + c) in posDiag || (r - c) in negDiag) {
    continue
}
```

- Check three attack vectors simultaneously:
    1. `c in col`: Is column `c` already occupied?
    2. `(r + c) in posDiag`: Is positive diagonal occupied?
    3. `(r - c) in negDiag`: Is negative diagonal occupied?
- `in` operator checks set membership (O(1))
- If ANY conflict exists, `continue` skips to next column
- Only proceed if ALL three are safe (none occupied)

Example for n=4, placing queen at (2,1):

- `c = 1`: check column 1
- `r + c = 2 + 1 = 3`: check positive diagonal 3
- `r - c = 2 - 1 = 1`: check negative diagonal 1
- If none in their respective sets → safe, proceed

---

## Place Queen

```kotlin
col.add(c)
posDiag.add(r + c)
negDiag.add(r - c)
board[r][c] = 'Q'
```

- Four atomic operations:
    1. Mark column `c` as occupied
    2. Mark positive diagonal as occupied
    3. Mark negative diagonal as occupied
    4. Place 'Q' character on board at position `[r][c]`

After placing at (2,1):

- `col = {1}`
- `posDiag = {3}`
- `negDiag = {1}`
- `board[2][1] = 'Q'`

---

## Recursive Call (Go Deeper)

```kotlin
backtrack(r + 1)
```

- Move to next row (r+1)
- Recursion explores all valid placements in remaining rows
- Call stack grows: `backtrack(0)` → `backtrack(1)` → `backtrack(2)` → ... → `backtrack(n)` (base case)

---

## Undo Changes (Backtrack)

```kotlin
col.remove(c)
posDiag.remove(r + c)
negDiag.remove(r - c)
board[r][c] = '.'
```

- Four operations that undo the placement:
    1. Un-mark column
    2. Un-mark positive diagonal
    3. Un-mark negative diagonal
    4. Erase queen from board
- Restores state so loop can try next column `c+1`
- This is the "backtrack" step — allows exploring alternate paths

Timeline for one iteration:

```
Before loop: board = [. . . .]
             col = {}

Iter c=0: Place Q  → board=[Q . . .], col={0}
          Recurse    → explore from row 1
          Remove Q   → board=[. . . .], col={}

Iter c=1: Place Q  → board=[. Q . .], col={1}
          Recurse    → explore from row 1
          Remove Q   → board=[. . . .], col={}

... and so on
```

---

## Main Function

```kotlin
fun main() {
    val n = 4
    val result = solveNQueens(n)
    result.forEach { solution ->
        solution.forEach { println(it) }
        println()
    }
    println("Total solutions: ${result.size}")
}
```

- `n = 4`: solve 4-Queens
- `solveNQueens(4)`: calls solver, returns list of solutions
- Outer `forEach`: iterate each solution
- Inner `forEach`: print each row of that solution
- `println()`: blank line between solutions
- Final line: prints count of solutions (2 for 4-Queens)

Output for 4-Queens:

```
Q..Q
..Q.
.Q..
...Q

.Q..
...Q
Q..Q
..Q.

Total solutions: 2
```

---

## Execution Flow (Condensed)

For n=2, which has 0 solutions:

```
backtrack(0)
  └─ c=0: place Q at (0,0)
  │  backtrack(1)
  │    ├─ c=0: conflict (col 0)
  │    ├─ c=1: conflict (diag)
  │    └─ return (no row 2 reached)
  │  remove Q at (0,0)
  └─ c=1: place Q at (0,1)
     backtrack(1)
       ├─ c=0: conflict (diag)
       ├─ c=1: conflict (col 1)
       └─ return (no row 2 reached)
     remove Q at (0,1)
  result: [] (empty)
```

---

## Algorithm Summary

| Phase        | Action                                  |
|--------------|-----------------------------------------|
| **Try**      | For each column in current row          |
| **Check**    | Verify no conflicts (col, diag+, diag-) |
| **Place**    | Add queen, update tracking sets         |
| **Explore**  | Recurse to next row                     |
| **Found?**   | If all n rows filled, store solution    |
| **Undo**     | Remove queen, restore state             |
| **Try Next** | Loop continues with next column         |

Time: O(N!), Space: O(N) for recursion depth + storage
