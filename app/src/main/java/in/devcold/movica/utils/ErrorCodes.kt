package `in`.devcold.movica.utils

enum class ErrorCodes(val code: String) {
    API_GENERIC("A001"),
    API_ERROR_RESPONSE("A002"),
    API_RESPONSE_ERROR("A003"),
    API_CONNECTIVITY_ERROR("A004"),
    API_NETWORK_FAILURE("A005"),
}