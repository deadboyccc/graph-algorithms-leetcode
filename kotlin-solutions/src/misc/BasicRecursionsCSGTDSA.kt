package misc

// count down fun
fun countdown(v: Int) {
    if (v == 0) return
    print("$v ")
    countdown(v - 1)

}

// passing params
fun doubleArrInPlace(arr: IntArray, index: Int = 0) {
    if (index >= arr.size - 1) return

    arr[index] *= 2

    doubleArrInPlace(arr, index + 1)

}

// in the top down we start from the top ( pause in the stack ) calculate down then pops back up
// in the bottom up, we calculate from the bot and add up to the sum of the problem

// factorial example top down ( break the problem into mini problems and calculations start from down upward)
fun factorial(n: Int): Int {
    return when (n) {
        0 -> 1
        else -> n * factorial(n - 1)
    }

}

// bottom  up ( start form 1 then *2 then *3) start from the bottom and calculation is done in the 1st step
tailrec fun factorial(n: Int, i: Int = 1, product: Long = 1L): Long {
    return if (i > n) {
        product
    } else {
        factorial(n, i + 1, product * i)
    }
}

fun arrSum(arr: IntArray): Long = when {
    arr.isEmpty() -> 0L
    else -> arr[0].toLong() + arrSum(arr.sliceArray(1..arr.lastIndex))
}

// fact(1) -> fact(2) - > fact(3) -> fact(4) state is in the params so the final fact(4) is the tail and it holds hte result
// tail recursion
// string reversal
fun stringReversal(s: String): String = when {
    s.isEmpty() -> ""
    else -> s.last() + stringReversal(s.substring(0, s.lastIndex))
}


// rev string reversal -> counting X

fun countFrequencyOfX(s: String): Int = when {
    // if the string is empty, return  0
    s.isEmpty() -> 0
    // If the first char is 'x', count it (1) AND keep searching the rest
    s[0] == 'x' -> 1 + countFrequencyOfX(s.substring(1))
    // Otherwise, just keep searching the rest
    else -> countFrequencyOfX(s.substring(1))
}

// zip i j loop
fun tryZipWithTwo() {
    for ((i, j) in (1..10).zip(1..20 step 2)) {
        println("$i -> $j")
    }
}

// fun staircase problem (3 steps - 1step+ 2 steps + 3 steps )
// given n steps return number of permutations  ways to climb the stairs
fun stairs(n: Int): Int {
    // Base Cases
    if (n < 0) return 0   // You can't climb negative steps
    if (n == 0) return 1  // There is 1 way to stay at the bottom (doing nothing)
    if (n == 1) return 1  // Only 1 way: [1]
    if (n == 2) return 2  // 2 ways: [1,1], [2]

    // Recursive Step
    // To reach step n, you could have come from n-1, n-2, or n-3
    return stairs(n - 1) + stairs(n - 2) + stairs(n - 3)
}

/**
 * Calculates the number of ways to climb a staircase of [n] steps.
 * * This implementation uses Top-Down Dynamic Programming (Memoization).
 * Each step can be reached by taking 1, 2, or 3 steps at a time.
 * * Time Complexity: O(n) - Each step is calculated once.
 * Space Complexity: O(n) - Stores results in a map and uses the call stack.
 */
class StaircaseCalculator {
    // Memoization cache to store results of previously calculated steps
    private val memo = mutableMapOf<Int, Long>()

    fun countWays(n: Int): Long {
        // Base Case: Only 1 way to reach the ground (by doing nothing)
        if (n == 0) return 1L

        // Negative steps are impossible to reach
        if (n < 0) return 0L

        // Base Case: Small optimizations for 1 and 2 steps
        if (n <= 2) return n.toLong()

        // Check if the value is already in our "Short-term memory"
        // This prevents the O(3^n) exponential explosion
        return memo.getOrPut(n) {
            countWays(n - 1) + countWays(n - 2) + countWays(n - 3)
        }
    }
}

fun stairsOptimized(n: Int): Int {
    if (n < 0) return 0
    if (n == 0) return 1
    if (n <= 2) return n
    if (n == 3) return 4

    var a = 1 // ways to reach n-3
    var b = 2 // ways to reach n-2
    var c = 4 // ways to reach n-1
    var current = 0

    for (i in 4..n) {
        current = a + b + c
        a = b
        b = c
        c = current
    }

    return c
}

/**
 * Calculates the total number of unique ways to climb a staircase of [totalSteps].
 * You can jump 1, 2, or 3 steps at a time.
 * * This uses "Bottom-Up" Dynamic Programming with O(1) space.
 */
fun countStaircasePermutations(totalSteps: Int): Long {
    // 1. Handle edge cases where no calculation is possible
    if (totalSteps < 0) return 0L
    if (totalSteps == 0) return 1L // 1 way: stay at the start
    if (totalSteps == 1) return 1L // 1 way: [1]
    if (totalSteps == 2) return 2L // 2 ways: [1,1], [2]
    if (totalSteps == 3) return 4L // 4 ways: [1,1,1], [1,2], [2,1], [3]

    // 2. Descriptive State Variables
    // These represent the number of ways to reach the 3 steps immediately behind us.
    var waysToReachThreeStepsBack = 1L // Result for step (i-3)
    var waysToReachTwoStepsBack = 2L   // Result for step (i-2)
    var waysToReachOneStepBack = 4L    // Result for step (i-1)

    var currentStepWays = 0L

    // 3. The Iteration
    // We start calculating from step 4 up to totalSteps.
    for (currentStep in 4..totalSteps) {
        // The number of ways to reach the CURRENT step is the SUM of the
        // ways to reach the 3 steps we could have jumped from.
        currentStepWays = waysToReachThreeStepsBack +
                waysToReachTwoStepsBack +
                waysToReachOneStepBack

        // 4. "Slide" the variables forward to prepare for the next step.
        // What was "2 steps back" now becomes "3 steps back" for the next iteration.
        waysToReachThreeStepsBack = waysToReachTwoStepsBack
        waysToReachTwoStepsBack = waysToReachOneStepBack
        waysToReachOneStepBack = currentStepWays
    }

    return waysToReachOneStepBack
}

// Anagram generation
fun main() {
//    countdown(5)
//    val arr = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
//    println(arr.contentToString())
//    doubleArrInPlace(arr)
//    println(arr.contentToString())
//    factorial(5).also { println(it) }

//    stringReversal("ABCPDF").also { println(it) }
//    countFrequencyOfX("textxoxux").also { println(it) }
    tryZipWithTwo()
}
