import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class Macaroni<T>(
    onRemoteObservable: suspend () -> Flow<T>,
    getLocalData: suspend () -> T,
    onUpdateLocal: suspend (T) -> Unit,
    onRemoteFailure: (Throwable) -> Unit
) {
    // remote
    //  'onRemoteObservable' should pass the logic
    //  for observing the request to the function
    //  when it receives a response from the remote.
    var onRemoteObservable: suspend () -> Flow<T>

    var onRemoteFailure: (Throwable) -> Unit

    // local
    // The user must pass the function so that fetch can get the data it needs locally.
    var getLocalData: suspend () -> T

    // The user must pass in a function that updates the local data as it is entered.
    var onUpdateLocal: suspend (T) -> Unit

    init {
        this.onRemoteObservable = onRemoteObservable
        this.onRemoteFailure = onRemoteFailure
        this.getLocalData = getLocalData
        this.onUpdateLocal = onUpdateLocal
    }

    suspend fun fetch(onNext: (Status, T) -> Unit) {
        // when request from remote
        runCatching {
            onNext(Status.Loading, getLocalData())
            onRemoteObservable()
        }.onSuccess {
            it.collect { data ->
                onUpdateDataToLocal(data = data, onNext = onNext)
            }
        }.onFailure {
            onNext(Status.Error, getLocalData())
            onRemoteFailure(it)
        }
    }

    private suspend inline fun onUpdateDataToLocal(data: T, onNext: (Status, T) -> Unit) {
        runCatching {
            onUpdateLocal(data)
        }.onSuccess {
            onNext(Status.Success, getLocalData())
        }.onFailure {
            onNext(Status.Error, getLocalData())
            throw it
        }
    }
}