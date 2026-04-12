package one091

class Solution {
    fun shortestPathBinaryMatrix(grid: Array<IntArray>): Int {
        if (grid[0][0] == 1) return -1

        val directions = arrayOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)
        val n = grid.size

        val queue = ArrayDeque<Triple<Int, Int, Int>>()
        val visited = hashSetOf<Pair<Int, Int>>()

        queue.add(Triple(0, 0, 1))
        visited.add(0 to 0)

        while (queue.isNotEmpty()) {
            val (r, c, pathLength) = queue.removeFirst()

            if (r == n - 1 && c == n - 1) return pathLength

            for ((dr, dc) in directions) {
                val nr = r + dr
                val nc = c + dc
                // guard: bounds + obstacle + visited
                if (minOf(nr, nc) < 0 || maxOf(nr, nc) >= n
                    || grid[nr][nc] == 1
                    || (nr to nc) in visited
                )
                    continue
                visited.add(nr to nc)
                queue.add(Triple(nr, nc, pathLength + 1))
            }
        }
        return -1
    }
}
