import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

        println(finalHier)

        assertTrue(finalHier.isNotEmpty())
    }
}