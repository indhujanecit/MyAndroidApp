package com.example.myapplication.data


import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.myapplication.domain.MovieListPagingSrc
import javax.inject.Inject

class Repository @Inject constructor(private val apiClient: ApiClient) {

    fun getMovies() = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { MovieListPagingSrc(apiClient) }
    ).flow
}