import org.junit.jupiter.api.Assertions.assertEquals
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

        println(parents)

    }

}