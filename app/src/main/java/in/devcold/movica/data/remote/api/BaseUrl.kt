package `in`.devcold.movica.data.remote.api

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class BaseUrl(val url: String) {
    companion object {
        const val NO_BASE_URL = "https://127.0.0.1/"
    }
}
