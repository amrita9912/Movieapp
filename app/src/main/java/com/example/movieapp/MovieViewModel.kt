package com.example.movieapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {


    fun getPopularMovies(): Flow<PagingData<Movie>> {
        return Pager(PagingConfig(pageSize = 20)) {
            MoviePagingSource(MovieApi.getInstance(), "popular")
        }.flow.cachedIn(viewModelScope)
    }

    fun getUpcomingMovies(): Flow<PagingData<Movie>> {
        return Pager(PagingConfig(pageSize = 20)) {
            MoviePagingSource(MovieApi.getInstance(), "upcoming")
        }.flow.cachedIn(viewModelScope)
    }

    fun getTrendingMovies(): Flow<PagingData<Movie>> {
        return Pager(PagingConfig(pageSize = 20)) {
            MoviePagingSource(MovieApi.getInstance(), "trending")
        }.flow.cachedIn(viewModelScope)
    }

    fun getTopRatedMovies(): Flow<PagingData<Movie>> {
        return Pager(PagingConfig(pageSize = 20)) {
            MoviePagingSource(MovieApi.getInstance(), "top_rated")
        }.flow.cachedIn(viewModelScope)
    }
}
