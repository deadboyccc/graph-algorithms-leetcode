package one768

class Solution {
    fun mergeAlternately(word1: String, word2: String): String {
        val firstIterator = word1.iterator()
        val secondIterator = word2.iterator()

        val length = minOf(word1.length, word2.length)
        val res = StringBuilder()

        for (i in 0 until length) {
            res.append(firstIterator.next())
            res.append(secondIterator.next())
        }

        while (firstIterator.hasNext()) {
            res.append(firstIterator.next())
        }

        while (secondIterator.hasNext()) {
            res.append(secondIterator.next())
        }


        return res.toString()


    }
}