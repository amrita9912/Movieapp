package com.example.movieapp

import androidx.room.*

@Dao
interface MovieDao {

    @Query("SELECT * from Watchlist ORDER BY time DESC")
    fun getAll() : List<MovieItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item:MovieItem)

    @Query("DELETE FROM Watchlist where time = :ts")
    suspend fun deleteId(ts: Long)



    @Query("SELECT * FROM Watchlist WHERE title = :title")
    fun getMovieByTitle(title: String): MovieItem?
}