import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class Macaroni<T>(
    onRemoteObservable: suspend () -> Flow<T>,
    onLocalObservable: suspend () -> Flow<T>,
    getLocalData: () -> T,
    onUpdateLocal: suspend (T) -> Unit
) {
    // remote
    // when us
    var onRemoteObservable: suspend () -> Flow<T>

    // local
    var onLocalObservable: suspend () -> Flow<T>
    // The user must pass the function so that fetch can get the data it needs locally.
    var getLocalData: () -> T
    // The user must pass in a function that updates the local data as it is entered.
    var onUpdateLocal: suspend (T) -> Unit

    init {
        this.onRemoteObservable = onRemoteObservable
        this.onLocalObservable = onLocalObservable
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
                onUpdateLocal(data)
            }
        }.onFailure {
            onNext(Status.Error, getLocalData())
        }

        // when get date from local
        runCatching {
            onLocalObservable()
        }.onSuccess {
            it.collect {changedLocalData ->
                onNext(Status.Success, changedLocalData)
            }
        }.onFailure {
            onNext(Status.Error, getLocalData())
        }

    }
}