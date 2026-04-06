package one21

class Soltuion {
    fun maxProfit(prices: IntArray): Int {
        if (prices.isEmpty()) return 0

        var minPrice = prices[0]
        var maxProfit = 0

        for (i in 1 until prices.size) {
            val profit = prices[i] - minPrice
            maxProfit = maxOf(maxProfit, profit)
            minPrice = minOf(minPrice, prices[i])
        }

        return maxProfit
    }
}

class Solution {
    data class StockPrice(var price: Int, var index: Int) {}

    fun maxProfit(prices: IntArray): Int {
        if (prices.isEmpty()) return 0

        val priceList = prices.toList()
        var maxProfit = 0

        for ((left, right) in (0 until priceList.size / 2).zip((priceList.size - 1) downTo (priceList.size / 2))) {
            // cool trick to do two pointers with zip
            println("left $left - - - Right  $right")
        }

        return maxProfit
    }
}

fun main() {
    Solution().maxProfit(intArrayOf(7, 1, 5, 3, 6, 4)).also { println(it) }
}

/*
package one21

import kotlin.math.max

class Solution {
    data class StockPrice(var price: Int, var index: Int) {}

    fun maxProfit(prices: IntArray): Int {
        if (prices.isEmpty()) return 0

        var left = 0
        var right = prices.size - 1
        var maxProfit = 0

        while (left < right) {
            val profit = prices[right] - prices[left]
            maxProfit = maxOf(maxProfit, profit)
            if (prices[left] > prices[right]) left++ else right--
        }

        return maxProfit
    }
}

fun main() {
    Solution().maxProfit(intArrayOf(7, 1, 5, 3, 6, 4)).also { println(it) }
}
 */