package com.neilmosca.basicandroidnetworking

import java.util.Collections.emptyList

data class MovieViewState(
   val movies: MutableList<Movie> = emptyList(),
   val isLoading: Boolean = false,
   val hasError: Boolean = false
)
