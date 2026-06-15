package com.neilmosca.basicandroidnetworking

import java.util.UUID

class MoviesApi private constructor() {

    // In-memory list to store movies
    private val movies = mutableListOf(
        Movie(id = UUID.randomUUID().toString(), title = "Inception", genre = "Sci-Fi", year = 2010),
        Movie(id = UUID.randomUUID().toString(), title = "Interstellar", genre = "Sci-Fi", year = 2014),
        Movie(id = UUID.randomUUID().toString(), title = "The Dark Knight", genre = "Action", year = 2008)
    )

    fun getMovies(): List<Movie> = movies.toList()

    fun getMovie(id: String): Movie {
        return movies.find { it.id == id } ?: throw Exception("Movie not found")
    }

    fun createMovie(movie: Movie): Movie {
        val newMovie = movie.copy(id = UUID.randomUUID().toString())
        movies.add(newMovie)
        return newMovie
    }

    suspend fun updateMovie(id: String, movie: Movie): Movie {
        val index = movies.indexOfFirst { it.id == id }
        if (index != -1) {
            movies[index] = movie.copy(id = id)
            return movies[index]
        }
        throw Exception("Movie not found")
    }

    suspend fun deleteMovie(id: String): MovieResponse {
        val removed = movies.removeIf { it.id == id }
        return MovieResponse(isSuccessful = removed)
    }

    // Helper class to match the 'isSuccessful' check in your ViewModel
    data class MovieResponse(val isSuccessful: Boolean)

    companion object {
        private var instance: MoviesApi? = null

        fun create(): MoviesApi {
            if (instance == null) {
                instance = MoviesApi()
            }
            return instance!!
        }
    }
}