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

fun main() {
//    countdown(5)
//    val arr = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
//    println(arr.contentToString())
//    doubleArrInPlace(arr)
//    println(arr.contentToString())
//    factorial(5).also { println(it) }

    stringReversal("ABCPDF").also { println(it) }
}
