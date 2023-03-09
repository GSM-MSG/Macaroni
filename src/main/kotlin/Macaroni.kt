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
        onNext(Status.Loading, getLocalData())
        onRemoteObservable().collect { data ->
            onUpdateLocal(data)
        }.runCatching {
            onNext(Status.Error, getLocalData())
        }

        onLocalObservable().collect { changedLocalData ->
            onNext(Status.Success, changedLocalData)
        }.runCatching {
            onNext(Status.Error, getLocalData())
        }
    }
}