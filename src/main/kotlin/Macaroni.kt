import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class Macaroni<T>(
    onRemoteObservable: suspend () -> Flow<T>,
    getLocalData: () -> T,
    onUpdateLocal: suspend (T) -> Unit
) {
    // remote
    // when us
    var onRemoteObservable: suspend () -> Flow<T>

    // local
    // The user must pass the function so that fetch can get the data it needs locally.
    var getLocalData: () -> T
    // The user must pass in a function that updates the local data as it is entered.
    var onUpdateLocal: suspend (T) -> Unit

    init {
        this.onRemoteObservable = onRemoteObservable
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
                onUpdateDateToLocal(data = data, onNext = onNext)
            }
        }.onFailure {
            onNext(Status.Error, getLocalData())
        }
    }

    private suspend inline fun onUpdateDateToLocal(data: T, onNext: (Status, T) -> Unit) {
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