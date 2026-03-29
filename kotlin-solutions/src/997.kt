package nine97

class Solution {
    fun findJudge(n: Int, trust: Array<IntArray>): Int {
        val trustScores = IntArray(n + 1)

        for (relationship in trust) {
            val truer = relationship[0]
            val trusted = relationship[1]

            trustScores[truer]--   // Rule 1: Judge trusts no one
            trustScores[trusted]++  // Rule 2: Everyone trusts the judge
        }

        for (i in 1..n) {
            if (trustScores[i] == n - 1) {
                return i
            }
        }

        return -1
    }
}
