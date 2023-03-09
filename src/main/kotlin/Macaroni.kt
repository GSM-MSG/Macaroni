import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class Macaroni<T>(
    onRemoteObservable: suspend () -> Flow<T>,
    onLocalObservable: suspend () -> Flow<T>,
    getLocalData: () -> T,
    onUpdateLocal: suspend (T) -> Unit
) {
    // remote
    var onRemoteObservable: suspend () -> Flow<T>

    // local
    var onLocalObservable: suspend () -> Flow<T>
    var getLocalData: () -> T
    var onUpdateLocal: suspend (T) -> Unit

    init {
        this.onRemoteObservable = onRemoteObservable
        this.onLocalObservable = onLocalObservable
        this.getLocalData = getLocalData
        this.onUpdateLocal = onUpdateLocal
    }

    suspend fun fetch(onNext: (Status, T) -> Unit) {
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