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
        onUpdateLocal = {
            println("onUpdateLocal")
        },
        getLocalData = { TestStatus(name = "getLocalData", test = "test") },
        onRemoteFailure = { println("it") }
    )

    macaroni.fetch { status, result ->
        println("status: $status, result: $result")
    }
}