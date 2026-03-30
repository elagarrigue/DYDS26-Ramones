import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class TestExample {

    @OptIn(ExperimentalCoroutinesApi::class)
    val testScope = CoroutineScope(UnconfinedTestDispatcher())

    interface MyService {
        fun getData(): String
        fun sideEffect()
    }

    class MyViewModel(private val myService: MyService) : ViewModel() {
        val dataFlow = MutableStateFlow("")

        fun getData(): String {
            myService.sideEffect()
            return myService.getData()
        }
    }

    class MyServiceFake : MyService {
        var isSideEffectCalled = false

        override fun getData(): String {
            return "Data"
        }

        override fun sideEffect() {
            isSideEffectCalled = true
        }
    }

    @Test
    fun `get data should return data and side effect should be triggered`() {
        // arrange
        val myService = MyServiceFake()
        val myViewModel = MyViewModel(myService)

        // act
        val result = myViewModel.getData()

        // assert
        assert(result == "Data")
        assert(myService.isSideEffectCalled)
    }

    @Test
    fun `data flow should emit string events`() = runTest {
        val myService = MyServiceFake()
        val myViewModel = MyViewModel(myService)

        val events: ArrayList<String> = arrayListOf()
        testScope.launch {
            myViewModel.dataFlow.collect { events.add(it) }
        }

        // act
        myViewModel.dataFlow.emit("value")
        myViewModel.dataFlow.emit("value2")

        // assert
        assert(events.last() == "value2")
    }
}