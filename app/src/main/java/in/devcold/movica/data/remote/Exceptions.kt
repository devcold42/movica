package `in`.devcold.movica.data.remote

import java.io.IOException

// Device has no internet connection
class ConnectivityException : IOException("No connectivity")
// Device has internet connection, but request failed
class HttpIOException(val requestUrl: String, val exceptionName: String?, message: String?) : IOException(message)
// Got response, but with error codes (400, 401, 404, 500 etc.)
class ResponseErrorException(val requestUrl: String, val code: Int, message: String, val errorBody: String?) : Exception(message) {
    override fun toString() = "Code: $code, Message: $message, ErrorBody: $errorBody"
}
// Got response with code 200, but error "result" or "status" field in the body
class ErrorResponseException(message: String) : Exception(message)
