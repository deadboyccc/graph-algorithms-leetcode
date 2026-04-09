package seven87

class Solution {
    fun findCheapestPrice(n: Int, flights: Array<IntArray>, src: Int, dst: Int, k: Int): Int {
        var prices = IntArray(n) { Int.MAX_VALUE }
        prices[src] = 0

        repeat(k + 1) {
            val temp = prices.copyOf()
            flights.forEach { (from, to, price) ->
                if (prices[from] != Int.MAX_VALUE) {
                    temp[to] = minOf(temp[to], prices[from] + price)
                }
            }
            prices = temp
        }

        return if (prices[dst] == Int.MAX_VALUE) -1 else prices[dst]
    }
}
