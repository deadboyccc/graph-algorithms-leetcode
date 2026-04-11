package misc

// review 1441, 28 and 150 FP ops and solutions
fun main() {
    // better lookup if unsorted
    addSeperator()

    val hashSet = intArrayOf(10, 20, 30, 40, 50, 60, 70).toHashSet()
    hashSet.contains(60).also { println(it) }

    addSeperator()

    listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10).indexOfFirst { it > 9 }
        .also { println(it) }

    addSeperator()

    listOf("String0", "String1", "String2", "String3", "String4", "5longAString5").indexOfFirst { it.length >= 8 }
        .also { println(it) }

    addSeperator()

    val res = (0..3).flatMap { num ->
        if (num % 2 == 0) listOf("Even", "Number")
        else listOf("Odd", "Number")
    }.also { println(it) }

    addSeperator()
    val isPrimeList = (0..3).associate { num -> Pair(num, num * num) }.flatMap { (num, numSquare) ->
        if (isPrime(num + numSquare)) listOf(11, num, numSquare, 11)
        else listOf(0, num, numSquare, 0)
    }.also { println(it) }


}

fun isPrime(n: Int): Boolean {
    if (n < 2) return false
    if (n == 2) return true
    if (n % 2 == 0) return false

    var divisor = 3
    while (divisor * divisor <= n) {
        if (n % divisor == 0) return false
        divisor += 2
    }
    return true
}

fun addSeperator(length: Int = 15) = println("_".repeat(length))