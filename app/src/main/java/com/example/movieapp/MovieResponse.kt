package com.example.movieapp


import com.google.gson.annotations.SerializedName

data class MovieResponse (

    @SerializedName("results") var results      : ArrayList<Movie> = arrayListOf(),
    @SerializedName("page") val page: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

