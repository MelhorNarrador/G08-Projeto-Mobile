package pt.iade.lane.data.network

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal

object RetrofitClient {

    /*private const val BASE_URL = "http://192.168.1.228:8080/"*/ //Casa fixo Pedro
   /* private const val BASE_URL = "http://10.208.196.51:8080/"*/ //Faculade
    private const val BASE_URL = "http://192.168.1.92:8080/" //Casa Pedro Wifi
 

    var authToken: String? = null

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()

        authToken?.let { token ->
            builder.header("Authorization", "Bearer $token")
        }

        val newRequest = builder.build()
        chain.proceed(newRequest)
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .build()

    private val gson = GsonBuilder()
        .registerTypeAdapter(BigDecimal::class.java, BigDecimalSerializer())
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val apiService: LaneAppAPIService by lazy {
        retrofit.create(LaneAppAPIService::class.java)
    }
}

private class BigDecimalSerializer : com.google.gson.JsonSerializer<BigDecimal> {
    override fun serialize(
        src: BigDecimal?,
        typeOfSrc: java.lang.reflect.Type?,
        context: com.google.gson.JsonSerializationContext?
    ): com.google.gson.JsonElement {
        return com.google.gson.JsonPrimitive(src?.toPlainString())
    }
}