package com.neilmosca.basicandroidnetworking

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class MoviesApi(private val client: HttpClient) {

    private val baseUrl = "http://10.0.2.2:5000/api/movies"

    suspend fun getMovies(): List<Movie> {
        return client.get(baseUrl).body()
    }

    suspend fun getMovie(id: String): Movie {
        return client.get("$baseUrl/$id").body()
    }

    suspend fun createMovie(movie: Movie): Movie {
        return client.post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(movie)
        }.body()
    }

    suspend fun updateMovie(id: String, movie: Movie): Movie {
        return client.put("$baseUrl/$id") {
            contentType(ContentType.Application.Json)
            setBody(movie)
        }.body()
    }

    suspend fun deleteMovie(id: String): MovieResponse {
        val response = client.delete("$baseUrl/$id")
        return MovieResponse(isSuccessful = response.status.isSuccess())
    }

    data class MovieResponse(val isSuccessful: Boolean)

    companion object {
        fun create(): MoviesApi {
            val client = HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                    })
                }
                install(Logging) {
                    level = LogLevel.BODY
                }
            }
            return MoviesApi(client)
        }
    }
}