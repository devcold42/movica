package `in`.devcold.movica.data.remote.api

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import `in`.devcold.movica.data.remote.SkipSerialization
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object ApiBuilder {

    inline fun <reified T> build(): T {
        val client = OkHttpClient.Builder()
            .addInterceptor(NetworkErrorInterceptor())
            .protocols(listOf(Protocol.HTTP_1_1))
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        val baseUrl = T::class.annotations.first {
            it.annotationClass == BaseUrl::class
        } as BaseUrl

        val gson = GsonBuilder()
            .addSerializationExclusionStrategy(object : ExclusionStrategy {
                override fun shouldSkipField(f: FieldAttributes?): Boolean {
                    return f?.getAnnotation(SkipSerialization::class.java) != null
                }

                override fun shouldSkipClass(clazz: Class<*>?) = false
            })
            .create()

        val retrofit  = Retrofit.Builder()
            .baseUrl(baseUrl.url)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        return retrofit.create(T::class.java)
    }
}