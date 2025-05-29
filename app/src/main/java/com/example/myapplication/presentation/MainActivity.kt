package com.example.myapplication.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.MovieData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieListScreen()
        }
    }
}


@Preview
@Composable
fun MovieListScreen(viewModel: MovieViewModel = hiltViewModel()){
    val movies = remember {viewModel.movies}.collectAsLazyPagingItems()
    Log.d("inside MovieListScreen ", movies.itemCount.toString())


    LazyColumn {
        items(
            movies.itemCount,
            key = { index -> movies[index]?.id ?: 0 }
        ) { index ->
            val movie = movies[index]
            movie?.let {
                MovieItem(it)
            }
        }

        /*items(
            items = movies.itemSnapshotList.items,
            key = { it.id }
        ) { movie ->
            MovieItem(movie)
        }*/

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
                    Log.d("inside lazy column error ", e.error.toString())
                    item {
                        Text("Error: ${e.error.localizedMessage}", color = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                                .paddingFromBaseline(top=20.dp))
                    }
                }
            }
        }
    }
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

