package com.neilmosca.basicandroidnetworking

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class MoviesApi(private val client: HttpClient) {

    private val path = "api/movies"

    suspend fun getMovies(): List<Movie> {
        return client.get(path).body()
    }

    suspend fun getMovie(id: String): Movie {
        return client.get("$path/$id").body()
    }

    suspend fun createMovie(movie: Movie): Movie {
        return client.post(path) {
            contentType(ContentType.Application.Json)
            setBody(movie)
        }.body()
    }

    suspend fun updateMovie(id: String, movie: Movie): Movie {
        return client.put("$path/$id") {
            contentType(ContentType.Application.Json)
            setBody(movie)
        }.body()
    }

    suspend fun deleteMovie(id: String): MovieResponse {
        val response = client.delete("$path/$id")
        return MovieResponse(isSuccessful = response.status.isSuccess())
    }

    data class MovieResponse(val isSuccessful: Boolean)

    companion object {

        // Connect to localhost, use 10.0.2.2 for emulator's loopback
        private const val BASE_URL = "http://10.0.2.2:5000/"

        fun create(): MoviesApi {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                    })
                }
                install(Logging) {
                    level = LogLevel.BODY
                }

                defaultRequest {
                    url(BASE_URL)
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                }
            }
            return MoviesApi(client)
        }
    }
}