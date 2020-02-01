import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.forEach
import kotlin.collections.getValue
import kotlin.collections.map
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.collections.set

class Hierarch {

    @JsonClass(generateAdapter = true)
    data class Node(val label: String, val id: Int, val parentId: Int) {
        val hasParent = (parentId != 0)
        override fun toString(): String {
            return "$label ($id) (parent=$parentId)"
        }
    }

    data class ReferenceItem(
        val label: String,
        val id: Int,
        val parentId: Int,
        val children: MutableList<ReferenceItem> = mutableListOf()
    ) {
        fun prettyPrint(nest: Int = 0): String {
            var tabs = ""
            for (i in 0..nest) {
                tabs += "\t"
            }
            return tabs + "$label ($id)" + "\n" + children.map { it.prettyPrint(nest + 1) } + "\n"
        }
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val type = Types.newParameterizedType(List::class.java, Node::class.java)
    private val jsonAdapter: JsonAdapter<List<Node>> = moshi.adapter(type)

    fun readData(): List<Node> {
        val dataIn = javaClass.classLoader.getResourceAsStream("hierarchy.json")
        val reader = dataIn.bufferedReader()
        val data = reader.readText()
        reader.close()
        dataIn.close()

        val nodes: List<Node>? = jsonAdapter.fromJson(data)
        return nodes!!
    }

    fun establishParents(nodes: List<Node>): Map<Int, Set<Node>> {

        val parentMap = mutableMapOf<Int, MutableSet<Node>>()
        nodes.forEach { node ->
            if (parentMap[node.id] == null) {
                parentMap[node.id] = mutableSetOf()
            }
            if (node.hasParent && parentMap[node.parentId] == null) {
                parentMap[node.parentId] = mutableSetOf()
            }

            if (node.hasParent) {
                parentMap[node.parentId]!!.add(node)
            }
        }

        return parentMap
    }

    fun depthFirst(node: Node, map: Map<Int, Set<Node>>): ReferenceItem {
        val item = ReferenceItem(node.label, node.id, node.parentId)

        map.getValue(node.id).forEach { child ->
            item.children.add(depthFirst(child, map))
        }

        return item
    }

}