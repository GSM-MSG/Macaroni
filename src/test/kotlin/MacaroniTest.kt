import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

suspend fun main() {
    val macaroni = Macaroni(
        onRemoteObservable = {
            flow {
                delay(100)
                emit(TestStatus("onRemoteObservable", "test"))
            }
        },
        onLocalObservable = {
            flow {
                delay(200)
                emit(TestStatus("onLocalObservable", "test"))
            }
        },
        onUpdateLocal = {
            println("onUpdateLocal")
        },
        getLocalData = { TestStatus(name = "getLocalData", test = "test") }
    )

    macaroni.fetch { status, testStatus ->
        println("status: $status, testStatus: $testStatus")
    }
}