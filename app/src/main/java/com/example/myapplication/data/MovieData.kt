package com.example.myapplication.data

import com.google.gson.annotations.SerializedName

data class MovieData(
    val id: Int,
    @SerializedName("title")
    val title:String,
    @SerializedName("poster_path")
    val poster:String,
    @SerializedName("vote_average")
    val rating:Double)
