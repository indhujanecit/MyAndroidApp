package com.example.myapplication.presentation

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.MovieData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MovieListScreen()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.R)
@Preview
@Composable
fun MovieListScreen(viewModel: MovieViewModel = hiltViewModel()){
    val movies = remember {viewModel.movies}.collectAsLazyPagingItems()
    Log.d("inside MovieListScreen ", movies.itemCount.toString())

    val activity = LocalContext.current as Activity
    val window = activity.window
    // Set status bar color and appearance
    SideEffect {
        window.statusBarColor = Color.White.toArgb()  // Status bar background
        window.navigationBarColor = Color.White.toArgb() // Nav bar background

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true    // Dark icons for status bar
        insetsController.isAppearanceLightNavigationBars = true // Dark icons for nav bar
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            TopAppBar(
                title = { Text("Movie List") },
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(
                    movies.itemCount,
                    key = { index -> movies[index]?.id ?: 0 }
                ) { index ->
                    val movie = movies[index]
                    movie?.let {
                        MovieItem(it)
                    }
                }

                movies.apply {
                    when {
                        loadState.append is LoadState.Loading -> {
                            item { LoadingItem() }
                        }

                        loadState.refresh is LoadState.Loading -> {
                            item { LoadingItem() }
                        }

                        loadState.refresh is LoadState.Error -> {
                            val e = loadState.refresh as LoadState.Error
                            Log.d("lazy column error", e.error.toString())
                            item {
                                Text(
                                    "Error: ${e.error.localizedMessage}",
                                    color = Color.Red,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                        .paddingFromBaseline(top = 20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun LoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .paddingFromBaseline(top=20.dp)


    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}



@Composable
fun MovieItem(movie: MovieData) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .paddingFromBaseline(top=20.dp, bottom = 20.dp)
    ) {
        val imageUrl = "https://image.tmdb.org/t/p/w500${movie.poster}"
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = movie.title,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(movie.title, style = MaterialTheme.typography.titleMedium)
            Text("⭐ ${movie.rating}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

