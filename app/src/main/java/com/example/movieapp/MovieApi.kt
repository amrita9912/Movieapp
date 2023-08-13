package com.example.movieapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.Response
import retrofit2.http.Query

interface MovieApi {

        @GET("movie/popular")
        suspend fun getPopularMovies(
            @Query("page") page: Int,
            @Header("Authorization") auth : String = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZWZlMzU3YmM5YjA5Yzg4YmMzZDQwMzIzYmEyMDVhMiIsInN1YiI6IjY0YzIyOGFkMmYxYmUwMDEyZDkxYTQ1OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.k89qa3h3pzJT0e-oDbWJHfCBflBdUqt30mlqi27kB4Y"
        ): Response<MovieResponse>

        @GET("movie/upcoming")
        suspend fun getUpcomingMovies(
            @Query("page") page: Int,
            @Header("Authorization") auth : String = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZWZlMzU3YmM5YjA5Yzg4YmMzZDQwMzIzYmEyMDVhMiIsInN1YiI6IjY0YzIyOGFkMmYxYmUwMDEyZDkxYTQ1OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.k89qa3h3pzJT0e-oDbWJHfCBflBdUqt30mlqi27kB4Y"
        ): Response<MovieResponse>

        @GET("trending/movie/day")
        suspend fun getTrendingMovies(
            @Query("page") page: Int,
            @Header("Authorization") auth : String = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZWZlMzU3YmM5YjA5Yzg4YmMzZDQwMzIzYmEyMDVhMiIsInN1YiI6IjY0YzIyOGFkMmYxYmUwMDEyZDkxYTQ1OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.k89qa3h3pzJT0e-oDbWJHfCBflBdUqt30mlqi27kB4Y"
        ): Response<MovieResponse>

        @GET("movie/top_rated")
        suspend fun getTopRatedMovies(
            @Query("page") page: Int,
            @Header("Authorization") auth : String = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZWZlMzU3YmM5YjA5Yzg4YmMzZDQwMzIzYmEyMDVhMiIsInN1YiI6IjY0YzIyOGFkMmYxYmUwMDEyZDkxYTQ1OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.k89qa3h3pzJT0e-oDbWJHfCBflBdUqt30mlqi27kB4Y"
        ): Response<MovieResponse>



    companion object {
        var apiService: MovieApi? = null

        fun getInstance(): MovieApi {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl("https://api.themoviedb.org/3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(MovieApi::class.java)
            }
            return apiService!!
        }
    }}


/*
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Header("Authorization") auth : String = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZWZlMzU3YmM5YjA5Yzg4YmMzZDQwMzIzYmEyMDVhMiIsInN1YiI6IjY0YzIyOGFkMmYxYmUwMDEyZDkxYTQ1OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.k89qa3h3pzJT0e-oDbWJHfCBflBdUqt30mlqi27kB4Y"
    ) : Response

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Header("Authorization") auth : String = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZWZlMzU3YmM5YjA5Yzg4YmMzZDQwMzIzYmEyMDVhMiIsInN1YiI6IjY0YzIyOGFkMmYxYmUwMDEyZDkxYTQ1OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.k89qa3h3pzJT0e-oDbWJHfCBflBdUqt30mlqi27kB4Y"
    ) : Response

    @GET("trending/movie/day")
    suspend fun getTrendingMovies(
        @Header("Authorization") auth : String = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZWZlMzU3YmM5YjA5Yzg4YmMzZDQwMzIzYmEyMDVhMiIsInN1YiI6IjY0YzIyOGFkMmYxYmUwMDEyZDkxYTQ1OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.k89qa3h3pzJT0e-oDbWJHfCBflBdUqt30mlqi27kB4Y"
    ) : Response

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Header("Authorization") auth : String = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZWZlMzU3YmM5YjA5Yzg4YmMzZDQwMzIzYmEyMDVhMiIsInN1YiI6IjY0YzIyOGFkMmYxYmUwMDEyZDkxYTQ1OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.k89qa3h3pzJT0e-oDbWJHfCBflBdUqt30mlqi27kB4Y"
    ) : Response

    companion object{
        var apiService : MovieApi ?= null

        fun getInstance() : MovieApi{
            if(apiService==null){
                apiService = Retrofit.Builder()
                    .baseUrl("https://api.themoviedb.org/3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(MovieApi::class.java)
            }
            return apiService!!
        }
    }
}*/