import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.Math.abs
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

fun main() {

    val scaleScope = CoroutineScope(Dispatchers.Default)
    val scale = JitterScale(scaleScope)
    val stabilizer = Stabilizer(weightFlow = scale.weightFlow, scope = scaleScope)

    GlobalScope.launch {

        while (true) {
            println("${System.currentTimeMillis()} : ${stabilizer.getAverage()}")
            delay(250)
        }

    }

    while (true) {

    }

}

data class ScaleWeight(val weight: BigDecimal, val timestamp: Long = System.currentTimeMillis())

abstract class Scale(val scope: CoroutineScope) {

    abstract val millisecondsBetweenReadings: Long

    internal val enablePublishWeights = AtomicBoolean(false)

    val weightFlow = MutableStateFlow(ScaleWeight(BigDecimal.ZERO))

    abstract fun start()
    abstract fun stop()
}

class JitterScale(
    scope: CoroutineScope,
    override val millisecondsBetweenReadings: Long = 250L
) : Scale(scope) {

    init {

        start()

        scope.launch {
            while (enablePublishWeights.get()) {
                weightFlow.value = readWeight()
                delay(millisecondsBetweenReadings)
            }
        }
    }

    override fun start() {
        enablePublishWeights.set(true)
    }

    override fun stop() {
        enablePublishWeights.set(false)
    }

    private fun readWeight(): ScaleWeight {
        // JitterScale returns weights between 0.00 and 0.01
        return ScaleWeight(
            weight = BigDecimal.valueOf(Random.nextInt(2) * 0.01).setScale(2, RoundingMode.HALF_UP)
        )
    }

}


class Stabilizer(
    private val thresholdMin: BigDecimal = BigDecimal.valueOf(0.02).setScale(2, RoundingMode.HALF_UP),
    private val numSamples: Int = 15,
    weightFlow: Flow<ScaleWeight>,
    scope: CoroutineScope
) {

    private var samples = MutableStateFlow<List<BigDecimal>>(mutableListOf())

    init {

        // Initialize the samples
        repeat(numSamples) {
            samples.value = samples.value.plus(BigDecimal.ZERO)
        }

        scope.launch {
            weightFlow.onEach { scaleWeight ->

                // Compare the new reading to the last reading
                val priorReading = samples.value.last()

                if (kotlin.math.abs(scaleWeight.weight.toFloat() - priorReading.toFloat()) > thresholdMin.toFloat()) {
                    samples.value = samples.value.plus(scaleWeight.weight).takeLast(numSamples).toMutableList()
                }

            }.launchIn(this)
        }
    }

    fun getAverage(): BigDecimal {
        return BigDecimal.valueOf(samples.value.map { it.toFloat() }.average()).setScale(2, RoundingMode.HALF_UP)
    }

}