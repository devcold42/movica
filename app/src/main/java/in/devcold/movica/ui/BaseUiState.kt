package `in`.devcold.movica.ui

import androidx.annotation.StringRes
import `in`.devcold.movica.utils.ErrorCodes

sealed class BaseUiState {
    object Idle : BaseUiState()
    object Loading : BaseUiState()

    sealed class Error(open val errorCode: ErrorCodes) : BaseUiState() {
        data class Msg(
            override val errorCode: ErrorCodes,
            val errorMessage: String
        ) : Error(errorCode)

        data class MsgId(
            override val errorCode: ErrorCodes,
            @StringRes
            val errorMessageId: Int
        ) : Error(errorCode)
    }
}