import kotlin.random.Random

fun main() {
    LetsTryThis()
}

class LetsTryThis {

    val algorithmOne: () -> Int = {
        Random.nextInt(5)
    }

    val algorithmTwo: () -> Int = {
        2
    }

    fun produce(producer: () -> Int) {
        println(producer.invoke())
    }

    init {
        produce(algorithmOne)
    }


}