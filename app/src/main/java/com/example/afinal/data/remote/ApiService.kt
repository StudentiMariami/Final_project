package com.example.afinal.data.remote

import com.example.afinal.model.Note
import retrofit2.Response
import retrofit2.http.*

/**
 * RETROFIT API SERVICE
 *
 * This interface defines the HTTP endpoints we can call.
 * Retrofit reads these annotations and generates the actual HTTP code.
 *
 * BASE URL is configured in RetrofitInstance.kt
 * Example API: JSONPlaceholder (https://jsonplaceholder.typicode.com) for testing
 */
interface ApiService {

    /**
     * @GET("posts") — sends an HTTP GET request to BASE_URL + "posts"
     * Response<List<Note>> wraps the result so we can check HTTP status codes
     */
    @GET("posts")
    suspend fun getNotes(): Response<List<Note>>

    /**
     * @POST("posts") — sends an HTTP POST with the Note as JSON body
     * @Body tells Retrofit to serialize the Note object to JSON
     */
    @POST("posts")
    suspend fun createNote(@Body note: Note): Response<Note>

    /**
     * @DELETE("posts/{id}") — sends HTTP DELETE
     * @Path("id") injects the note's id into the URL path
     */
    @DELETE("posts/{id}")
    suspend fun deleteNote(@Path("id") id: Int): Response<Unit>
}
