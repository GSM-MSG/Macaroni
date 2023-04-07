import kotlinx.coroutines.flow.Flow

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

    suspend fun fetch(onNext: (MacaroniStatus, T) -> Unit) {
        // when request from remote
        try {
            runCatching {
                onNext(Loading, getLocalData())
                onRemoteObservable()
            }.onSuccess {
                it.collect { data ->
                    onUpdateDataToLocal(data = data, onNext = onNext)
                }
            }.onFailure {
                onNext(Error, getLocalData())
                onRemoteFailure(it)
            }
        } catch (e: Exception) {
            onNext(Error, getLocalData())
        }

    }

    private suspend inline fun onUpdateDataToLocal(data: T, onNext: (MacaroniStatus, T) -> Unit) {
        runCatching {
            onUpdateLocal(data)
        }.onSuccess {
            onNext(Success, getLocalData())
        }.onFailure {
            onNext(Error, getLocalData())
            it.printStackTrace()
        }
    }
}