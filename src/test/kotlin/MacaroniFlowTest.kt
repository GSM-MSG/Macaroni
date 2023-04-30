import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

private suspend fun main() {
    val macaroni = MacaroniFlow(
        onRemoteObservable = {
            flow {
                delay(100)
                emit(TestStatus("onRemoteObservable", "test"))
            }
        },
        onUpdateLocal = {
            delay(100)
            println("onUpdateLocal")
        },
        onLocalObservable = {
            flow {
                emit(TestStatus("getLocalData", "first"))
                delay(200)
                emit(TestStatus("changeLocal", "nice"))
            }
        }
    )

    macaroni.fetch { status, result ->
        println("status: $status, result: $result")
    }
}