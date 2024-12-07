package `in`.devcold.movica.utils

typealias SimpleCallback = () -> Unit
typealias DataCallback<T> = (T) -> Unit
typealias ReturnCallback<R> = () -> R
typealias ResultCallback<T> = (Result<T>) -> Unit
fun interface ExceptionCallback<T : Throwable> {
    operator fun invoke(exception: T): Boolean
}
typealias SuspendCallback = suspend () -> Unit
typealias SuspendDataCallback<T> = suspend (T) -> Unit