package com.example.movieapp
import com.google.gson.annotations.SerializedName

data class Movie (

    @SerializedName("backdrop_path"     ) var backdropPath     : String?  = null,
    @SerializedName("id"                ) var id               : Int?     = null,
    @SerializedName("original_title"    ) var originalTitle    : String?  = null,
    @SerializedName("overview"          ) var overview         : String?  = null,
    @SerializedName("poster_path"       ) var posterPath       : String?  = null,
    @SerializedName("release_date"      ) var releaseDate      : String?  = null,
    @SerializedName("title"             ) var title            : String?  = null,
    @SerializedName("vote_average"      ) var voteAverage      : Double?  = null,

)