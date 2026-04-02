import java.util.*

class Solution {
    // Data class to preserve the original index after sorting
    data class Task(val arrivalTime: Int, val processingTime: Int, val originalIndex: Int)

    fun getOrder(tasks: Array<IntArray>): IntArray {
        val n = tasks.size

        // 1. Map to Task objects and sort by arrivalTime to process them chronologically
        val sortedTasks = tasks.mapIndexed { index, task ->
            Task(task[0], task[1], index)
        }.sortedBy { it.arrivalTime }

        // 2. Min-Heap for "available" tasks
        // Primary: shortest processing time | Secondary: smallest original index
        val pq = PriorityQueue<Task>(
            compareBy<Task> { it.processingTime }.thenBy { it.originalIndex }
        )

        val result = IntArray(n)
        var resultIndex = 0
        var taskPointer = 0
        var currentTime = 0L // Use Long to avoid overflow from large processing times

        // TODO: Implement the while loop to manage currentTime and the PQ

        return result
    }
}
