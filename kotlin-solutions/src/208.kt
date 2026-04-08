class TrieNode(
    var isEndOfWord: Boolean = false,
    val children: MutableMap<Char, TrieNode> = mutableMapOf()
)

class Trie() {
    val root = TrieNode()

    fun insert(word: String) {

        var currNode = root
        for (c in word) {
            currNode = currNode.children.getOrPut(c) { TrieNode() }
        }
        currNode.isEndOfWord = true
    }

    fun search(word: String): Boolean {
        var currNode = root
        for (c in word) {
            currNode = currNode.children[c] ?: return false
        }
        return currNode.isEndOfWord
    }

    fun startsWith(prefix: String): Boolean {
        var currNode = root
        for (c in prefix) {
            currNode = currNode.children[c] ?: return false
        }
        return true
    }
}
