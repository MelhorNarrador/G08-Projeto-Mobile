package pt.iade.lane.data.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    private val gson = GsonBuilder()
        .registerTypeAdapter(BigDecimal::class.java, BigDecimalSerializer())
        .create()


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        // 4. Passa o 'gson' personalizado para o Retrofit
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