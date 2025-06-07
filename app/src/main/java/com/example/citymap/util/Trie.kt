package com.example.citymap.util


data class TrieNode (
    val children: MutableMap<Char, TrieNode> = mutableMapOf(),
    var isEndOfWord : Boolean = false
)

class Trie {
    private val root = TrieNode()

    fun insert(name:String) {
        var currentNode = root
        for (char in name) {
            currentNode = currentNode.children.getOrPut(char) { TrieNode() }
        }
        currentNode.isEndOfWord = true
    }

    fun searchByPrefix(prefix: String): List<String> {
        val results = mutableListOf<String>()
        var currentNode = root

        for (char in prefix) {
            currentNode = currentNode.children[char] ?: return emptyList()
        }

        findAllWordsFromNode(currentNode, StringBuilder(prefix), results)
        return results
    }

    private fun findAllWordsFromNode(
        node: TrieNode,
        prefix: StringBuilder,
        results: MutableList<String>
    ) {
        if (node.isEndOfWord) {
            results.add(prefix.toString())
        }
        for ((char, childNode) in node.children) {
            prefix.append(char)
            findAllWordsFromNode(childNode,prefix, results)
            prefix.removeSuffix(char.toString())
        }
    }
}