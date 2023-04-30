import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first

class MacaroniFlow<T> constructor(
    onRemoteObservable: suspend () -> Flow<T>,
    onUpdateLocal: suspend (T) -> Unit,
    onLocalObservable: suspend () -> Flow<T>,
) {

    private var onRemoteObservable: suspend () -> Flow<T>
    private var onUpdateLocal: suspend (T) -> Unit
    private var onLocalObservable: suspend () -> Flow<T>

    init {
        this.onRemoteObservable = onRemoteObservable
        this.onUpdateLocal = onUpdateLocal
        this.onLocalObservable = onLocalObservable
    }

    suspend fun fetch(onNext: (MacaroniStatus, T) -> Unit) {
        val localData = onLocalObservable().first()
        onLocalObservable()
            .drop(1)
            .collect { data ->
                onNext(Success, data)
            }
        try {
            runCatching {
                onNext(Loading, localData)
                onRemoteObservable()
            }.onSuccess {
                it.collect { data ->
                    if (localData != data) {
                        onUpdateLocal(data)
                    } else {
                        onNext(Success, data)
                    }
                }
            }.onFailure {
                onNext(Error, localData)
            }
        } catch (e: Exception) {
            onNext(Error, localData)
        }
    }
}