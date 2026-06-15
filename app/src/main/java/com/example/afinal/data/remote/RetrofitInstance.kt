package com.example.afinal.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * RETROFIT INSTANCE — Singleton
 *
 * Creates and provides a single Retrofit client used throughout the app.
 * Using a singleton avoids creating a new HTTP client on every network call.
 */
object RetrofitInstance {

    // The base URL for all API calls. All @GET/@POST paths are appended to this.
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    /**
     * HttpLoggingInterceptor — prints every HTTP request and response to Logcat.
     * LEVEL.BODY shows headers + body (useful for debugging, disable in production).
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * OkHttpClient is the underlying HTTP engine used by Retrofit.
     * We add our logging interceptor to see all network traffic in Logcat.
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * Lazy initialization — the Retrofit instance is only created when first accessed.
     * GsonConverterFactory automatically converts JSON ↔ Kotlin data classes.
     */
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // JSON → Kotlin objects
            .build()
            .create(ApiService::class.java) // Creates the implementation of ApiService
    }
}
