package `in`.devcold.movica.data.remote.api

import `in`.devcold.movica.data.remote.ConnectivityException
import `in`.devcold.movica.data.remote.HttpIOException
import `in`.devcold.movica.utils.NetworkUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        return try {
            chain.proceed(chain.request())
        } catch (e: IOException) {
            throw if (NetworkUtils.isConnected()) {
                HttpIOException(chain.request().url.toString(), e::class.simpleName, e.localizedMessage)
            } else {
                ConnectivityException()
            }
        }
    }
}
