import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class Macaroni<T> {
    // remote
    lateinit var onRemoteObservable: (suspend () -> Flow<T>)

    // local
    lateinit var onLocalObservable: (suspend () -> Flow<T>)
    lateinit var getLocalData: (() -> T)
    lateinit var onUpdateLocal: (suspend (T) -> Unit)

    suspend fun fetch(onNext: (Status, T) -> Unit) {
        onNext(Status.Loading, getLocalData())
        onRemoteObservable().collect{data ->
            onUpdateLocal(data)

            onLocalObservable().collect { changedLocalData ->
                onNext(Status.Success, changedLocalData)
            }
        }
    }
}