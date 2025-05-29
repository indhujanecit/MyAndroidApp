package com.example.myapplication.data

import com.google.gson.annotations.SerializedName

data class MovieListResponse (
    val page: Int,
    @SerializedName("results")
    val result:List<MovieData>?)





