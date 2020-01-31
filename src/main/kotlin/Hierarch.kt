import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Array.get

class Hierarch {

    @JsonClass(generateAdapter = true)
    data class Node(val label: String, val id: Int, val parentId: Int) {
        val hasParent = parentId != 0
        override fun toString(): String {
            return "$label ($id) (parent=$parentId)"
        }
    }

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val type = Types.newParameterizedType(List::class.java, Node::class.java)
    val jsonAdapter: JsonAdapter<List<Node>> = moshi.adapter(type)

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

}