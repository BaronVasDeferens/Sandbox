import org.junit.jupiter.api.Test

class HierarchyTest {

    @Test
    fun basicIOTest() {
        val hier = Hierarch()
        val data = hier.readData()
        assert(data.isNotEmpty())
    }

    @Test
    fun parentTest() {
        val hier = Hierarch()
        val data = hier.readData()
        val parents = hier.establishParents(data)
        assert(parents.isNotEmpty())
        assert(parents.getValue(50).size == 1)
        assert(parents.getValue(100).size == 2)
        assert(parents.getValue(200).size == 2)
    }

    @Test
    fun finalTest() {
        val hier = Hierarch()
        val data = hier.readData()
        val parents = hier.establishParents(data)

        val finalHier = data.filterNot { it.hasParent }
            .map {
                hier.depthFirst(it, parents)
            }.toList()

        finalHier.forEach { println(it.prettyPrint()) }

        assert(finalHier.size == 1)
    }
}