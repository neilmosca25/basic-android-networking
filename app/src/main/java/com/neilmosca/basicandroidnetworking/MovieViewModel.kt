package com.neilmosca.basicandroidnetworking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(MovieViewState())
    val viewState: StateFlow<MovieViewState> = _viewState
    private val api = MoviesApi.create()

    fun handleIntent(intent: MovieIntent) {
        when (intent) {
            is MovieIntent.GetMovies -> getMovies()
            is MovieIntent.GetMovie -> getMovie(intent.id)
            is MovieIntent.CreateMovie -> createMovie(intent.movie)
            is MovieIntent.UpdateMovie -> updateMovie(intent.id, intent.movie)
            is MovieIntent.DeleteMovie -> deleteMovie(intent.id)
        }
    }

    init {
        getMovies()
    }


    private fun getMovies() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, hasError = false) }
            try {
                val movies = api.getMovies()
                _viewState.update { it.copy(movies = movies.toMutableList(), isLoading = false, hasError = false) }
            } catch (e: Exception) {
                _viewState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    private fun getMovie(id: String) {
        viewModelScope.launch {
            try {
                val movie = api.getMovie(id)
                // Handle single movie result (e.g., update state or navigate)
            } catch (e: Exception) {
                _viewState.update { it.copy(hasError = true) }
            }
        }
    }

    private fun createMovie(movie: Movie) {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, hasError = false) }
            try {
                val createdMovie = api.createMovie(movie)
                if (createdMovie.id.isNotEmpty()) {
                    getMovies()
                } else {
                    _viewState.update { it.copy(isLoading = false, hasError = true) }
                }
            } catch (e: Exception) {
                _viewState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    private fun updateMovie(id: String, movie: Movie) {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, hasError = false) }
            try {
                val updatedMovie = api.updateMovie(id, movie)
                if (updatedMovie.id.isNotEmpty()) {
                    getMovies()
                } else {
                    _viewState.update { it.copy(isLoading = false, hasError = true) }
                }
            } catch (e: Exception) {
                _viewState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    private fun deleteMovie(id: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, hasError = false) }
            try {
                val response = api.deleteMovie(id)
                if (response.isSuccessful) {
                    getMovies()
                } else {
                    _viewState.update { it.copy(isLoading = false, hasError = true) }
                }
            } catch (e: Exception) {
                _viewState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }
}