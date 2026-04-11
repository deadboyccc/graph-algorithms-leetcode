package one441;

class Solution {
    fun buildArray(target: IntArray, n: Int): List<String> {
        var index = 0
        val list = mutableListOf<String>()


        for (streamInt in 1..n) {
            if (index == target.size) break

            if (target[index] == streamInt) {
                index++
                list.add("Push")
            } else {
                list.add("Push")
                list.add("Pop")
            }
        }

        return list
    }
}

class SolutionFP {
    fun buildArray(target: IntArray, n: Int): List<String> {
        val targetSet = target.toHashSet()
        return (1..target.last())
            .flatMap { streamInt ->
                if (streamInt in targetSet) listOf("Push")
                else listOf("Push", "Pop")
            }
    }
}