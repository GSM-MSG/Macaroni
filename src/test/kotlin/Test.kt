import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

suspend fun main() {
    val macaroni = Macaroni(
        onUpdateLocal = {
            throw Exception()
//            delay(1000)
//            println(it)
        },
        onRemoteObservable = {
//            throw Exception()
            delay(500)
            flow {
                emit("onRemoteObservable")
            }
        },
        onRemoteFailure = {
            println(it)
        },
        getLocalData = {
            "getLocalData"
        }
    )
    coroutineScope {
        macaroni.fetch { status, String ->
            println("$status: status, String: $String")
        }
    }
}
