package com.example.myapplication.domain

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myapplication.data.ApiClient
import com.example.myapplication.data.MovieData
import javax.inject.Inject

class MovieListPagingSrc @Inject constructor(private val apiClient: ApiClient) :PagingSource<Int,MovieData>(){
    override fun getRefreshKey(state: PagingState<Int, MovieData>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieData> {
        return try {
            val page = params.key ?: 1
            val response = apiClient.fetchMovieList(page)
            val movies = response.result ?: emptyList()
            Log.d("PagingSource", "Movies: ${response.result}")
            LoadResult.Page(
                data = movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if ( movies.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}