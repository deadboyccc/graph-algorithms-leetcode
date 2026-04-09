package seven09;


class Solution {
    fun toLowerCase(s: String): String {
        return s.asSequence().map { it.lowercase() }.joinToString(separator = "")

    }
}