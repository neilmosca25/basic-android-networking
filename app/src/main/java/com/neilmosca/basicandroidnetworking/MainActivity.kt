@file:OptIn(ExperimentalMaterial3Api::class)

package com.neilmosca.basicandroidnetworking

import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neilmosca.basicandroidnetworking.ui.theme.BasicAndroidNetworkingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasicAndroidNetworkingTheme() {
                MovieScreen()
            }
        }
    }
}

@Composable
fun MovieScreen(viewModel: MovieViewModel = viewModel()) {
    // Observe state from ViewModel
    val state by viewModel.viewState.collectAsState()

    // Pass state and callbacks to the stateless version
    MovieScreenContent(
        state = state,
        onIntent = { intent -> viewModel.handleIntent(intent) }
    )
}

@Composable
fun MovieScreenContent(
    state: MovieViewState,
    onIntent: (MovieIntent) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var movieToEdit by remember { mutableStateOf<Movie?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Movie Explorer") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (!state.isLoading && !state.hasError) {
                        movieToEdit = null
                        showDialog = true
                    }
                },
                // Visually dim the colors when loading (using standard 0.12/0.38 alpha)
                containerColor =
                    if (state.isLoading && !state.hasError) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.primaryContainer,
                contentColor =
                    if (state.isLoading && !state.hasError) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    else MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Movie")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }

                state.hasError -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Failed to load movies", color = MaterialTheme.colorScheme.error)
                        Button(onClick = { onIntent(MovieIntent.GetMovies) }) {
                            Text("Retry")
                        }
                    }
                }

                state.movies.isEmpty() -> {
                    Text("No movies found", modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    val movies = state.movies
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(movies, key = { it.id }) { movie ->
                            MovieItem(
                                movie = movie,
                                onEdit = {
                                    movieToEdit = movie
                                    showDialog = true
                                },
                                onDelete = {
                                    onIntent(MovieIntent.DeleteMovie(movie.id))
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {
            MovieDialog(
                movie = movieToEdit,
                onDismiss = { showDialog = false },
                onConfirm = { title, genre, year ->
                    if (movieToEdit == null) {
                        onIntent(
                            MovieIntent.CreateMovie(
                                Movie(
                                    title = title,
                                    genre = genre,
                                    year = year
                                )
                            )
                        )
                    } else {
                        onIntent(
                            MovieIntent.UpdateMovie(
                                movieToEdit!!.id,
                                movieToEdit!!.copy(
                                    title = title,
                                    genre = genre,
                                    year = year
                                )
                            )
                        )
                    }
                    showDialog = false
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDialog(
    movie: Movie? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit
) {
    var title by remember { mutableStateOf(movie?.title ?: "") }
    var genre by remember { mutableStateOf(movie?.genre ?: "") }
    var expanded by remember { mutableStateOf(false) }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear downTo 1900).toList()
    var selectedYear by remember { mutableStateOf(movie?.year ?: currentYear) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (movie == null) "Add Movie"
                else "Edit Movie"
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = selectedYear.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Year") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        years.take(50).forEach { year ->
                            DropdownMenuItem(
                                text = { Text(year.toString()) },
                                onClick = {
                                    selectedYear = year
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title, genre, selectedYear) },
                enabled = title.isNotBlank() && genre.isNotBlank()
            ) {
                Text(
                    if (movie == null) "Add"
                    else "Update"
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MovieItem(
    movie: Movie,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = movie.genre,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = movie.year.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun MovieScreenContentPreview() {
    val mockMovies = listOf(
        Movie(id = "1", title = "Inception", genre = "Sci-Fi", year = 2010),
        Movie(id = "2", title = "The Dark Knight", genre = "Action", year = 2008),
        Movie(id = "3", title = "Interstellar", genre = "Sci-Fi", year = 2014)
    )

    BasicAndroidNetworkingTheme() {
        MovieScreenContent(
            state = MovieViewState(
                movies = mockMovies.toMutableList(),
                isLoading = false,
                hasError = false
            ),
            onIntent = {} // Empty callback for preview
        )
    }
}

@Composable
@Preview
fun MovieScreenContentLoadingPreview() {
    BasicAndroidNetworkingTheme() {
        MovieScreenContent(
            state = MovieViewState(
                isLoading = true
            ),
            onIntent = {} // Empty callback for preview
        )
    }
}

@Composable
@Preview
fun MovieScreenContentHasErrorPreview() {
    BasicAndroidNetworkingTheme() {
        MovieScreenContent(
            state = MovieViewState(
                hasError = true
            ),
            onIntent = {} // Empty callback for preview
        )
    }
}