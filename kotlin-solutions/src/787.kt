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



class Solution2 {

    fun findCheapestPrice(
        numberOfCities: Int,
        flights: Array<IntArray>,
        sourceCity: Int,
        destinationCity: Int,
        maxStops: Int
    ): Int {

        val unreachableCost = Int.MAX_VALUE

        var minimumCostToCity =
            IntArray(numberOfCities) { unreachableCost }
                .apply { this[sourceCity] = 0 }

        repeat(maxStops + 1) {

            val updatedCostToCity = minimumCostToCity.copyOf()

            for ((departureCity, arrivalCity, flightPrice) in flights) {

                val costToDeparture = minimumCostToCity[departureCity]
                if (costToDeparture == unreachableCost) continue

                val newCost = costToDeparture + flightPrice

                updatedCostToCity[arrivalCity] =
                    minOf(updatedCostToCity[arrivalCity], newCost)
            }

            minimumCostToCity = updatedCostToCity
        }

        val cheapestPrice = minimumCostToCity[destinationCity]

        return if (cheapestPrice == unreachableCost) -1 else cheapestPrice
    }
}


class SolutionFP {

    fun findCheapestPrice(
        numberOfCities: Int,
        flights: Array<IntArray>,
        sourceCity: Int,
        destinationCity: Int,
        maxStops: Int
    ): Int {

        val unreachableCost = Int.MAX_VALUE

        var minimumCostToCity =
            IntArray(numberOfCities) { unreachableCost }
                .apply { this[sourceCity] = 0 }

        repeat(maxStops + 1) {

            val updatedCostToCity = minimumCostToCity.copyOf()

            flights.forEach { (departureCity, arrivalCity, flightPrice) ->

                minimumCostToCity[departureCity]
                    .takeIf { it != unreachableCost }
                    ?.let { costToDeparture ->

                        val newCost = costToDeparture + flightPrice

                        updatedCostToCity[arrivalCity] =
                            minOf(updatedCostToCity[arrivalCity], newCost)
                    }
            }

            minimumCostToCity = updatedCostToCity
        }

        return minimumCostToCity[destinationCity]
            .takeIf { it != unreachableCost }
            ?: -1
    }
}
