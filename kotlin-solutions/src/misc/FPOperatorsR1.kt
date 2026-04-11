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

}

fun addSeperator(length: Int = 15) = println("_".repeat(length))