package misc.bfsDemo;

/**
 * Idiomatic generation of the Adjacency Map using functional builders.
 */
fun main() {
    val adj = generateLayeredGraph()
    val graph = mapOf(
        'A' to listOf('B', 'C'),
        'B' to listOf('D'),
        'C' to listOf('E'),
        'D' to listOf('F'),
        'E' to listOf(),
        'F' to listOf()
    )

    /*
           ┌───► B ───► D ───► F
           │
     A ────┤
           │
           └───► C ───► E
     */
    // bfs
    println(bfs(graph, 'A', 'F')) // Output: true
    println(bfs(graph, 'A', 'Z')) // Output: false

    // level order bfs
    println("_".repeat(10))
    orderLevelBfs(adj, 'A')

    // dfs
    println("_".repeat(10))
    dfs(graph, 'A')

}

fun orderLevelBfs(adj: Map<Char, List<Char>>, src: Char) {
    val queue = ArrayDeque<Char>().apply { addLast(src) }
    val visited = mutableSetOf<Char>()

    visited.add(src) // Mark src visited immediately

    var currLevel = 0

    while (queue.isNotEmpty()) {
        val nodesAtThisLevel = queue.size
        val currentLevelList = mutableListOf<Char>()

        // Process exactly the number of nodes present at the start of this level
        repeat(nodesAtThisLevel) {
            val current = queue.removeFirst()
            currentLevelList.add(current)

            // Add all unvisited neighbors to the queue for the NEXT level
            adj[current]?.forEach { neighbor ->
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    queue.addLast(neighbor)
                }
            }
        }

        println("Level $currLevel : $currentLevelList")
        currLevel++

    }
}

fun dfsRecursive(
    adj: Map<Char, List<Char>>,
    curr: Char,
    visited: MutableSet<Char> = mutableSetOf(),
    res: MutableList<Char> = mutableListOf()
): List<Char> {
    if (curr in visited) return res

    visited.add(curr)
    res.add(curr)

    adj[curr]?.forEach { neighbor ->
        dfsRecursive(adj, neighbor, visited, res)
    }

    return res
}

fun dfs(adj: Map<Char, List<Char>>, src: Char) {
    val stack = ArrayDeque<Char>().apply { addLast(src) }
    val visited = mutableSetOf<Char>()
    val res = mutableListOf<Char>()

    while (stack.isNotEmpty()) {
        val currChar = stack.removeLast()


        if (visited.contains(currChar)) continue
        visited.add(currChar)
        res.add(currChar)

        adj[currChar]?.asReversed()?.forEach { neighbor ->
            if (neighbor !in visited) stack.add(neighbor)
        }


    }
    println("Res: $res")


}

fun bfs(adj: Map<Char, List<Char>>, src: Char, dst: Char): Boolean {
    val queue = ArrayDeque<Char>().apply { add(src) }
    val visited = hashSetOf<Char>()
    while (queue.isNotEmpty()) {
        val currChar = queue.removeFirst()

        // if it's the dst = return true
        if (currChar == dst) return true

        // if visited continue
        if (visited.contains(currChar)) continue
        // else add it to visited as we are going to process it now
        visited.add(currChar)


        // add its neighbors

        adj.get(currChar)?.let {
            it.forEach {
                if (visited.contains(it)) return@forEach
                queue.add(it)
            }
        }


    }
    return false


}

fun generateLayeredGraph(): Map<Char, List<Char>> = mutableMapOf<Char, MutableList<Char>>().apply {
    fun addEdge(u: Char, v: Char) {
        this.getOrPut(u) { mutableListOf() }.add(v)
        this.getOrPut(v) { mutableListOf() }.add(u)
    }

    // Creating 5 distinct layers of characters
    val layers = listOf('A'..'E', 'F'..'J', 'K'..'O', 'P'..'T', 'U'..'Z').map { it.toList() }

    // Connect layer i to layer i+1
    for (i in 0 until layers.size - 1) {
        val currentLayer = layers[i]
        val nextLayer = layers[i + 1]
        for (u in currentLayer) {
            for (v in nextLayer) {
                // Determine connections based on Char codes to create a consistent web
                if ((u.code + v.code) % 2 == 0) addEdge(u, v)
                if ((u.code * v.code) % 3 == 0) addEdge(u, v)
            }
        }
    }

    // Strategic Bridges to test race conditions and non-linear meeting points
    addEdge('A', 'K') // Forward search can jump deep into the middle early
    addEdge('M', 'Z') // Backward search can jump deep into the middle early

}.mapValues { it.value.distinct().toList() }
