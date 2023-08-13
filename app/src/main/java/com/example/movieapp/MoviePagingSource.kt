package com.example.movieapp

import androidx.paging.PagingSource
import androidx.paging.PagingState

class MoviePagingSource(
    private val movieApi: MovieApi,
    private val category: String
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: 1
            val response = when (category) {
                "popular" -> movieApi.getPopularMovies(page)
                "upcoming" -> movieApi.getUpcomingMovies(page)
                "trending" -> movieApi.getTrendingMovies(page)
                "top-rated" -> movieApi.getTopRatedMovies(page)
                else -> throw IllegalArgumentException("Invalid category")
            }

            if (response.isSuccessful) {
                val movies = response.body()?.results ?: emptyList()
                val nextPage = if (movies.isEmpty()) null else page + 1
                LoadResult.Page(data = movies, prevKey = null, nextKey = nextPage)
            } else {
                LoadResult.Error(Throwable("Failed to load data"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

   }
}
