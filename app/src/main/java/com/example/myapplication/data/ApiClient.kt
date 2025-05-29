package com.example.myapplication.data

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiClient {

    @GET("discover/movie")
    suspend fun fetchMovieList(@Query("page") page: Int):MovieListResponse
}