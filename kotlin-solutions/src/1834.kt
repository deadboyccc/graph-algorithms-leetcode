import java.util.*

class Solution {
    data class Task(val arrivalTime: Int, val processingTime: Int, val originalIndex: Int)

    fun getOrder(tasks: Array<IntArray>): IntArray {
        val n = tasks.size
        val sortedTasks = tasks.mapIndexed { index, task ->
            Task(task[0], task[1], index)
        }.sortedBy { it.arrivalTime }

        val pq = PriorityQueue<Task>(
            compareBy<Task> { it.processingTime }.thenBy { it.originalIndex }
        )

        val result = IntArray(n)
        var resultIdx = 0
        var currTime = 0L
        val taskIterator = sortedTasks.listIterator()

        while (resultIdx < n) {
            // 1. Fill the PQ with all tasks that have arrived by now
            while (taskIterator.hasNext()) {
                val nextTask = taskIterator.next()
                if (nextTask.arrivalTime <= currTime) {
                    pq.add(nextTask)
                } else {
                    // Peek failed: it hasn't arrived yet.
                    // Put it back and stop adding for this time cycle.
                    taskIterator.previous()
                    break
                }
            }

            // 2. Decide what the CPU does next
            if (pq.isNotEmpty()) {
                // CPU is busy: process the best task in the heap
                val processedTask = pq.poll()
                result[resultIdx++] = processedTask.originalIndex
                currTime += processedTask.processingTime
            } else if (taskIterator.hasNext()) {
                // CPU is idle: no tasks arrived yet.
                // Jump time to the arrival of the very next task.
                currTime = taskIterator.next().arrivalTime.toLong()
                // Move back so the "while" loop at the top can add it to the PQ
                taskIterator.previous()
            }
        }
        return result
    }
}
