package two073

class Solution {

    fun timeRequiredToBuy(tickets: IntArray, k: Int): Int {
        // Queue stores (ticket_count, original_person_index) pairs.
        // We track index to identify when person k finishes buying.
        val queue = ArrayDeque<Pair<Int, Int>>()

        // 1. Initialize queue with all people and their ticket counts
        tickets.forEachIndexed { index, count -> queue.add(count to index) }

        var seconds = 0

        // 2. Simulate the buying process
        while (queue.isNotEmpty()) {
            seconds++
            val (count, index) = queue.removeFirst()
            val remaining = count - 1

            // 3. If person k just bought their last ticket, we're done
            if (remaining == 0 && index == k) return seconds

            // 4. If person still needs tickets, rejoin the back of the queue
            if (remaining > 0) {
                queue.add(remaining to index)
            }
        }

        return seconds
    }
}

fun main() {
    val tickets = intArrayOf(2, 3, 2)
    val k = 2
    println("Total time: ${Solution().timeRequiredToBuy(tickets, k)}") // Expected: 6

}

class Solution2 {
    fun timeRequiredToBuy(tickets: IntArray, k: Int): Int {
        val target = tickets[k]
        return tickets.withIndex().sumOf { (i, t) ->
            if (i <= k) minOf(t, target) else minOf(t, target - 1)
        }
    }
}
