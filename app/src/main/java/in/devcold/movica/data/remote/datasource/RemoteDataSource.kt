package `in`.devcold.movica.data.remote.datasource

import `in`.devcold.movica.data.remote.ConnectivityException
import `in`.devcold.movica.data.remote.HttpIOException
import `in`.devcold.movica.data.remote.ResponseErrorException
import `in`.devcold.movica.utils.AppMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.Buffer
import retrofit2.Response
import timber.log.Timber
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

abstract class RemoteDataSource {

    protected suspend fun <R> safeApiCall(request: suspend () -> Response<R>): Result<R> {
        val response = try {
            request()
        } catch(e: Exception) {
            // Log only critical errors caused by bugs, not by device conditions or user actions
            if (e !is HttpIOException && e !is CancellationException && e !is ConnectivityException)
                Timber.e(e)

            Timber.i("Exception during call: ${e::class.simpleName}, ${e.message}")
            return Result.failure(e)
        }

        Timber.i("Call: ${response.raw().request.url}")
        Timber.i("Request body: ${response.requestBody}")

        return if(response.isSuccessful)
            Result.success(response.body()!!)
        else {
            val exception = ResponseErrorException(
                response.raw().request.url.toString(),
                response.code(),
                response.raw().message,
                response.errorBody()?.string()
            )
            Timber.e(
                exception,
                "url: ${exception.requestUrl}, code: ${exception.code}, message: ${exception.message}"
            )
            Result.failure(exception)
        }
    }

    // Extensions

    protected fun File.asFormData(mediaType: AppMediaType, file: File, fieldName: String = ""): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            fieldName,
            file.name,
            file.asRequestBody(mediaType.mime.toMediaTypeOrNull())
        )
    }

    protected val <T> Response<T>.requestBody: String?; get() {
        return try {
            val copy = raw().request.newBuilder().build()
            val buffer = Buffer()
            copy.body!!.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}