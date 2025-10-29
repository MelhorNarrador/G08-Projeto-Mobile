package pt.iade.lane.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 1. ALTERA ISTO: Coloca aqui a URL base da tua API
    // Ex: "http://192.168.1.10:8080/api/" ou "https://api.tuadominio.com/"
    // Tem de terminar sempre com uma barra "/"!
    private const val BASE_URL = "http://10.0.2.2:8080/"


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: LaneAppAPIService by lazy {
        retrofit.create(LaneAppAPIService::class.java)
    }
}