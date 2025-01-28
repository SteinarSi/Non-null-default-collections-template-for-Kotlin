import no.kantega.generated.example.ExampleDataClass
import no.kantega.generated.example.mutable.MutableExampleDataClass
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Tests {
    private val example = ExampleDataClass(
        requiredList = listOf("hello"),
        requiredMap = emptyMap(),
    )

    @Test
    fun optionalListIsEmptyList() {
        assertEquals(emptyList<String>(), example.optionalList)
    }

    @Test
    fun optionalMapIsEmptyMap() {
        assertEquals(emptyMap<String,String>(), example.optionalMap)
    }

    @Test
    fun canHaveDefaultValue() {
        assertEquals(listOf("hello", "world"), example.optionalListWithDefault)
    }

    @Test
    fun requiredListWorksAsUsual() {
        assertEquals(listOf("hello"), example.requiredList)
    }

    private var mutable = MutableExampleDataClass(
        requiredList = mutableListOf("hello"),
        requiredMap = mutableMapOf(),
    )

    @Test
    fun canMutateDataClass() {
        mutable.optionalList.add("hello")
        mutable.optionalMap["hello"] = "world"
    }
}
