package com.neilmosca.basicandroidnetworking

sealed class MovieIntent {
    // Create
    data class CreateMovie(val movie: Movie) : MovieIntent()

    // Read
    object GetMovies : MovieIntent()
    data class GetMovie(val id: String) : MovieIntent()

    // Update
    data class UpdateMovie(val id: String, val movie: Movie) : MovieIntent()

    // Delete
    data class DeleteMovie(val id: String) : MovieIntent()
}