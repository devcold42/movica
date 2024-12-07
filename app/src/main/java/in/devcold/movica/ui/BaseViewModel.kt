package `in`.devcold.movica.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.devcold.movica.data.remote.ConnectivityException
import `in`.devcold.movica.data.remote.ErrorResponseException
import `in`.devcold.movica.data.remote.HttpIOException
import `in`.devcold.movica.data.remote.ResponseErrorException
import `in`.devcold.movica.utils.DataCallback
import `in`.devcold.movica.utils.ErrorCodes
import `in`.devcold.movica.utils.ExceptionCallback
import `in`.devcold.movica.utils.ResultCallback
import `in`.devcold.movica.utils.SimpleCallback
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

abstract class BaseViewModel : ViewModel() {

    protected val _baseUiState = MutableStateFlow<BaseUiState>(BaseUiState.Idle)

    protected fun showErrorMessage(errorCode: ErrorCodes, message: String) {
        _baseUiState.update {
            BaseUiState.Error.Msg(errorCode, message)
        }
    }

    protected fun showErrorMessage(errorCode: ErrorCodes, @StringRes messageId: Int) {
        _baseUiState.update {
            BaseUiState.Error.MsgId(errorCode, messageId)
        }
    }

    // This is only for UI reaction to API call result, no need to log errors etc.
    // Check out safeApiCall method to get a better idea
    protected fun <T> handleApiCallResult(
        result: Result<T>,
        success: DataCallback<T> = {},
        // When it doesn't matter what kind of error happened
        failure: SimpleCallback = {},
        // When response code is successful, but body has an error
        errorResponse: ExceptionCallback<ErrorResponseException> = ExceptionCallback { true },
        // When response code is not successful
        responseError: ExceptionCallback<ResponseErrorException> = ExceptionCallback { true },
        // When connection is weak or server is unavailable
        networkError: ExceptionCallback<HttpIOException> = ExceptionCallback { true },
        // When there's no connection
        connectivityError: ExceptionCallback<ConnectivityException> = ExceptionCallback { true },
        // When unknown/unsupported error happened
        exception: ExceptionCallback<Throwable> = ExceptionCallback { true },
        // When you need to ensure something is called in the very end, no matter if it's success or failure
        finally: ResultCallback<T> = {},
        // ExceptionCallback's return value defines whether default error message will be shown
        // Use this parameter as a shortcut to turn all of them off
        showDefaultErrorMessage: Boolean = true,
        // Whether to use e.message field for non-overridden callbacks.
        // Generic API exception message is used by default or if e.message is null or blank
        useExceptionMessage: Boolean = false
    ) {
        if(result.isSuccess)
            success(result.getOrThrow())
        else {
            val e = result.exceptionOrNull()!!
            val message = if (useExceptionMessage && !e.message.isNullOrBlank())
                e.message!!
            else
                ""
            
            failure()

            when(e) {
                is ErrorResponseException -> {
                    if (showDefaultErrorMessage && errorResponse(e)) {
                        if (message.isBlank())
                            showErrorMessage(ErrorCodes.API_ERROR_RESPONSE, R.string.network_generic_exception_message)
                        else
                            showErrorMessage(ErrorCodes.API_ERROR_RESPONSE, message)
                    }
                }
                is ResponseErrorException -> {
                    if (showDefaultErrorMessage && responseError(e)) {
                        if (message.isBlank())
                            showErrorMessage(ErrorCodes.API_RESPONSE_ERROR, R.string.network_generic_exception_message)
                        else
                            showErrorMessage(ErrorCodes.API_RESPONSE_ERROR, message)
                    }
                }
                is HttpIOException -> {
                    if (showDefaultErrorMessage && networkError(e)) {
                        if (message.isBlank())
                            showErrorMessage(ErrorCodes.API_NETWORK_FAILURE, R.string.network_generic_exception_message)
                        else
                            showErrorMessage(ErrorCodes.API_NETWORK_FAILURE, message)
                    }
                }
                is ConnectivityException -> {
                    if (showDefaultErrorMessage && connectivityError(e)) {
                        if (message.isBlank())
                            showErrorMessage(ErrorCodes.API_CONNECTIVITY_ERROR, R.string.network_generic_exception_message)
                        else
                            showErrorMessage(ErrorCodes.API_CONNECTIVITY_ERROR, message)
                    }
                }
                is CancellationException -> {
                    // Ignore
                }
                else -> {
                    if (showDefaultErrorMessage && exception(e)) {
                        if (message.isBlank())
                            showErrorMessage(ErrorCodes.API_GENERIC, R.string.network_generic_exception_message)
                        else
                            showErrorMessage(ErrorCodes.API_GENERIC, message)
                    }
                }
            }
        }

        finally(result)
    }

    protected fun <T> listUiStateFlow(listStream: Flow<List<T>>): StateFlow<ListUiState<T>> {
        return combine(_baseUiState, listStream) { baseUiState, list ->
            ListUiState(baseUiState, list)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ListUiState()
        )
    }
}