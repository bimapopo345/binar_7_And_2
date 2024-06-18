package com.gpillaca.upcomingmovies.framework.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM MovieDB")
    fun getAll(): Flow<List<MovieDB>>

    @Query("SELECT * FROM MovieDB")
    fun getAllMovies(): List<MovieDB>

    @Query("SELECT * FROM MovieDB WHERE id = :id")
    fun findByID(id: Int): Flow<MovieDB>

    @Query("SELECT COUNT(id) FROM MovieDB")
    suspend fun movieCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieDB>)

    @Update
    suspend fun updateMovie(movie: MovieDB)

}
