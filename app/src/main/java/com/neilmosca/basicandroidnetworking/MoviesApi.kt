package com.neilmosca.basicandroidnetworking
import kotlinx.serialization.json.Json
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MoviesApi {
    // Create
    @POST("api/movies")
    suspend fun createMovie(@Body movie: Movie): Movie

    // Read (List)
    @GET("api/movies")
    suspend fun getMovies(): List<Movie>

    // Read (Single)
    @GET("api/movies/{id}")
    suspend fun getMovie(@Path("id") id: String): Movie

    // Update
    @PUT("api/movies/{id}")
    suspend fun updateMovie(@Path("id") id: String, @Body movie: Movie): Movie

    // Delete
    @DELETE("api/movies/{id}")
    suspend fun deleteMovie(@Path("id") id: String): Response<Unit>

    companion object {

        // Connect to localhost, use 10.0.2.2 for emulator's loopback
        private const val BASE_URL = "http://10.0.2.2:5000/"

        val jsonConfig = Json { ignoreUnknownKeys = true }
        val contentType = okhttp3.MediaType.get("application/json")

        fun create(): MoviesApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(jsonConfig.asConverterFactory(contentType))
                .build()
                .create(MoviesApi::class.java)
        }
    }
}